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

import java.io.IOException;
import java.util.Map;

import jakarta.portlet.ActionResponse;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletModeException;
import jakarta.portlet.RenderURL;
import jakarta.portlet.WindowState;
import jakarta.portlet.WindowStateException;

import org.springframework.util.Assert;


/**
 * Mock implementation of the {@link jakarta.portlet.ActionResponse} interface.
 *
 * @author  John A. Lewis
 * @author  Juergen Hoeller
 * @since   2.0
 */
public class MockActionResponse extends MockStateAwareResponse implements ActionResponse {

	private boolean redirectAllowed = true;

	private String redirectedUrl;

	/**
	 * Create a new MockActionResponse with a default {@link MockPortalContext}.
	 *
	 * @see  MockPortalContext
	 */
	public MockActionResponse() {
		super();
	}

	/**
	 * Create a new MockActionResponse.
	 *
	 * @param  portalContext  the PortalContext defining the supported PortletModes and WindowStates
	 */
	public MockActionResponse(PortalContext portalContext) {
		super(portalContext);
	}

	@Override
	public RenderURL createRedirectURL(MimeResponse.Copy copy) throws IllegalStateException {
		return new MockRenderURL(getPortalContext(), copy);
	}

	public String getRedirectedUrl() {
		return this.redirectedUrl;
	}

	@Override
	public void sendRedirect(String location) throws IOException {

		if (!this.redirectAllowed) {
			throw new IllegalStateException(
				"Cannot call sendRedirect after windowState, portletMode, or renderParameters have been set");
		}

		Assert.notNull(location, "Redirect URL must not be null");
		this.redirectedUrl = location;
	}

	@Override
	public void sendRedirect(String location, String renderUrlParamName) throws IOException {
		sendRedirect(location);

		if (renderUrlParamName != null) {
			setRenderParameter(renderUrlParamName, location);
		}
	}

	@Override
	public void setPortletMode(PortletMode portletMode) throws PortletModeException {

		if (this.redirectedUrl != null) {
			throw new IllegalStateException("Cannot set PortletMode after sendRedirect has been called");
		}

		super.setPortletMode(portletMode);
		this.redirectAllowed = false;
	}

	@Override
	public void setRenderParameter(String key, String value) {

		if (this.redirectedUrl != null) {
			throw new IllegalStateException("Cannot set render parameters after sendRedirect has been called");
		}

		super.setRenderParameter(key, value);
		this.redirectAllowed = false;
	}

	@Override
	public void setRenderParameter(String key, String[] values) {

		if (this.redirectedUrl != null) {
			throw new IllegalStateException("Cannot set render parameters after sendRedirect has been called");
		}

		super.setRenderParameter(key, values);
		this.redirectAllowed = false;
	}

	@Override
	public void setRenderParameters(Map<String, String[]> parameters) {

		if (this.redirectedUrl != null) {
			throw new IllegalStateException("Cannot set render parameters after sendRedirect has been called");
		}

		super.setRenderParameters(parameters);
		this.redirectAllowed = false;
	}

	@Override
	public void setWindowState(WindowState windowState) throws WindowStateException {

		if (this.redirectedUrl != null) {
			throw new IllegalStateException("Cannot set WindowState after sendRedirect has been called");
		}

		super.setWindowState(windowState);
		this.redirectAllowed = false;
	}

}
