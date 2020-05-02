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
import java.io.InputStream;
import java.io.Reader;
import java.security.Principal;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

import javax.portlet.ClientDataRequest;
import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.WindowState;

import org.springframework.core.MethodParameter;

import org.springframework.lang.Nullable;

import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartRequest;

import com.liferay.portletmvc4spring.support.PortletRequestContextUtils;


/**
 * Resolves servlet backed request-related method arguments. Supports values of the following types:
 *
 * <ul>
 *   <li>{@link WebRequest}</li>
 *   <li>{@link PortletRequest}</li>
 *   <li>{@link MultipartRequest}</li>
 *   <li>{@link PortletSession}</li>
 *   <li>{@link Principal}</li>
 *   <li>{@link InputStream}</li>
 *   <li>{@link Reader}</li>
 *   <li>{@link Locale}</li>
 *   <li>{@link TimeZone} (as of Spring 4.0)</li>
 *   <li>{@link ZoneId} (as of Spring 4.0 and Java 8)</li>
 * </ul>
 *
 * @author  Arjen Poutsma
 * @author  Rossen Stoyanchev
 * @author  Juergen Hoeller
 * @author  Neil Griffin
 * @since   5.1
 */
public class PortletRequestMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

		Class<?> paramType = parameter.getParameterType();

		// WebRequest / NativeWebRequest / ServletWebRequest
		if (WebRequest.class.isAssignableFrom(paramType)) {

			if (!paramType.isInstance(webRequest)) {
				throw new IllegalStateException("Current request is not of type [" + paramType.getName() + "]: " +
					webRequest);
			}

			return webRequest;
		}

		// PortletRequest / MultipartRequest
		if (PortletRequest.class.isAssignableFrom(paramType) || MultipartRequest.class.isAssignableFrom(paramType)) {
			return resolveNativeRequest(webRequest, paramType);
		}

		// PortletRequest required for all further argument types
		return resolveArgument(paramType, resolveNativeRequest(webRequest, PortletRequest.class));
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> paramType = parameter.getParameterType();

		return (WebRequest.class.isAssignableFrom(paramType) || Event.class.isAssignableFrom(paramType) ||
				MultipartRequest.class.isAssignableFrom(paramType) || PortalContext.class.isAssignableFrom(paramType) ||
				PortletMode.class.isAssignableFrom(paramType) || PortletPreferences.class.isAssignableFrom(paramType) ||
				PortletRequest.class.isAssignableFrom(paramType) || PortletSession.class.isAssignableFrom(paramType) ||
				Principal.class.isAssignableFrom(paramType) || InputStream.class.isAssignableFrom(paramType) ||
				Reader.class.isAssignableFrom(paramType) || (Locale.class == paramType) ||
				(TimeZone.class == paramType) || (WindowState.class == paramType) || (ZoneId.class == paramType));
	}

	@Nullable
	private Object resolveArgument(Class<?> paramType, PortletRequest request) throws IOException {

		if (Event.class.isAssignableFrom(paramType)) {

			if (!(request instanceof EventRequest)) {
				throw new IllegalStateException("Event can only get obtained from EventRequest");
			}

			return ((EventRequest) request).getEvent();
		}
		else if (PortalContext.class.isAssignableFrom(paramType)) {
			return request.getPortalContext();
		}
		else if (PortletMode.class.isAssignableFrom(paramType)) {
			return request.getPortletMode();
		}
		else if (PortletPreferences.class.isAssignableFrom(paramType)) {
			return request.getPreferences();
		}
		else if (PortletSession.class.isAssignableFrom(paramType)) {
			PortletSession session = request.getPortletSession();

			if ((session != null) && !paramType.isInstance(session)) {
				throw new IllegalStateException("Current session is not of type [" + paramType.getName() + "]: " +
					session);
			}

			return session;
		}
		else if (InputStream.class.isAssignableFrom(paramType)) {

			if (!(request instanceof ClientDataRequest)) {
				throw new IllegalStateException("InputStream can only get obtained for Action/ResourceRequest");
			}

			return ((ClientDataRequest) request).getPortletInputStream();
		}
		else if (Reader.class.isAssignableFrom(paramType)) {

			if (!(request instanceof ClientDataRequest)) {
				throw new IllegalStateException("Reader can only get obtained for Action/ResourceRequest");
			}

			return ((ClientDataRequest) request).getReader();
		}
		else if (Principal.class.isAssignableFrom(paramType)) {
			Principal userPrincipal = request.getUserPrincipal();

			if ((userPrincipal != null) && !paramType.isInstance(userPrincipal)) {
				throw new IllegalStateException("Current user principal is not of type [" + paramType.getName() +
					"]: " + userPrincipal);
			}

			return userPrincipal;
		}
		else if (Locale.class == paramType) {
			return PortletRequestContextUtils.getLocale(request);
		}
		else if (TimeZone.class == paramType) {
			TimeZone timeZone = PortletRequestContextUtils.getTimeZone(request);

			return ((timeZone != null) ? timeZone : TimeZone.getDefault());
		}
		else if (WindowState.class == paramType) {
			return request.getWindowState();
		}
		else if (ZoneId.class == paramType) {
			TimeZone timeZone = PortletRequestContextUtils.getTimeZone(request);

			return ((timeZone != null) ? timeZone.toZoneId() : ZoneId.systemDefault());
		}

		// Should never happen...
		throw new UnsupportedOperationException("Unknown parameter type: " + paramType.getName());
	}

	private <T> T resolveNativeRequest(NativeWebRequest webRequest, Class<T> requiredType) {
		T nativeRequest = webRequest.getNativeRequest(requiredType);

		if (nativeRequest == null) {
			throw new IllegalStateException("Current request is not of type [" + requiredType.getName() + "]: " +
				webRequest);
		}

		return nativeRequest;
	}

}
