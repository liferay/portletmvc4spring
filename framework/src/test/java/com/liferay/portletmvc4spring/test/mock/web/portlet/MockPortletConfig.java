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
package com.liferay.portletmvc4spring.test.mock.web.portlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.WindowState;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.springframework.util.Assert;


/**
 * Mock implementation of the {@link jakarta.portlet.PortletConfig} interface.
 *
 * @author  John A. Lewis
 * @author  Juergen Hoeller
 * @since   2.0
 */
public class MockPortletConfig implements PortletConfig {

	private final PortletContext portletContext;

	private final String portletName;

	private final Map<Locale, ResourceBundle> resourceBundles = new HashMap<Locale, ResourceBundle>();

	private final Map<String, String> initParameters = new LinkedHashMap<String, String>();

	private final Set<String> publicRenderParameterNames = new LinkedHashSet<String>();

	private String defaultNamespace = XMLConstants.NULL_NS_URI;

	private final Set<QName> publishingEventQNames = new LinkedHashSet<QName>();

	private final Set<QName> processingEventQNames = new LinkedHashSet<QName>();

	private final Set<Locale> supportedLocales = new LinkedHashSet<Locale>();

	private final Map<String, String[]> containerRuntimeOptions = new LinkedHashMap<String, String[]>();

	/**
	 * Create a new MockPortletConfig with a default {@link MockPortletContext}.
	 */
	public MockPortletConfig() {
		this(null, "");
	}

	/**
	 * Create a new MockPortletConfig with a default {@link MockPortletContext}.
	 *
	 * @param  portletName  the name of the portlet
	 */
	public MockPortletConfig(String portletName) {
		this(null, portletName);
	}

	/**
	 * Create a new MockPortletConfig.
	 *
	 * @param  portletContext  the PortletContext that the portlet runs in
	 */
	public MockPortletConfig(PortletContext portletContext) {
		this(portletContext, "");
	}

	/**
	 * Create a new MockPortletConfig.
	 *
	 * @param  portletContext  the PortletContext that the portlet runs in
	 * @param  portletName     the name of the portlet
	 */
	public MockPortletConfig(PortletContext portletContext, String portletName) {
		this.portletContext = ((portletContext != null) ? portletContext : new MockPortletContext());
		this.portletName = portletName;
	}

	public void addContainerRuntimeOption(String key, String value) {
		this.containerRuntimeOptions.put(key, new String[] { value });
	}

	public void addContainerRuntimeOption(String key, String[] values) {
		this.containerRuntimeOptions.put(key, values);
	}

	public void addInitParameter(String name, String value) {
		Assert.notNull(name, "Parameter name must not be null");
		this.initParameters.put(name, value);
	}

	public void addProcessingEventQName(QName name) {
		this.processingEventQNames.add(name);
	}

	public void addPublicRenderParameterName(String name) {
		this.publicRenderParameterNames.add(name);
	}

	public void addPublishingEventQName(QName name) {
		this.publishingEventQNames.add(name);
	}

	public void addSupportedLocale(Locale locale) {
		this.supportedLocales.add(locale);
	}

	@Override
	public Map<String, String[]> getContainerRuntimeOptions() {
		return Collections.unmodifiableMap(this.containerRuntimeOptions);
	}

	@Override
	public String getDefaultNamespace() {
		return this.defaultNamespace;
	}

	@Override
	public String getInitParameter(String name) {
		Assert.notNull(name, "Parameter name must not be null");

		return this.initParameters.get(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return Collections.enumeration(this.initParameters.keySet());
	}

	@Override
	public PortletContext getPortletContext() {
		return this.portletContext;
	}

	@Override
	public Enumeration<PortletMode> getPortletModes(String mimeType) {
		return Collections.enumeration(Collections.<PortletMode>emptySet());
	}

	@Override
	public String getPortletName() {
		return this.portletName;
	}

	@Override
	public Enumeration<QName> getProcessingEventQNames() {
		return Collections.enumeration(this.processingEventQNames);
	}

	@Override
	public Map<String, QName> getPublicRenderParameterDefinitions() {
		return Collections.<String, QName>emptyMap();
	}

	@Override
	public Enumeration<String> getPublicRenderParameterNames() {
		return Collections.enumeration(this.publicRenderParameterNames);
	}

	@Override
	public Enumeration<QName> getPublishingEventQNames() {
		return Collections.enumeration(this.publishingEventQNames);
	}

	@Override
	public ResourceBundle getResourceBundle(Locale locale) {
		Assert.notNull(locale, "Locale must not be null");

		return this.resourceBundles.get(locale);
	}

	@Override
	public Enumeration<Locale> getSupportedLocales() {
		return Collections.enumeration(this.supportedLocales);
	}

	@Override
	public Enumeration<WindowState> getWindowStates(String mimeType) {
		return Collections.enumeration(Collections.<WindowState>emptySet());
	}

	public void setDefaultNamespace(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}

	public void setResourceBundle(Locale locale, ResourceBundle resourceBundle) {
		Assert.notNull(locale, "Locale must not be null");
		this.resourceBundles.put(locale, resourceBundle);
	}
}
