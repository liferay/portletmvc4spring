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
package com.liferay.portletmvc4spring.handler;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventPortlet;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletContext;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceServingPortlet;

import com.liferay.portletmvc4spring.HandlerAdapter;
import com.liferay.portletmvc4spring.ModelAndView;
import com.liferay.portletmvc4spring.context.PortletContextAware;
import com.liferay.portletmvc4spring.util.PortletUtils;


/**
 * Adapter to use the Portlet interface with the generic DispatcherPortlet. Calls the Portlet's {@code render} and
 * {@code processAction} methods to handle a request.
 *
 * <p>This adapter is not activated by default; it needs to be defined as a bean in the DispatcherPortlet context. It
 * will automatically apply to mapped handler beans that implement the Portlet interface then.
 *
 * <p>Note that Portlet instances defined as bean will not receive initialization and destruction callbacks, unless a
 * special post-processor such as SimplePortletPostProcessor is defined in the DispatcherPortlet context.
 *
 * <p><b>Alternatively, consider wrapping a Portlet with Spring's PortletWrappingController.</b> This is particularly
 * appropriate for existing Portlet classes, allowing to specify Portlet initialization parameters, etc.
 *
 * @author  John A. Lewis
 * @since   2.0
 * @see     javax.portlet.Portlet
 * @see     SimplePortletPostProcessor
 * @see     com.liferay.portletmvc4spring.mvc.PortletWrappingController
 */
public class SimplePortletHandlerAdapter implements HandlerAdapter, PortletContextAware {

	private PortletContext portletContext;

	@Override
	public void handleAction(ActionRequest request, ActionResponse response, Object handler) throws Exception {

		((Portlet) handler).processAction(request, response);
	}

	@Override
	public void handleEvent(EventRequest request, EventResponse response, Object handler) throws Exception {

		if (handler instanceof EventPortlet) {
			((EventPortlet) handler).processEvent(request, response);
		}
		else {

			// if no event processing method was found just keep render params
			response.setRenderParameters(request);
		}
	}

	@Override
	public ModelAndView handleRender(RenderRequest request, RenderResponse response, Object handler) throws Exception {

		((Portlet) handler).render(request, response);

		return null;
	}

	@Override
	public ModelAndView handleResource(ResourceRequest request, ResourceResponse response, Object handler)
		throws Exception {

		if (handler instanceof ResourceServingPortlet) {
			((ResourceServingPortlet) handler).serveResource(request, response);
		}
		else {

			// roughly equivalent to Portlet 2.0 GenericPortlet
			PortletUtils.serveResource(request, response, this.portletContext);
		}

		return null;
	}

	@Override
	public void setPortletContext(PortletContext portletContext) {
		this.portletContext = portletContext;
	}

	@Override
	public boolean supports(Object handler) {
		return (handler instanceof Portlet);
	}

}
