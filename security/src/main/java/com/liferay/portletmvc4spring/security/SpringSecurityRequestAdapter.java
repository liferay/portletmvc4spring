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
package com.liferay.portletmvc4spring.security;

import jakarta.portlet.PortletRequest;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletContext;

import com.liferay.plutonium.portlet.servlet.adapter.HttpServletRequestAdapter;


/**
 * @author  Neil Griffin
 */
public class SpringSecurityRequestAdapter extends HttpServletRequestAdapter {

	private DispatcherType dispatcherType;

	public SpringSecurityRequestAdapter(PortletRequest portletRequest, DispatcherType dispatcherType) {
		super(portletRequest);
		this.dispatcherType = dispatcherType;
	}

	public SpringSecurityRequestAdapter(PortletRequest portletRequest, ServletContext servletContext,
										DispatcherType dispatcherType) {
		super(portletRequest, servletContext);
		this.dispatcherType = dispatcherType;
	}

	@Override
	public String getContextPath() {
		return "";
	}

	@Override
	public DispatcherType getDispatcherType() {
		return dispatcherType;
	}

	@Override
	public String getHeader(String name) {

		if ("X-CSRF-TOKEN".equals(name)) {

			// https://issues.liferay.com/browse/MVCS-66
			return null;
		}

		return super.getHeader(name);
	}
}
