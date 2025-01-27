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
package com.liferay.portletmvc4spring.mvc.method.annotation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.Callable;

import javax.portlet.PortletResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;

import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.View;
import org.springframework.web.util.NestedServletException;

import com.liferay.portletmvc4spring.context.PortletWebRequest;


/**
 * Extends {@link InvocableHandlerMethod} with the ability to handle return values through a registered {@link
 * HandlerMethodReturnValueHandler} and also supports setting the response status based on a method-level {@code @
 * ResponseStatus} annotation.
 *
 * <p>A {@code null} return value (including void) may be interpreted as the end of request processing in combination
 * with a {@code @ResponseStatus} annotation, a not-modified check condition (see {@link
 * ServletWebRequest#checkNotModified(long)}), or a method argument that provides access to the response stream.
 *
 * @author  Rossen Stoyanchev
 * @author  Juergen Hoeller
 * @author  Neil Griffin
 * @since   5.1
 */
public class PortletInvocableHandlerMethod extends InvocableHandlerMethod {

	private static final Method CALLABLE_METHOD = ClassUtils.getMethod(Callable.class, "call");

	@Nullable
	private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

	/**
	 * Create an instance from a {@code HandlerMethod}.
	 */
	public PortletInvocableHandlerMethod(HandlerMethod handlerMethod) {
		super(handlerMethod);
	}

	/**
	 * Creates an instance from the given handler and method.
	 */
	public PortletInvocableHandlerMethod(Object handler, Method method) {
		super(handler, method);
	}

	/**
	 * Invoke the method and handle the return value through one of the configured {@link
	 * HandlerMethodReturnValueHandler HandlerMethodReturnValueHandlers}.
	 *
	 * @param  webRequest    the current request
	 * @param  mavContainer  the ModelAndViewContainer for this request
	 * @param  providedArgs  "given" arguments matched by type (not resolved)
	 */
	public void invokeAndHandle(PortletWebRequest webRequest, ModelAndViewContainer mavContainer,
		Object... providedArgs) throws Exception {

		Object returnValue = invokeForRequest(webRequest, mavContainer, providedArgs);
		setResponseStatus(webRequest);

		if (returnValue == null) {

			mavContainer.setRequestHandled(true);

			// Return since there is no reason to invoke a return value handler.
			return;
		}
		else if (StringUtils.hasText(getResponseStatusReason())) {
			mavContainer.setRequestHandled(true);

			return;
		}

		mavContainer.setRequestHandled(false);
		Assert.state(this.returnValueHandlers != null, "No return value handlers");

		try {
			this.returnValueHandlers.handleReturnValue(returnValue, getReturnValueType(returnValue), mavContainer,
				webRequest);
		}
		catch (Exception ex) {

			if (logger.isTraceEnabled()) {
				logger.trace(formatErrorForReturnValue(returnValue), ex);
			}

			throw ex;
		}
	}

	/**
	 * Register {@link HandlerMethodReturnValueHandler} instances to use to handle return values.
	 */
	public void setHandlerMethodReturnValueHandlers(HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
		this.returnValueHandlers = returnValueHandlers;
	}

	/**
	 * Create a nested PortletInvocableHandlerMethod subclass that returns the the given value (or raises an Exception
	 * if the value is one) rather than actually invoking the controller method. This is useful when processing async
	 * return values (e.g. Callable, DeferredResult, ListenableFuture).
	 */
	PortletInvocableHandlerMethod wrapConcurrentResult(Object result) {
		return new ConcurrentResultHandlerMethod(result, new ConcurrentResultMethodParameter(result));
	}

	private String formatErrorForReturnValue(@Nullable Object returnValue) {
		return "Error handling return value=[" + returnValue + "]" +
			((returnValue != null) ? (", type=" + returnValue.getClass().getName()) : "") + " in " + toString();
	}

	/**
	 * Does the given request qualify as "not modified"?
	 *
	 * @see  ServletWebRequest#checkNotModified(long)
	 * @see  ServletWebRequest#checkNotModified(String)
	 */
	private boolean isRequestNotModified(PortletWebRequest webRequest) {

		// TODO: Possibly implement "not modified" logic for portlets.
		return false;
	}

	/**
	 * Set the response status according to the {@link ResponseStatus} annotation.
	 */
	private void setResponseStatus(PortletWebRequest webRequest) throws IOException {
		HttpStatusCode status = getResponseStatus();

		if (status == null) {
			return;
		}

		PortletResponse response = webRequest.getResponse();

		if (response instanceof ResourceResponse) {
			ResourceResponse resourceResponse = (ResourceResponse) response;

			// TODO: Portlet 3.0 - resourceResponse.setStatus(status.value());
			resourceResponse.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString(status.value()));
		}

		// To be picked up by RedirectView
		webRequest.getRequest().setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, status);
	}

	/**
	 * A nested subclass of {@code PortletInvocableHandlerMethod} that uses a simple {@link Callable} instead of the
	 * original controller as the handler in order to return the fixed (concurrent) result value given to it.
	 * Effectively "resumes" processing with the asynchronously produced return value.
	 */
	private class ConcurrentResultHandlerMethod extends PortletInvocableHandlerMethod {

		private final MethodParameter returnType;

		public ConcurrentResultHandlerMethod(final Object result, ConcurrentResultMethodParameter returnType) {
			super((Callable<Object>) () -> {

					if (result instanceof Exception) {
						throw (Exception) result;
					}
					else if (result instanceof Throwable) {
						throw new NestedServletException("Async processing failed", (Throwable) result);
					}

					return result;
				}, CALLABLE_METHOD);

			if (PortletInvocableHandlerMethod.this.returnValueHandlers != null) {
				setHandlerMethodReturnValueHandlers(PortletInvocableHandlerMethod.this.returnValueHandlers);
			}

			this.returnType = returnType;
		}

		/**
		 * Bridge to actual controller type-level annotations.
		 */
		@Override
		public Class<?> getBeanType() {
			return PortletInvocableHandlerMethod.this.getBeanType();
		}

		/**
		 * Bridge to controller method-level annotations.
		 */
		@Override
		public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
			return PortletInvocableHandlerMethod.this.getMethodAnnotation(annotationType);
		}

		/**
		 * Bridge to actual return value or generic type within the declared async return type, e.g. Foo instead of
		 * {@code DeferredResult<Foo>}.
		 */
		@Override
		public MethodParameter getReturnValueType(@Nullable Object returnValue) {
			return this.returnType;
		}

		/**
		 * Bridge to controller method-level annotations.
		 */
		@Override
		public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
			return PortletInvocableHandlerMethod.this.hasMethodAnnotation(annotationType);
		}
	}

	/**
	 * MethodParameter subclass based on the actual return value type or if that's null falling back on the generic type
	 * within the declared async return type, e.g. Foo instead of {@code DeferredResult<Foo>}.
	 */
	private class ConcurrentResultMethodParameter extends MethodParameter {

		@Nullable
		private final Object returnValue;

		private final ResolvableType returnType;

		public ConcurrentResultMethodParameter(Object returnValue) {
			super(ConcurrentResultMethodParameter.class.getDeclaredMethods()[0], -1);
			this.returnValue = returnValue;
			this.returnType = ResolvableType.forType(super.getGenericParameterType()).getGeneric();
		}

		public ConcurrentResultMethodParameter(ConcurrentResultMethodParameter original) {
			super(original);
			this.returnValue = original.returnValue;
			this.returnType = original.returnType;
		}

		@Override
		public ConcurrentResultMethodParameter clone() {
			return new ConcurrentResultMethodParameter(this);
		}

		@Override
		public Type getGenericParameterType() {
			return this.returnType.getType();
		}

		@Override
		public Class<?> getParameterType() {

			if (this.returnValue != null) {
				return this.returnValue.getClass();
			}

			if (!ResolvableType.NONE.equals(this.returnType)) {
				return this.returnType.toClass();
			}

			return super.getParameterType();
		}
	}

}
