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
package com.liferay.portletmvc4spring.context;

import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletSession;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import org.springframework.web.context.request.NativeWebRequest;

import com.liferay.portletmvc4spring.util.PortletUtils;


/**
 * {@link org.springframework.web.context.request.WebRequest} adapter for a {@link jakarta.portlet.PortletRequest}.
 *
 * @author  Juergen Hoeller
 * @since   2.0
 */
public class PortletWebRequest extends PortletRequestAttributes implements NativeWebRequest {

	/**
	 * Create a new PortletWebRequest instance for the given request.
	 *
	 * @param  request  current portlet request
	 */
	public PortletWebRequest(PortletRequest request) {
		super(request);
	}

	/**
	 * Create a new PortletWebRequest instance for the given request/response pair.
	 *
	 * @param  request   current portlet request
	 * @param  response  current portlet response
	 */
	public PortletWebRequest(PortletRequest request, PortletResponse response) {
		super(request, response);
	}

	/**
	 * Last-modified handling not supported for portlet requests: As a consequence, this method always returns {@code
	 * false}.
	 */
	@Override
	public boolean checkNotModified(long lastModifiedTimestamp) {
		return false;
	}

	/**
	 * Last-modified handling not supported for portlet requests: As a consequence, this method always returns {@code
	 * false}.
	 */
	@Override
	public boolean checkNotModified(String eTag) {
		return false;
	}

	/**
	 * Last-modified handling not supported for portlet requests: As a consequence, this method always returns {@code
	 * false}.
	 *
	 * @since  4.2
	 */
	@Override
	public boolean checkNotModified(String etag, long lastModifiedTimestamp) {
		return false;
	}

	@Override
	public String getContextPath() {
		return getRequest().getContextPath();
	}

	@Override
	public String getDescription(boolean includeClientInfo) {
		PortletRequest request = getRequest();
		StringBuilder result = new StringBuilder();
		result.append("context=").append(request.getContextPath());

		if (includeClientInfo) {
			PortletSession session = request.getPortletSession(false);

			if (session != null) {
				result.append(";session=").append(session.getId());
			}

			String user = getRequest().getRemoteUser();

			if (StringUtils.hasLength(user)) {
				result.append(";user=").append(user);
			}
		}

		return result.toString();
	}

	@Override
	public String getHeader(String headerName) {
		return getRequest().getProperty(headerName);
	}

	@Override
	public Iterator<String> getHeaderNames() {
		return CollectionUtils.toIterator(getRequest().getPropertyNames());
	}

	@Override
	public String[] getHeaderValues(String headerName) {
		String[] headerValues = StringUtils.toStringArray(getRequest().getProperties(headerName));

		return ((!ObjectUtils.isEmpty(headerValues)) ? headerValues : null);
	}

	@Override
	public Locale getLocale() {
		return getRequest().getLocale();
	}

	@Override
	public Object getNativeRequest() {
		return getRequest();
	}

	@Override
	public <T> T getNativeRequest(Class<T> requiredType) {
		return PortletUtils.getNativeRequest(getRequest(), requiredType);
	}

	@Override
	public Object getNativeResponse() {
		return getResponse();
	}

	@Override
	public <T> T getNativeResponse(Class<T> requiredType) {
		return PortletUtils.getNativeResponse(getResponse(), requiredType);
	}

	@Override
	public String getParameter(String paramName) {
		return getRequest().getParameter(paramName);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return getRequest().getParameterMap();
	}

	@Override
	public Iterator<String> getParameterNames() {
		return CollectionUtils.toIterator(getRequest().getParameterNames());
	}

	@Override
	public String[] getParameterValues(String paramName) {
		return getRequest().getParameterValues(paramName);
	}

	@Override
	public String getRemoteUser() {
		return getRequest().getRemoteUser();
	}

	@Override
	public Principal getUserPrincipal() {
		return getRequest().getUserPrincipal();
	}

	@Override
	public boolean isSecure() {
		return getRequest().isSecure();
	}

	@Override
	public boolean isUserInRole(String role) {
		return getRequest().isUserInRole(role);
	}

	@Override
	public String toString() {
		return "PortletWebRequest: " + getDescription(true);
	}

}
