/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.portletmvc4spring.mvc.method;


import org.springframework.core.Ordered;
import org.springframework.web.method.HandlerMethod;
import com.liferay.portletmvc4spring.HandlerAdapter;
import com.liferay.portletmvc4spring.ModelAndView;
import com.liferay.portletmvc4spring.handler.PortletContentGenerator;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * Abstract base class for {@link HandlerAdapter} implementations that support
 * handlers of type {@link HandlerMethod}.
 *
 * @author Arjen Poutsma
 * @author Neil Griffin
 * @since 5.1
 */
public abstract class AbstractPortletHandlerMethodAdapter extends PortletContentGenerator
	implements HandlerAdapter, Ordered {

	private int order = Ordered.LOWEST_PRECEDENCE;


	/**
	 * Specify the order value for this HandlerAdapter bean.
	 * <p>The default value is {@code Ordered.LOWEST_PRECEDENCE}, meaning non-ordered.
	 * @see Ordered#getOrder()
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}


	/**
	 * @param handler the handler instance to check
	 * @return whether or not this adapter can adapt the given handler
	 */
	@Override
	public final boolean supports(Object handler) {
		return supportsInternal(handler);
	}

	/**
	 * Given a handler method, return whether or not this adapter can support it.
	 * @param handler refers to an instance of a class with a method annotated with
	 *                {@link com.liferay.portletmvc4spring.bind.annotation.ActionMapping},
	 *                {@link com.liferay.portletmvc4spring.bind.annotation.EventMapping},
	 *                {@link com.liferay.portletmvc4spring.bind.annotation.RenderMapping}, or
	 *                {@link com.liferay.portletmvc4spring.bind.annotation.ResourceMapping}.
	 *                Alternatively refers to an instance of {@link HandlerMethod}.
	 * @return whether or not this adapter can adapt the given method
	 */
	protected abstract boolean supportsInternal(Object handler);

	@Override
	public void handleAction(
		ActionRequest request, ActionResponse response, Object handler) throws Exception {
		handleActionInternal(request, response, handler);
	}

	/**
	 * Use the given handler method to handle the request.
	 * @param request current action request
	 * @param response current action response
	 * @param handler refers to an instance of a class with a method annotated with
	 *                {@link com.liferay.portletmvc4spring.bind.annotation.ActionMapping}.
	 *                Alternatively refers to an instance of {@link HandlerMethod}.
	 * @throws Exception in case of errors
	 */
	protected abstract void handleActionInternal(ActionRequest request, ActionResponse response, Object handler) throws Exception;

	@Override
	public void handleEvent(
		EventRequest request, EventResponse response, Object handler) throws Exception {
		handleEventInternal(request, response, handler);
	}

	/**
	 * Use the given handler method to handle the request.
	 * @param request current action request
	 * @param response current action response
	 * @param handler refers to an instance of a class with a method annotated with
	 *                {@link com.liferay.portletmvc4spring.bind.annotation.EventMapping}.
	 *                Alternatively refers to an instance of {@link HandlerMethod}.
	 * @throws Exception in case of errors
	 */
	protected abstract void handleEventInternal(EventRequest request, EventResponse response, Object handler) throws Exception;

	@Override
	public ModelAndView handleRender(
		RenderRequest request, RenderResponse response, Object handler) throws Exception {
		return handleRenderInternal(request, response, handler);
	}

	/**
	 * Use the given handler method to handle the request.
	 * @param request current action request
	 * @param response current action response
	 * @param handler refers to an instance of a class with a method annotated with
	 *                {@link com.liferay.portletmvc4spring.bind.annotation.RenderMapping}.
	 *                Alternatively refers to an instance of {@link HandlerMethod}.
	 * @return a ModelAndView object with the name of the view and the required model data,
	 * or {@code null} if the request has been handled directly
	 * @throws Exception in case of errors
	 */
	protected abstract ModelAndView handleRenderInternal(RenderRequest request, RenderResponse response, Object handler)
		throws Exception;

	@Override
	public ModelAndView handleResource(
		ResourceRequest request, ResourceResponse response, Object handler) throws Exception {
		return handleResourceInternal(request, response, handler);
	}

	/**
	 * Use the given handler method to handle the request.
	 * @param request current action request
	 * @param response current action response
	 * @param handler refers to an instance of a class with a method annotated with
	 *                {@link com.liferay.portletmvc4spring.bind.annotation.ResourceMapping}.
	 *                Alternatively refers to an instance of {@link HandlerMethod}.
	 * @return a ModelAndView object with the name of the view and the required model data,
	 * or {@code null} if the request has been handled directly
	 * @throws Exception in case of errors
	 */
	protected abstract ModelAndView handleResourceInternal(ResourceRequest request, ResourceResponse response, Object handler) throws Exception;

}
