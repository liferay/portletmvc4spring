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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.StateAwareResponse;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;
import javax.xml.namespace.QName;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;


/**
 * Mock implementation of the {@link jakarta.portlet.StateAwareResponse} interface.
 *
 * @author  Juergen Hoeller
 * @since   3.0
 */
public class MockStateAwareResponse extends MockPortletResponse implements StateAwareResponse {

	private final Map<QName, Serializable> events = new HashMap<QName, Serializable>();

	private MutableRenderParameters mutableRenderParameters;

	private PortletMode portletMode;

	private final Map<String, String[]> renderParameters = new LinkedHashMap<String, String[]>();

	private WindowState windowState;

	/**
	 * Create a new MockActionResponse with a default {@link MockPortalContext}.
	 *
	 * @see  org.springframework.mock.web.portlet.MockPortalContext
	 */
	public MockStateAwareResponse() {
		super();
	}

	/**
	 * Create a new MockActionResponse.
	 *
	 * @param  portalContext  the PortalContext defining the supported PortletModes and WindowStates
	 */
	public MockStateAwareResponse(PortalContext portalContext) {
		super(portalContext);
	}

	public Serializable getEvent(QName name) {
		return this.events.get(name);
	}

	public Serializable getEvent(String name) {
		return this.events.get(new QName(name));
	}

	public Iterator<QName> getEventNames() {
		return this.events.keySet().iterator();
	}

	@Override
	public PortletMode getPortletMode() {
		return this.portletMode;
	}

	public String getRenderParameter(String key) {
		Assert.notNull(key, "Parameter key must not be null");

		String[] arr = this.renderParameters.get(key);

		return (((arr != null) && (arr.length > 0)) ? arr[0] : null);
	}

	@Override
	public Map<String, String[]> getRenderParameterMap() {
		return Collections.unmodifiableMap(this.renderParameters);
	}

	public Iterator<String> getRenderParameterNames() {
		return this.renderParameters.keySet().iterator();
	}

	@Override
	public MutableRenderParameters getRenderParameters() {

		if (mutableRenderParameters == null) {
			mutableRenderParameters = new MockMutableRenderParameters();
		}

		return mutableRenderParameters;
	}

	public String[] getRenderParameterValues(String key) {
		Assert.notNull(key, "Parameter key must not be null");

		return this.renderParameters.get(key);
	}

	@Override
	public WindowState getWindowState() {
		return this.windowState;
	}

	@Override
	public void removePublicRenderParameter(String name) {
		this.renderParameters.remove(name);
	}

	@Override
	public void setEvent(QName name, Serializable value) {
		this.events.put(name, value);
	}

	@Override
	public void setEvent(String name, Serializable value) {
		this.events.put(new QName(name), value);
	}

	@Override
	public void setPortletMode(PortletMode portletMode) throws PortletModeException {

		if (!CollectionUtils.contains(getPortalContext().getSupportedPortletModes(), portletMode)) {
			throw new PortletModeException("PortletMode not supported", portletMode);
		}

		this.portletMode = portletMode;
	}

	@Override
	public void setRenderParameter(String key, String value) {
		Assert.notNull(key, "Parameter key must not be null");
		Assert.notNull(value, "Parameter value must not be null");
		this.renderParameters.put(key, new String[] { value });
	}

	@Override
	public void setRenderParameter(String key, String[] values) {
		Assert.notNull(key, "Parameter key must not be null");
		Assert.notNull(values, "Parameter values must not be null");
		this.renderParameters.put(key, values);
	}

	@Override
	public void setRenderParameters(Map<String, String[]> parameters) {
		Assert.notNull(parameters, "Parameters Map must not be null");
		this.renderParameters.clear();
		this.renderParameters.putAll(parameters);
	}

	@Override
	public void setWindowState(WindowState windowState) throws WindowStateException {

		if (!CollectionUtils.contains(getPortalContext().getSupportedWindowStates(), windowState)) {
			throw new WindowStateException("WindowState not supported", windowState);
		}

		this.windowState = windowState;
	}

}
