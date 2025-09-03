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

import java.util.Collection;

import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;


/**
 * Mock implementation of the {@link jakarta.portlet.RenderResponse} interface.
 *
 * @author  John A. Lewis
 * @author  Juergen Hoeller
 * @since   2.0
 */
public class MockRenderResponse extends MockMimeResponse implements RenderResponse {

	private String title;

	private Collection<? extends PortletMode> nextPossiblePortletModes;

	/**
	 * Create a new MockRenderResponse with a default {@link MockPortalContext}.
	 *
	 * @see  MockPortalContext
	 */
	public MockRenderResponse() {
		super();
	}

	/**
	 * Create a new MockRenderResponse.
	 *
	 * @param  portalContext  the PortalContext defining the supported PortletModes and WindowStates
	 */
	public MockRenderResponse(PortalContext portalContext) {
		super(portalContext);
	}

	/**
	 * Create a new MockRenderResponse.
	 *
	 * @param  portalContext  the PortalContext defining the supported PortletModes and WindowStates
	 * @param  request        the corresponding render request that this response is generated for
	 */
	public MockRenderResponse(PortalContext portalContext, RenderRequest request) {
		super(portalContext, request);
	}

	public Collection<? extends PortletMode> getNextPossiblePortletModes() {
		return this.nextPossiblePortletModes;
	}

	public String getTitle() {
		return this.title;
	}

	@Override
	public void setNextPossiblePortletModes(Collection<? extends PortletMode> portletModes) {
		this.nextPossiblePortletModes = portletModes;
	}

	// ---------------------------------------------------------------------
	// RenderResponse methods
	// ---------------------------------------------------------------------

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

}
