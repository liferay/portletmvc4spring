/**
 * Copyright (c) 2000-2019 the original author or authors.
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

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;

import org.springframework.web.servlet.view.JstlView;

import com.liferay.portletmvc4spring.util.PortletContainer;


/**
 * This class provides a work-around for a JSTL incompatibility with Apache Pluto. See the {@link
 * #exposeHelpers(HttpServletRequest)}} method for details.
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
}
