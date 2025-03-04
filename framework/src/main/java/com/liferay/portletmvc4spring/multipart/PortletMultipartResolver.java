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
package com.liferay.portletmvc4spring.multipart;

import javax.portlet.ActionRequest;
import javax.portlet.ResourceRequest;

import org.springframework.web.multipart.MultipartException;


/**
 * Portlet version of Spring's multipart resolution strategy for file uploads as defined in <a
 * href="http://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>.
 *
 * <p>Implementations are typically usable both within any application context and standalone.
 *
 * <p>There is one concrete implementation included in Spring:
 *
 * <ul>
 *   <li>{@link org.springframework.web.multipart.commons.CommonsMultipartResolver} for Apache Commons FileUpload</li>
 * </ul>
 *
 * <p>There is no default resolver implementation used for Spring {@link com.liferay.portletmvc4spring.DispatcherPortlet
 * DispatcherPortlets}, as an application might choose to parse its multipart requests itself. To define an
 * implementation, create a bean with the id "portletMultipartResolver" in a {@code DispatcherPortlet's} application
 * context. Such a resolver gets applied to all requests handled by that {@code DispatcherPortlet}.
 *
 * <p>If a {@code DispatcherPortlet} detects a multipart request, it will resolve it via the configured {@link
 * com.liferay.portletmvc4spring.multipart.PortletMultipartResolver} and pass on a wrapped Portlet {@link
 * ActionRequest}. Controllers can then cast their given request to the {@link MultipartActionRequest} interface, being
 * able to access {@code MultipartFiles}. Note that this cast is only supported in case of an actual multipart request.
 *
 * <pre class="code"> public void handleActionRequest(ActionRequest request, ActionResponse response) {
     MultipartActionRequest multipartRequest = (MultipartActionRequest) request;
     MultipartFile multipartFile = multipartRequest.getFile("image");
     ...
   }</pre>
 *
 * Instead of direct access, command or form controllers can register a {@link
 * org.springframework.web.multipart.support.ByteArrayMultipartFileEditor} or {@link
 * org.springframework.web.multipart.support.StringMultipartFileEditor} with their data binder, to automatically apply
 * multipart content to form bean properties.
 *
 * <p>Note: There is hardly ever a need to access the {@code MultipartResolver} itself from application code. It will
 * simply do its work behind the scenes, making {@code MultipartActionRequests} available to controllers.
 *
 * @author  Juergen Hoeller
 * @since   2.0
 * @see     MultipartActionRequest
 * @see     MultipartResourceRequest
 * @see     org.springframework.web.multipart.MultipartFile
 * @see     org.springframework.web.multipart.support.ByteArrayMultipartFileEditor
 * @see     org.springframework.web.multipart.support.StringMultipartFileEditor
 * @see     com.liferay.portletmvc4spring.DispatcherPortlet
 */
public interface PortletMultipartResolver {

	/**
	 * Cleanup any resources used for the multipart handling, such as storage for any uploaded file(s).
	 *
	 * @param  request  the request to cleanup resources for
	 */
	void cleanupMultipart(MultipartActionRequest request);

	/**
	 * Cleanup any resources used for the multipart handling, such as storage for any uploaded file(s).
	 *
	 * @param  request  the request to cleanup resources for
	 *
	 * @since  5.1
	 */
	void cleanupMultipart(MultipartResourceRequest request);

	/**
	 * Determine if the given request contains multipart content.
	 *
	 * <p>Will typically check for content type"{@code multipart/form-data}", but the actually accepted requests might
	 * depend on the capabilities of the resolver implementation.
	 *
	 * @param   request  the portlet request to be evaluated
	 *
	 * @return  whether the request contains multipart content
	 */
	boolean isMultipart(ActionRequest request);

	/**
	 * Determine if the given request contains multipart content.
	 *
	 * <p>Will typically check for content type"{@code multipart/form-data}", but the actually accepted requests might
	 * depend on the capabilities of the resolver implementation.
	 *
	 * @param   request  the portlet request to be evaluated
	 *
	 * @return  whether the request contains multipart content
	 *
	 * @since   5.1
	 */
	boolean isMultipart(ResourceRequest request);

	/**
	 * Parse the given portlet request into multipart files and parameters, and wrap the request inside a
	 * MultipartActionRequest object that provides access to file descriptors and makes contained parameters accessible
	 * via the standard PortletRequest methods.
	 *
	 * @param   request  the portlet request to wrap (must be of a multipart content type)
	 *
	 * @return  the wrapped portlet request
	 *
	 * @throws  org.springframework.web.multipart.MultipartException  if the portlet request is not multipart, or if
	 *                                                                implementation-specific problems are encountered
	 *                                                                (such as exceeding file size limits)
	 *
	 * @see     com.liferay.portletmvc4spring.multipart.MultipartActionRequest#getFile
	 * @see     com.liferay.portletmvc4spring.multipart.MultipartActionRequest#getFileNames
	 * @see     com.liferay.portletmvc4spring.multipart.MultipartActionRequest#getFileMap
	 * @see     javax.portlet.ActionRequest#getParameter
	 * @see     javax.portlet.ActionRequest#getParameterNames
	 * @see     javax.portlet.ActionRequest#getParameterMap
	 * @since   5.1
	 */
	MultipartActionRequest resolveMultipart(ActionRequest request) throws MultipartException;

	/**
	 * Parse the given portlet request into multipart files and parameters, and wrap the request inside a
	 * MultipartResourceRequest object that provides access to file descriptors and makes contained parameters
	 * accessible via the standard PortletRequest methods.
	 *
	 * @param   request  the portlet request to wrap (must be of a multipart content type)
	 *
	 * @return  the wrapped portlet request
	 *
	 * @throws  org.springframework.web.multipart.MultipartException  if the portlet request is not multipart, or if
	 *                                                                implementation-specific problems are encountered
	 *                                                                (such as exceeding file size limits)
	 *
	 * @see     com.liferay.portletmvc4spring.multipart.MultipartResourceRequest#getFile
	 * @see     com.liferay.portletmvc4spring.multipart.MultipartResourceRequest#getFileNames
	 * @see     com.liferay.portletmvc4spring.multipart.MultipartResourceRequest#getFileMap
	 * @see     javax.portlet.ResourceRequest#getParameter
	 * @see     javax.portlet.ResourceRequest#getParameterNames
	 * @see     javax.portlet.ResourceRequest#getParameterMap
	 * @since   5.1
	 */
	MultipartResourceRequest resolveMultipart(ResourceRequest request) throws MultipartException;

}
