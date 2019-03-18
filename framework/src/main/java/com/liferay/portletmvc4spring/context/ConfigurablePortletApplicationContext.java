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
package com.liferay.portletmvc4spring.context;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;

import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.web.context.WebApplicationContext;


/**
 * Interface to be implemented by configurable portlet application contexts. Supported by {@link
 * com.liferay.portletmvc4spring.FrameworkPortlet}.
 *
 * <p>Note: The setters of this interface need to be called before an invocation of the {@link #refresh} method
 * inherited from {@link org.springframework.context.ConfigurableApplicationContext}. They do not cause an
 * initialization of the context on their own.
 *
 * @author  Juergen Hoeller
 * @author  William G. Thompson, Jr.
 * @author  John A. Lewis
 * @since   2.0
 * @see     #refresh
 * @see     org.springframework.web.context.ContextLoader#createWebApplicationContext
 * @see     com.liferay.portletmvc4spring.FrameworkPortlet#createPortletApplicationContext
 * @see     org.springframework.web.context.ConfigurableWebApplicationContext
 */
public interface ConfigurablePortletApplicationContext extends WebApplicationContext, ConfigurableApplicationContext {

	/** Prefix for ApplicationContext ids that refer to portlet name. */
	String APPLICATION_CONTEXT_ID_PREFIX = WebApplicationContext.class.getName() + ":";

	/**
	 * Name of the PortletContext environment bean in the factory.
	 *
	 * @see  javax.portlet.PortletContext
	 */
	String PORTLET_CONTEXT_BEAN_NAME = "portletContext";

	/**
	 * Name of the PortletConfig environment bean in the factory.
	 *
	 * @see  javax.portlet.PortletConfig
	 */
	String PORTLET_CONFIG_BEAN_NAME = "portletConfig";

	/**
	 * Return the config locations for this web application context, or {@code null} if none specified.
	 */
	String[] getConfigLocations();

	/**
	 * Return the namespace for this web application context, if any.
	 */
	String getNamespace();

	/**
	 * Return the PortletConfig for this portlet application context, if any.
	 */
	PortletConfig getPortletConfig();

	/**
	 * Return the standard Portlet API PortletContext for this application.
	 */
	PortletContext getPortletContext();

	/**
	 * Set the config locations for this portlet application context in init-param style, i.e. with distinct locations
	 * separated by commas, semicolons or whitespace.
	 *
	 * <p>If not set, the implementation is supposed to use a default for the given namespace.
	 */
	void setConfigLocation(String configLocation);

	/**
	 * Set the config locations for this portlet application context.
	 *
	 * <p>If not set, the implementation is supposed to use a default for the given namespace.
	 */
	void setConfigLocations(String... configLocations);

	/**
	 * Set the namespace for this portlet application context, to be used for building a default context config
	 * location.
	 */
	void setNamespace(String namespace);

	/**
	 * Set the PortletConfig for this portlet application context.
	 *
	 * @see  #refresh()
	 */
	void setPortletConfig(PortletConfig portletConfig);

	/**
	 * Set the PortletContext for this portlet application context.
	 *
	 * <p>Does not cause an initialization of the context: refresh needs to be called after the setting of all
	 * configuration properties.
	 *
	 * @see  #refresh()
	 */
	void setPortletContext(PortletContext portletContext);

}
