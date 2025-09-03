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
import java.io.OutputStream;
import java.io.Writer;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletResponse;

import org.springframework.core.MethodParameter;

import org.springframework.lang.Nullable;

import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * Resolves servlet backed response-related method arguments. Supports values of the following types:
 *
 * <ul>
 *   <li>{@link PortletResponse}</li>
 *   <li>{@link OutputStream}</li>
 *   <li>{@link Writer}</li>
 * </ul>
 *
 * @author  Arjen Poutsma
 * @author  Rossen Stoyanchev
 * @author  Juergen Hoeller
 * @since   3.1
 */
public class PortletResponseMethodArgumentResolver implements HandlerMethodArgumentResolver {

	/**
	 * Set {@link ModelAndViewContainer#setRequestHandled(boolean)} to {@code false} to indicate that the method
	 * signature provides access to the response. If subsequently the underlying method returns {@code null}, the
	 * request is considered directly handled.
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

		if (mavContainer != null) {
			mavContainer.setRequestHandled(true);
		}

		Class<?> paramType = parameter.getParameterType();

		// PortletResponse
		if (PortletResponse.class.isAssignableFrom(paramType)) {
			return resolveNativeResponse(webRequest, paramType);
		}

		// PortletResponse required for all further argument types
		return resolveArgument(paramType, resolveNativeResponse(webRequest, PortletResponse.class));
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();

		return (PortletResponse.class.isAssignableFrom(paramType) || OutputStream.class.isAssignableFrom(paramType) ||
				Writer.class.isAssignableFrom(paramType));
	}

	private Object resolveArgument(Class<?> paramType, PortletResponse response) throws IOException {

		if (OutputStream.class.isAssignableFrom(paramType)) {

			if (!(response instanceof MimeResponse)) {
				throw new IllegalStateException("OutputStream can only get obtained for Render/ResourceResponse");
			}

			return ((MimeResponse) response).getPortletOutputStream();
		}
		else if (Writer.class.isAssignableFrom(paramType)) {

			if (!(response instanceof MimeResponse)) {
				throw new IllegalStateException("Writer can only get obtained for Render/ResourceResponse");
			}

			return ((MimeResponse) response).getWriter();
		}

		// Should never happen...
		throw new UnsupportedOperationException("Unknown parameter type: " + paramType);
	}

	private <T> T resolveNativeResponse(NativeWebRequest webRequest, Class<T> requiredType) {
		T nativeResponse = webRequest.getNativeResponse(requiredType);

		if (nativeResponse == null) {
			throw new IllegalStateException("Current response is not of type [" + requiredType.getName() + "]: " +
				webRequest);
		}

		return nativeResponse;
	}

}
