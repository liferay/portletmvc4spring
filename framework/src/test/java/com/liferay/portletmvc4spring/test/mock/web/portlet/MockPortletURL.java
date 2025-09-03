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

import java.util.Map;

import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;
import jakarta.portlet.annotations.PortletSerializable;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;


/**
 * Mock implementation of the {@link jakarta.portlet.PortletURL} interface.
 *
 * @author  John A. Lewis
 * @author  Juergen Hoeller
 * @since   2.0
 */
public class MockPortletURL extends MockBaseURL implements PortletURL {

	public static final String URL_TYPE_RENDER = "render";

	public static final String URL_TYPE_ACTION = "action";

	private final PortalContext portalContext;

	private final String urlType;

	private WindowState windowState;

	private PortletMode portletMode;

	/**
	 * Create a new MockPortletURL for the given URL type.
	 *
	 * @param  portalContext  the PortalContext defining the supported PortletModes and WindowStates
	 * @param  urlType        the URL type, for example "render" or "action"
	 *
	 * @see    #URL_TYPE_RENDER
	 * @see    #URL_TYPE_ACTION
	 */
	public MockPortletURL(PortalContext portalContext, String urlType) {
		Assert.notNull(portalContext, "PortalContext is required");
		this.portalContext = portalContext;
		this.urlType = urlType;
	}

	@Override
	public PortletMode getPortletMode() {
		return this.portletMode;
	}

	@Override
	public WindowState getWindowState() {
		return this.windowState;
	}

	@Override
	public void removePublicRenderParameter(String name) {
		this.parameters.remove(name);
	}

	@Override
	public void setBeanParameter(PortletSerializable portletSerializable) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPortletMode(PortletMode portletMode) throws PortletModeException {

		if (!CollectionUtils.contains(this.portalContext.getSupportedPortletModes(), portletMode)) {
			throw new PortletModeException("PortletMode not supported", portletMode);
		}

		this.portletMode = portletMode;
	}

	// ---------------------------------------------------------------------
	// PortletURL methods
	// ---------------------------------------------------------------------

	@Override
	public void setWindowState(WindowState windowState) throws WindowStateException {

		if (!CollectionUtils.contains(this.portalContext.getSupportedWindowStates(), windowState)) {
			throw new WindowStateException("WindowState not supported", windowState);
		}

		this.windowState = windowState;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(encodeParameter("urlType", this.urlType));

		if (this.windowState != null) {
			sb.append(";").append(encodeParameter("windowState", this.windowState.toString()));
		}

		if (this.portletMode != null) {
			sb.append(";").append(encodeParameter("portletMode", this.portletMode.toString()));
		}

		for (Map.Entry<String, String[]> entry : this.parameters.entrySet()) {
			sb.append(";").append(encodeParameter("param_" + entry.getKey(), entry.getValue()));
		}

		return (isSecure() ? "https:" : "http:") + "//localhost/mockportlet?" + sb.toString();
	}

}
