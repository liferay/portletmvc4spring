/**
 * Copyright (c) 2000-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.portletmvc4spring.bind;

import jakarta.portlet.ClientDataRequest;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.Part;
import org.springframework.beans.MutablePropertyValues;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

import org.springframework.web.bind.WebDataBinder;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.liferay.portletmvc4spring.util.PortletUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Special {@link org.springframework.validation.DataBinder} to perform data binding from portlet request parameters to
 * JavaBeans, including support for multipart files.
 *
 * <p>See the DataBinder/WebDataBinder superclasses for customization options, which include specifying allowed/required
 * fields, and registering custom property editors.
 *
 * <p>Can also be used for manual data binding in custom web controllers: for example, in a plain Portlet Controller
 * implementation. Simply instantiate a PortletRequestDataBinder for each binding process, and invoke {@code bind} with
 * the current PortletRequest as argument:
 *
 * <pre class="code">
   MyBean myBean = new MyBean();
   // apply binder to custom target object
   PortletRequestDataBinder binder = new PortletRequestDataBinder(myBean);
   // register custom editors, if desired
   binder.registerCustomEditor(...);
   // trigger actual binding of request parameters
   binder.bind(request);
   // optionally evaluate binding errors
   Errors errors = binder.getErrors();
   ...</pre>
 *
 * @author  Juergen Hoeller
 * @author  John A. Lewis
 * @since   2.0
 * @see     #bind(jakarta.portlet.PortletRequest)
 * @see     #registerCustomEditor
 * @see     #setAllowedFields
 * @see     #setRequiredFields
 * @see     #setFieldMarkerPrefix
 */
public class PortletRequestDataBinder extends WebDataBinder {

	/**
	 * Create a new PortletRequestDataBinder instance, with default object name.
	 *
	 * @param  target  the target object to bind onto (or {@code null} if the binder is just used to convert a plain
	 *                 parameter value)
	 *
	 * @see    #DEFAULT_OBJECT_NAME
	 */
	public PortletRequestDataBinder(Object target) {
		super(target);
	}

	/**
	 * Create a new PortletRequestDataBinder instance.
	 *
	 * @param  target      the target object to bind onto (or {@code null} if the binder is just used to convert a plain
	 *                     parameter value)
	 * @param  objectName  the name of the target object
	 */
	public PortletRequestDataBinder(Object target, String objectName) {
		super(target, objectName);
	}

	/**
	 * Bind the parameters of the given request to this binder's target, also binding multipart files in case of a
	 * multipart request.
	 *
	 * <p>This call can create field errors, representing basic binding errors like a required field (code "required"),
	 * or type mismatch between value and bean property (code "typeMismatch").
	 *
	 * <p>Multipart files are bound via their parameter name, just like normal HTTP parameters: i.e. "uploadedFile" to
	 * an "uploadedFile" bean property, invoking a "setUploadedFile" setter method.
	 *
	 * <p>The type of the target property for a multipart file can be MultipartFile, byte[], or String. The latter two
	 * receive the contents of the uploaded file; all metadata like original file name, content type, etc are lost in
	 * those cases.
	 *
	 * @param  request  request with parameters to bind (can be multipart)
	 *
	 * @see    com.liferay.portletmvc4spring.multipart.MultipartActionRequest
	 * @see    org.springframework.web.multipart.MultipartFile
	 * @see    #bind(org.springframework.beans.PropertyValues)
	 */
	public void bind(PortletRequest request) {
		MutablePropertyValues mpvs = new PortletRequestParameterPropertyValues(request);
		MultipartRequest multipartRequest = PortletUtils.getNativeRequest(request, MultipartRequest.class);

		if (multipartRequest != null) {
			bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
		}

		doBind(mpvs);
	}

	/**
	 * Treats errors as fatal.
	 *
	 * <p>Use this method only if it's an error if the input isn't valid. This might be appropriate if all input is from
	 * dropdowns, for example.
	 *
	 * @throws  PortletRequestBindingException  subclass of PortletException on any binding problem
	 */
	public void closeNoCatch() throws PortletRequestBindingException {

		if (getBindingResult().hasErrors()) {
			throw new PortletRequestBindingException("Errors binding onto object '" +
				getBindingResult().getObjectName() + "'", new BindException(getBindingResult()));
		}
	}

	public void construct(PortletRequest request) {
		construct(createValueResolver(request));
	}

	protected PortletRequestValueResolver createValueResolver(PortletRequest request) {
		return new PortletRequestValueResolver(request);
	}

	private static boolean isFormDataPost(ClientDataRequest request) {
		return StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.MULTIPART_FORM_DATA_VALUE);
	}

	protected class PortletRequestValueResolver implements ValueResolver {

		private final PortletRequest request;

		@Nullable
		private Set<String> parameterNames;

		protected PortletRequestValueResolver(PortletRequest request) {
			this.request = request;
		}

		protected PortletRequest getRequest() {
			return this.request;
		}

		@Nullable
		@Override
		public final Object resolveValue(String name, Class<?> paramType) {
			Object value = getRequestParameter(name, paramType);
			if (value == null) {
				value = PortletRequestDataBinder.this.resolvePrefixValue(name, paramType, this::getRequestParameter);
			}
			if (value == null) {
				try {
					value = getMultipartValue(name);
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			return value;
		}

		@Nullable
		protected Object getRequestParameter(String name, Class<?> type) {
			Object value = this.request.getParameterValues(name);
			return (ObjectUtils.isArray(value) && Array.getLength(value) == 1 ? Array.get(value, 0) : value);
		}

		@Nullable
		private Object getMultipartValue(String name) throws PortletException, IOException {
			MultipartRequest multipartRequest = PortletUtils.getNativeRequest(request, MultipartRequest.class);
			ClientDataRequest clientDataRequest = PortletUtils.getNativeRequest(request, ClientDataRequest.class);
			if (multipartRequest != null) {
				List<MultipartFile> files = multipartRequest.getFiles(name);
				if (!files.isEmpty()) {
					return (files.size() == 1 ? files.get(0) : files);
				}
			}
			else if ((clientDataRequest != null) && isFormDataPost(clientDataRequest)) {
				if (HttpMethod.POST.matches(clientDataRequest.getMethod())) {
					Collection<Part> parts = clientDataRequest.getParts();
					if (!parts.isEmpty()) {
						return (parts.size() == 1 ? parts.iterator().next() : parts);
					}
				}
			}
			return null;
		}

		@Override
		public Set<String> getNames() {
			if (this.parameterNames == null) {
				this.parameterNames = initParameterNames(this.request);
			}
			return this.parameterNames;
		}

		protected Set<String> initParameterNames(PortletRequest request) {
			Set<String> set = new LinkedHashSet<>();
			Enumeration<String> enumeration = request.getParameterNames();
			while (enumeration.hasMoreElements()) {
				set.add(enumeration.nextElement());
			}
			return set;
		}
	}

}
