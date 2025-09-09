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
package com.liferay.portletmvc4spring.util;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.filter.PortletRequestWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;


/**
 * @author  Neil Griffin
 * @since   5.1.0
 */
public enum PortletContainer {

	LIFERAY("com.liferay"), PLUTO("com.liferay.pluto"), WEBSPHERE("com.ibm");

	private String fqcnPrefix;

	PortletContainer(String fqcnPrefix) {
		this.fqcnPrefix = fqcnPrefix;
	}

	public boolean isDetected(HttpServletRequest httpServletRequest) {

		if (httpServletRequest == null) {
			return false;
		}

		Class<? extends HttpServletRequest> httpServletRequestClass = httpServletRequest.getClass();

		String httpServletRequestClassName = httpServletRequestClass.getName();

		if (httpServletRequestClassName.startsWith(fqcnPrefix)) {
			return true;
		}

		if (httpServletRequest instanceof HttpServletRequestWrapper) {
			HttpServletRequestWrapper httpServletRequestWrapper = (HttpServletRequestWrapper) httpServletRequest;

			return isDetected((HttpServletRequest) httpServletRequestWrapper.getRequest());
		}

		return false;
	}

	public boolean isDetected(PortletRequest portletRequest) {

		portletRequest = unwrapPortletRequest(portletRequest);

		Class<? extends PortletRequest> portletRequestClass = portletRequest.getClass();

		String portletRequestClassName = portletRequestClass.getName();

		return portletRequestClassName.startsWith(fqcnPrefix);
	}

	private PortletRequest unwrapPortletRequest(PortletRequest portletRequest) {

		if (portletRequest instanceof PortletRequestWrapper) {
			PortletRequestWrapper portletRequestWrapper = (PortletRequestWrapper) portletRequest;

			return unwrapPortletRequest(portletRequestWrapper.getRequest());
		}

		return portletRequest;
	}
}
