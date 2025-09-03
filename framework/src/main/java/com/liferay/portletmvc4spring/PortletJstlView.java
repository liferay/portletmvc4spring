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
package com.liferay.portletmvc4spring;

import jakarta.portlet.ResourceRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;

import org.springframework.web.servlet.view.JstlView;

import com.liferay.portletmvc4spring.util.PortletContainer;


/**
 * This class provides work-arounds for two incompatibilities with Apache Pluto:
 *
 * <ol>
 *   <li>JSTL incompatibility -- see the {@link #exposeHelpers(HttpServletRequest)}} method for details.</li>
 *   <li>Request dispatcher incompatibility -- see the {@link #useInclude(HttpServletRequest, HttpServletResponse)}
 *     method for more details</li>
 * </ol>
 *
 * @author  Neil Griffin
 * @since   5.1.0
 */
public class PortletJstlView extends JstlView {

	/**
	 * If the specified request is associated with Apache Pluto, then this method takes no action. This is because the
	 * {@link JstlView#exposeHelpers(HttpServletRequest)} superclass method calls {@link
	 * org.springframework.web.servlet.support.JstlUtils#exposeLocalizationContext(HttpServletRequest, MessageSource)}
	 * which is incompatible with Apache Pluto. For all other portlet containers, this method simply calls through to
	 * the superclass method.
	 */
	@Override
	protected void exposeHelpers(HttpServletRequest request) throws Exception {

		if (!PortletContainer.PLUTO.isDetected(request)) {
			super.exposeHelpers(request);
		}
	}

	/**
	 * If the specified request is associated with Apache Pluto and the RESOURCE_PHASE of the portlet lifecycle, then
	 * this method returns <code>true</code> in order to force PortletMVC4Spring to dispatch using "include" rather than
	 * "forward". This is because Apache Pluto is not able to render JSPs using the RESOURCE_PHASE using a "forward" --
	 * it can only do it with an "include". For all other portlet containers and types of requests, this method simply
	 * calls through to the superclass method.
	 */
	@Override
	protected boolean useInclude(HttpServletRequest request, HttpServletResponse response) {

		if (PortletContainer.PLUTO.isDetected(request)) {

			Object portletRequest = request.getAttribute("jakarta.portlet.request");

			if (portletRequest instanceof ResourceRequest) {
				return true;
			}
		}

		return super.useInclude(request, response);
	}
}
