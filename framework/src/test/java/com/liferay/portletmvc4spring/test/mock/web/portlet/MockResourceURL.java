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

import jakarta.portlet.MutableResourceParameters;
import jakarta.portlet.PortletMode;
import jakarta.portlet.ResourceURL;
import jakarta.portlet.WindowState;


/**
 * Mock implementation of the {@link jakarta.portlet.ResourceURL} interface.
 *
 * @author  Juergen Hoeller
 * @since   3.0
 */
public class MockResourceURL extends MockBaseURL implements ResourceURL {

	private String cacheability;

	private MutableResourceParameters mutableResourceParameters;

	private PortletMode portletMode;

	private String resourceID;

	private WindowState windowState;

	public MockResourceURL() {
	}

	public MockResourceURL(PortletMode portletMode, WindowState windowState) {
		this.portletMode = portletMode;
		this.windowState = windowState;
	}

	@Override
	public String getCacheability() {
		return this.cacheability;
	}

	@Override
	public PortletMode getPortletMode() {
		return portletMode;
	}

	public String getResourceID() {
		return this.resourceID;
	}

	// ---------------------------------------------------------------------
	// ResourceURL methods
	// ---------------------------------------------------------------------

	@Override
	public MutableResourceParameters getResourceParameters() {

		if (mutableResourceParameters == null) {
			mutableResourceParameters = new MockMutableResourceParameters();
		}

		return mutableResourceParameters;
	}

	@Override
	public WindowState getWindowState() {
		return windowState;
	}

	@Override
	public void setCacheability(String cacheLevel) {
		this.cacheability = cacheLevel;
	}

	@Override
	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(encodeParameter("resourceID", this.resourceID));

		if (this.cacheability != null) {
			sb.append(";").append(encodeParameter("cacheability", this.cacheability));
		}

		for (Map.Entry<String, String[]> entry : this.parameters.entrySet()) {
			sb.append(";").append(encodeParameter("param_" + entry.getKey(), entry.getValue()));
		}

		return (isSecure() ? "https:" : "http:") + "//localhost/mockportlet?" + sb.toString();
	}
}
