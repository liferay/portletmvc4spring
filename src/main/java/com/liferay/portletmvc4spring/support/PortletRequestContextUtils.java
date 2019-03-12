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
package com.liferay.portletmvc4spring.support;

import java.util.Locale;
import java.util.TimeZone;

import javax.portlet.PortletRequest;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;

import org.springframework.lang.Nullable;

import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.support.RequestContext;

import com.liferay.portletmvc4spring.PortletLocaleContextResolver;
import com.liferay.portletmvc4spring.PortletLocaleResolver;


/**
 * Utility class for easy access to request-specific state which has been set by the {@link DispatcherServlet}.
 *
 * <p>Supports lookup of current WebApplicationContext, LocaleResolver, Locale, ThemeResolver, Theme, and
 * MultipartResolver.
 *
 * @author  Juergen Hoeller
 * @author  Rossen Stoyanchev
 * @author  Neil Griffin
 * @since   5.1
 * @see     RequestContext
 * @see     DispatcherServlet
 */
public abstract class PortletRequestContextUtils {

	/**
	 * Retrieve the current locale from the given request, using the PortletLocaleResolver bound to the request by the
	 * DispatcherServlet (if available), falling back to the request's accept-header Locale.
	 *
	 * <p>This method serves as a straightforward alternative to the standard Servlet {@link PortletRequest#getLocale()}
	 * method, falling back to the latter if no more specific locale has been found.
	 *
	 * <p>Consider using {@link org.springframework.context.i18n.LocaleContextHolder#getLocale()} which will normally be
	 * populated with the same Locale.
	 *
	 * @param   request  current HTTP request
	 *
	 * @return  the current locale for the given request, either from the PortletLocaleResolver or from the plain
	 *          request itself
	 *
	 * @see     #getPortletLocaleResolver
	 * @see     org.springframework.context.i18n.LocaleContextHolder#getLocale()
	 */
	public static Locale getLocale(PortletRequest request) {
		PortletLocaleResolver localeResolver = getPortletLocaleResolver(request);

		return ((localeResolver != null) ? localeResolver.resolveLocale(request) : request.getLocale());
	}

	/**
	 * Return the LocaleResolver that has been bound to the request by the DispatcherServlet.
	 *
	 * @param   request  current HTTP request
	 *
	 * @return  the current LocaleResolver, or {@code null} if not found
	 */
	@Nullable
	public static PortletLocaleResolver getPortletLocaleResolver(PortletRequest request) {
		return (PortletLocaleResolver) request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
	}

	/**
	 * Retrieve the current time zone from the given request, using the TimeZoneAwareLocaleResolver bound to the request
	 * by the DispatcherServlet (if available), falling back to the system's default time zone.
	 *
	 * <p>Note: This method returns {@code null} if no specific time zone can be resolved for the given request. This is
	 * in contrast to {@link #getLocale} where there is always the request's accept-header locale to fall back to.
	 *
	 * <p>Consider using {@link org.springframework.context.i18n.LocaleContextHolder#getTimeZone()} which will normally
	 * be populated with the same TimeZone: That method only differs in terms of its fallback to the system time zone if
	 * the PortletLocaleResolver hasn't provided a specific time zone (instead of this method's {@code null}).
	 *
	 * @param   request  current HTTP request
	 *
	 * @return  the current time zone for the given request, either from the TimeZoneAwareLocaleResolver or {@code null}
	 *          if none associated
	 *
	 * @see     #getPortletLocaleResolver
	 * @see     org.springframework.context.i18n.LocaleContextHolder#getTimeZone()
	 */
	@Nullable
	public static TimeZone getTimeZone(PortletRequest request) {
		PortletLocaleResolver localeResolver = getPortletLocaleResolver(request);

		if (localeResolver instanceof LocaleContextResolver) {
			LocaleContext localeContext = ((PortletLocaleContextResolver) localeResolver).resolveLocaleContext(request);

			if (localeContext instanceof TimeZoneAwareLocaleContext) {
				return ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
			}
		}

		return null;
	}

}
