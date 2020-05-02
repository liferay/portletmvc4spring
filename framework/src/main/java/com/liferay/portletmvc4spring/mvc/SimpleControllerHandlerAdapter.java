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
package com.liferay.portletmvc4spring.mvc;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.portletmvc4spring.HandlerAdapter;
import com.liferay.portletmvc4spring.ModelAndView;
import com.liferay.portletmvc4spring.context.PortletContextAware;
import com.liferay.portletmvc4spring.util.PortletUtils;


/**
 * Adapter to use the Controller workflow interface with the generic DispatcherPortlet.
 *
 * <p>This is an SPI class, not used directly by application code.
 *
 * @author  Juergen Hoeller
 * @author  John A. Lewis
 * @since   2.0
 * @see     com.liferay.portletmvc4spring.DispatcherPortlet
 * @see     Controller
 * @see     ResourceAwareController
 * @see     EventAwareController
 */
public class SimpleControllerHandlerAdapter implements HandlerAdapter, PortletContextAware {

	private PortletContext portletContext;

	@Override
	public void handleAction(ActionRequest request, ActionResponse response, Object handler) throws Exception {

		((Controller) handler).handleActionRequest(request, response);
	}

	@Override
	public void handleEvent(EventRequest request, EventResponse response, Object handler) throws Exception {

		if (handler instanceof EventAwareController) {
			((EventAwareController) handler).handleEventRequest(request, response);
		}
		else {

			// if no event processing method was found just keep render params
			response.setRenderParameters(request);
		}
	}

	@Override
	public ModelAndView handleRender(RenderRequest request, RenderResponse response, Object handler) throws Exception {

		return ((Controller) handler).handleRenderRequest(request, response);
	}

	@Override
	public ModelAndView handleResource(ResourceRequest request, ResourceResponse response, Object handler)
		throws Exception {

		if (handler instanceof ResourceAwareController) {
			return ((ResourceAwareController) handler).handleResourceRequest(request, response);
		}
		else {

			// equivalent to Portlet 2.0 GenericPortlet
			PortletUtils.serveResource(request, response, this.portletContext);

			return null;
		}
	}

	@Override
	public void setPortletContext(PortletContext portletContext) {
		this.portletContext = portletContext;
	}

	@Override
	public boolean supports(Object handler) {
		return (handler instanceof Controller);
	}

}
