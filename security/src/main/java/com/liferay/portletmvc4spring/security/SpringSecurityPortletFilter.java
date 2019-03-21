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
package com.liferay.portletmvc4spring.security;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.MutableRenderParameters;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderParameters;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.filter.ActionFilter;
import javax.portlet.filter.FilterChain;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.RenderFilter;
import javax.portlet.filter.ResourceFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.adapter.HttpServletRequestAdapter;
import org.apache.pluto.adapter.HttpServletResponseAdapter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractMessageSource;

import org.springframework.security.access.AccessDeniedException;

import org.springframework.web.filter.DelegatingFilterProxy;

import com.liferay.portletmvc4spring.context.PortletApplicationContextUtils;


/**
 * In order to enable CSRF protection, it is necessary to register this class in the WEB-INF/portlet.xml descriptor. For
 * example:
 *
 * <pre>
 {@code
 <portlet>
    <filter>
        <filter-name>SpringSecurityPortletFilter</filter-name>
        <filter-class>com.liferay.portletmvc4spring.security.SpringSecurityPortletFilter</filter-class>
        <lifecycle>ACTION_PHASE</lifecycle>
        <lifecycle>RENDER_PHASE</lifecycle>
        <lifecycle>RESOURCE_PHASE</lifecycle>
    </filter>
    <filter-mapping>
        <filter-name>SpringSecurityPortletFilter</filter-name>
        <portlet-name>portlet1</portlet-name>
    </filter-mapping>
 </portlet>
 }
 * </pre>
 *
 * It is also necessary to specify {@link SpringSecurityPortletConfigurer} in a component-scan or register it in the
 * WEB-INF/spring-context/portlet-application-context.xml descriptor. For example:
 *
 * <pre>
 {@code
 <bean id="springSecurityPortletConfigurer" class="com.liferay.portletmvc4spring.security.SpringSecurityPortletConfigurer" />
 <bean id="delegatingFilterProxy" class="org.springframework.web.filter.DelegatingFilterProxy">
    <property name="targetBeanName" value="springSecurityFilterChain" />
 </bean>
 }
 * </pre>
 * Finally, it is necessary to specify the following in the WEB-INF/web.xml descriptor:
 * <pre>
 {@code
 <filter>
 	<filter-name>delegatingFilterProxy</filter-name>
 	<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
 </filter>
 <filter-mapping>
 	<filter-name>delegatingFilterProxy</filter-name>
 	<url-pattern>/WEB-INF/servlet/view</url-pattern>
 	<dispatcher>FORWARD</dispatcher>
 	<dispatcher>INCLUDE</dispatcher>
 </filter-mapping>
 }
 * </pre>
 *
 * @author  Neil Griffin
 */
public class SpringSecurityPortletFilter implements ActionFilter, RenderFilter, ResourceFilter {

	private static final String ACCESS_DENIED = SpringSecurityPortletFilter.class.getName() + "_ACCESS_DENIED";

	private PortletContext portletContext;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ActionRequest actionRequest, ActionResponse actionResponse, FilterChain portletFilterChain)
		throws IOException, PortletException {

		ApplicationContext applicationContext = PortletApplicationContextUtils.getWebApplicationContext(portletContext);

		DelegatingFilterProxy delegatingFilterProxy = _getBean(applicationContext, DelegatingFilterProxy.class);

		if (delegatingFilterProxy != null) {

			try {
				delegatingFilterProxy.doFilter(new HttpServletRequestAdapter(actionRequest),
					new HttpServletResponseAdapter(actionResponse), EmptyServletFilterChain.INSTANCE);
			}
			catch (AccessDeniedException e) {
				MutableRenderParameters mutableRenderParameters = actionResponse.getRenderParameters();
				mutableRenderParameters.setValue(ACCESS_DENIED, Boolean.TRUE.toString());

				return;
			}
			catch (ServletException e) {
				throw new PortletException(e);
			}
		}

		portletFilterChain.doFilter(actionRequest, actionResponse);
	}

	@Override
	public void doFilter(ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		FilterChain portletFilterChain) throws IOException, PortletException {

		if ("post".equalsIgnoreCase(resourceRequest.getMethod())) {

			ApplicationContext applicationContext = PortletApplicationContextUtils.getWebApplicationContext(
					portletContext);

			DelegatingFilterProxy delegatingFilterProxy = _getBean(applicationContext, DelegatingFilterProxy.class);

			if (delegatingFilterProxy != null) {

				try {
					delegatingFilterProxy.doFilter(new HttpServletRequestAdapter(resourceRequest),
						new HttpServletResponseAdapter(resourceResponse), EmptyServletFilterChain.INSTANCE);
				}
				catch (AccessDeniedException e) {
					resourceResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

					return;
				}
				catch (ServletException e) {
					throw new PortletException(e);
				}
			}
		}

		portletFilterChain.doFilter(resourceRequest, resourceResponse);
	}

	@Override
	public void doFilter(RenderRequest renderRequest, RenderResponse renderResponse, FilterChain filterChain)
		throws IOException, PortletException {

		RenderParameters renderParameters = renderRequest.getRenderParameters();

		if (renderParameters.getValue(ACCESS_DENIED) != null) {
			renderError(PortletApplicationContextUtils.getWebApplicationContext(portletContext),
				renderRequest.getLocale(), renderResponse.getWriter(), "access-denied", "Access Denied");
		}
		else {
			filterChain.doFilter(renderRequest, renderResponse);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws PortletException {
		portletContext = filterConfig.getPortletContext();
	}

	protected void renderError(Writer writer, String message) throws IOException {

		writer.write("<span class=\"portlet-msg-error\">");
		writer.write(message);
		writer.write("</span>");
	}

	protected void renderError(ApplicationContext applicationContext, Locale locale, Writer writer, String messageKey,
		String defaultMessage) throws IOException {

		String message = defaultMessage;

		AbstractMessageSource messageSource = _getBean(applicationContext, AbstractMessageSource.class);

		if (messageSource != null) {
			message = messageSource.getMessage(messageKey, null, message, locale);
		}

		renderError(writer, message);
	}

	private <T> T _getBean(ApplicationContext applicationContext, Class<T> beanType) {
		String[] beanNamesForType = applicationContext.getBeanNamesForType(beanType);

		if (beanNamesForType.length > 0) {
			return applicationContext.getBean(beanType);
		}

		return null;
	}

	private static class EmptyServletFilterChain implements javax.servlet.FilterChain {

		public static EmptyServletFilterChain INSTANCE = new EmptyServletFilterChain();

		@Override
		public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException,
			ServletException {
			// no-op
		}
	}
}
