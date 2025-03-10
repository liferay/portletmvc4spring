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
package com.liferay.portletmvc4spring.context;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import jakarta.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import org.springframework.context.ApplicationContext;

import org.springframework.core.env.MutablePropertySources;

import org.springframework.util.Assert;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.SessionScope;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * Convenience methods for retrieving the root {@link WebApplicationContext} for a given {@link PortletContext}. This is
 * useful for programmatically accessing a Spring application context from within custom Portlet implementations.
 *
 * @author  Juergen Hoeller
 * @author  John A. Lewis
 * @since   2.0
 * @see     org.springframework.web.context.ContextLoader
 * @see     org.springframework.web.context.support.WebApplicationContextUtils
 * @see     com.liferay.portletmvc4spring.FrameworkPortlet
 * @see     com.liferay.portletmvc4spring.DispatcherPortlet
 */
public abstract class PortletApplicationContextUtils {

	private static final Log logger = LogFactory.getLog(PortletApplicationContextUtils.class);

	/**
	 * Find the root {@link WebApplicationContext} for this web app, typically loaded via {@link
	 * org.springframework.web.context.ContextLoaderListener}.
	 *
	 * <p>Will rethrow an exception that happened on root context startup, to differentiate between a failed context
	 * startup and no context at all.
	 *
	 * @param   pc  PortletContext to find the web application context for
	 *
	 * @return  the root WebApplicationContext for this web app (typed to ApplicationContext to avoid a Servlet API
	 *          dependency; can usually be casted to WebApplicationContext, but there shouldn't be a need to)
	 *
	 * @throws  IllegalStateException  if the root WebApplicationContext could not be found
	 *
	 * @see     org.springframework.web.context.WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
	 */
	public static ApplicationContext getRequiredWebApplicationContext(PortletContext pc) throws IllegalStateException {
		ApplicationContext wac = getWebApplicationContext(pc);

		if (wac == null) {
			throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
		}

		return wac;
	}

	/**
	 * Find the root {@link WebApplicationContext} for this web app, typically loaded via {@link
	 * org.springframework.web.context.ContextLoaderListener}.
	 *
	 * <p>Will rethrow an exception that happened on root context startup, to differentiate between a failed context
	 * startup and no context at all.
	 *
	 * @param   pc  PortletContext to find the web application context for
	 *
	 * @return  the root WebApplicationContext for this web app, or {@code null} if none (typed to ApplicationContext to
	 *          avoid a Servlet API dependency; can usually be casted to WebApplicationContext, but there shouldn't be a
	 *          need to)
	 *
	 * @see     org.springframework.web.context.WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
	 */
	public static ApplicationContext getWebApplicationContext(PortletContext pc) {
		Assert.notNull(pc, "PortletContext must not be null");

		Object attr = null;

		try {
			attr = pc.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			attr = e;
		}

		if (attr == null) {
			return null;
		}

		if (attr instanceof RuntimeException) {
			throw (RuntimeException) attr;
		}

		if (attr instanceof Error) {
			throw (Error) attr;
		}

		if (!(attr instanceof ApplicationContext)) {
			throw new IllegalStateException("Root context attribute is not of type WebApplicationContext: " + attr);
		}

		return (ApplicationContext) attr;
	}

	/**
	 * Replace {@code Servlet}- and {@code Portlet}-based {@link
	 * org.springframework.core.env.PropertySource.StubPropertySource stub property sources} with actual instances
	 * populated with the given {@code servletContext}, {@code portletContext} and {@code portletConfig} objects.
	 *
	 * <p>This method is idempotent with respect to the fact it may be called any number of times but will perform
	 * replacement of stub property sources with their corresponding actual property sources once and only once.
	 *
	 * @param  propertySources  the {@link MutablePropertySources} to initialize (must not be {@code null})
	 * @param  servletContext   the current {@link ServletContext} (ignored if {@code null} or if the {@link
	 *                          org.springframework.web.context.support.StandardServletEnvironment#SERVLET_CONTEXT_PROPERTY_SOURCE_NAME
	 *                          servlet context property source} has already been initialized)
	 * @param  portletContext   the current {@link PortletContext} (ignored if {@code null} or if the {@link
	 *                          StandardPortletEnvironment#PORTLET_CONTEXT_PROPERTY_SOURCE_NAME portlet context
	 *                          property source} has already been initialized)
	 * @param  portletConfig    the current {@link PortletConfig} (ignored if {@code null} or if the {@link
	 *                          StandardPortletEnvironment#PORTLET_CONFIG_PROPERTY_SOURCE_NAME portlet config property
	 *                          source} has already been initialized)
	 *
	 * @see    org.springframework.core.env.PropertySource.StubPropertySource
	 * @see    org.springframework.web.context.support.WebApplicationContextUtils#initServletPropertySources(MutablePropertySources,
	 *         ServletContext)
	 * @see    org.springframework.core.env.ConfigurableEnvironment#getPropertySources()
	 */
	public static void initPortletPropertySources(MutablePropertySources propertySources, ServletContext servletContext,
		PortletContext portletContext, PortletConfig portletConfig) {

		Assert.notNull(propertySources, "'propertySources' must not be null");
		WebApplicationContextUtils.initServletPropertySources(propertySources, servletContext);

		if ((portletContext != null) &&
				propertySources.contains(StandardPortletEnvironment.PORTLET_CONTEXT_PROPERTY_SOURCE_NAME)) {
			propertySources.replace(StandardPortletEnvironment.PORTLET_CONTEXT_PROPERTY_SOURCE_NAME,
				new PortletContextPropertySource(StandardPortletEnvironment.PORTLET_CONTEXT_PROPERTY_SOURCE_NAME,
					portletContext));
		}

		if ((portletConfig != null) &&
				propertySources.contains(StandardPortletEnvironment.PORTLET_CONFIG_PROPERTY_SOURCE_NAME)) {
			propertySources.replace(StandardPortletEnvironment.PORTLET_CONFIG_PROPERTY_SOURCE_NAME,
				new PortletConfigPropertySource(StandardPortletEnvironment.PORTLET_CONFIG_PROPERTY_SOURCE_NAME,
					portletConfig));
		}
	}

	/**
	 * Register web-specific environment beans ("contextParameters", "contextAttributes") with the given BeanFactory, as
	 * used by the Portlet ApplicationContext.
	 *
	 * @param  bf              the BeanFactory to configure
	 * @param  servletContext  the ServletContext that we're running within
	 * @param  portletContext  the PortletContext that we're running within
	 * @param  portletConfig   the PortletConfig of the containing Portlet
	 */
	static void registerEnvironmentBeans(ConfigurableListableBeanFactory bf, ServletContext servletContext,
		PortletContext portletContext, PortletConfig portletConfig) {

		if ((servletContext != null) && !bf.containsBean(WebApplicationContext.SERVLET_CONTEXT_BEAN_NAME)) {
			bf.registerSingleton(WebApplicationContext.SERVLET_CONTEXT_BEAN_NAME, servletContext);
		}

		if ((portletContext != null) &&
				!bf.containsBean(ConfigurablePortletApplicationContext.PORTLET_CONTEXT_BEAN_NAME)) {
			bf.registerSingleton(ConfigurablePortletApplicationContext.PORTLET_CONTEXT_BEAN_NAME, portletContext);
		}

		if ((portletConfig != null) &&
				!bf.containsBean(ConfigurablePortletApplicationContext.PORTLET_CONFIG_BEAN_NAME)) {
			bf.registerSingleton(ConfigurablePortletApplicationContext.PORTLET_CONFIG_BEAN_NAME, portletConfig);
		}

		if (!bf.containsBean(WebApplicationContext.CONTEXT_PARAMETERS_BEAN_NAME)) {
			Map<String, String> parameterMap = new HashMap<String, String>();

			if (portletContext != null) {
				Enumeration<String> paramNameEnum = portletContext.getInitParameterNames();

				while (paramNameEnum.hasMoreElements()) {
					String paramName = paramNameEnum.nextElement();
					parameterMap.put(paramName, portletContext.getInitParameter(paramName));
				}
			}

			if (portletConfig != null) {
				Enumeration<String> paramNameEnum = portletConfig.getInitParameterNames();

				while (paramNameEnum.hasMoreElements()) {
					String paramName = paramNameEnum.nextElement();
					parameterMap.put(paramName, portletConfig.getInitParameter(paramName));
				}
			}

			bf.registerSingleton(WebApplicationContext.CONTEXT_PARAMETERS_BEAN_NAME,
				Collections.unmodifiableMap(parameterMap));
		}

		if (!bf.containsBean(WebApplicationContext.CONTEXT_ATTRIBUTES_BEAN_NAME)) {
			Map<String, Object> attributeMap = new HashMap<String, Object>();

			if (portletContext != null) {
				Enumeration<String> attrNameEnum = portletContext.getAttributeNames();

				while (attrNameEnum.hasMoreElements()) {
					String attrName = attrNameEnum.nextElement();
					attributeMap.put(attrName, portletContext.getAttribute(attrName));
				}
			}

			bf.registerSingleton(WebApplicationContext.CONTEXT_ATTRIBUTES_BEAN_NAME,
				Collections.unmodifiableMap(attributeMap));
		}
	}

	/**
	 * Register web-specific scopes ("request", "session", "globalSession") with the given BeanFactory, as used by the
	 * Portlet ApplicationContext.
	 *
	 * @param  bf  the BeanFactory to configure
	 * @param  pc  the PortletContext that we're running within
	 */
	static void registerPortletApplicationScopes(ConfigurableListableBeanFactory bf, PortletContext pc) {
		bf.registerScope(WebApplicationContext.SCOPE_REQUEST, new RequestScope());
		bf.registerScope(WebApplicationContext.SCOPE_SESSION, new PortletSessionScope(false));
		bf.registerScope(PortletApplicationContext.SCOPE_GLOBAL_SESSION, new PortletSessionScope(true));

		if (pc != null) {
			PortletContextScope appScope = new PortletContextScope(pc);
			bf.registerScope(WebApplicationContext.SCOPE_APPLICATION, appScope);

			// Register as PortletContext attribute, for ContextCleanupListener to detect it.
			pc.setAttribute(PortletContextScope.class.getName(), appScope);
		}

		bf.registerResolvableDependency(PortletRequest.class, new RequestObjectFactory());
		bf.registerResolvableDependency(PortletResponse.class, new ResponseObjectFactory());
		bf.registerResolvableDependency(PortletSession.class, new SessionObjectFactory());
		bf.registerResolvableDependency(WebRequest.class, new WebRequestObjectFactory());
	}

	/**
	 * Return the current RequestAttributes instance as PortletRequestAttributes.
	 *
	 * @see  RequestContextHolder#currentRequestAttributes()
	 */
	private static PortletRequestAttributes currentRequestAttributes() {
		RequestAttributes requestAttr = RequestContextHolder.currentRequestAttributes();

		if (!(requestAttr instanceof PortletRequestAttributes)) {
			throw new IllegalStateException("Current request is not a portlet request");
		}

		return (PortletRequestAttributes) requestAttr;
	}

	/**
	 * Factory that exposes the current request object on demand.
	 */
	@SuppressWarnings("serial")
	private static class RequestObjectFactory implements ObjectFactory<PortletRequest>, Serializable {

		@Override
		public PortletRequest getObject() {
			return currentRequestAttributes().getRequest();
		}

		@Override
		public String toString() {
			return "Current PortletRequest";
		}
	}

	/**
	 * Factory that exposes the current response object on demand.
	 */
	@SuppressWarnings("serial")
	private static class ResponseObjectFactory implements ObjectFactory<PortletResponse>, Serializable {

		@Override
		public PortletResponse getObject() {
			PortletResponse response = currentRequestAttributes().getResponse();

			if (response == null) {
				throw new IllegalStateException("Current portlet response not available");
			}

			return response;
		}

		@Override
		public String toString() {
			return "Current PortletResponse";
		}
	}

	/**
	 * Factory that exposes the current session object on demand.
	 */
	@SuppressWarnings("serial")
	private static class SessionObjectFactory implements ObjectFactory<PortletSession>, Serializable {

		@Override
		public PortletSession getObject() {
			return currentRequestAttributes().getRequest().getPortletSession();
		}

		@Override
		public String toString() {
			return "Current PortletSession";
		}
	}

	/**
	 * Factory that exposes the current WebRequest object on demand.
	 */
	@SuppressWarnings("serial")
	private static class WebRequestObjectFactory implements ObjectFactory<WebRequest>, Serializable {

		@Override
		public WebRequest getObject() {
			PortletRequestAttributes requestAttr = currentRequestAttributes();

			return new PortletWebRequest(requestAttr.getRequest(), requestAttr.getResponse());
		}

		@Override
		public String toString() {
			return "Current PortletWebRequest";
		}
	}

}
