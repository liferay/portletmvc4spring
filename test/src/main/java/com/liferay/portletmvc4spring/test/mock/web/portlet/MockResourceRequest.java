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
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletAsyncContext;
import jakarta.portlet.PortletContext;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.ResourceParameters;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.servlet.DispatcherType;


/**
 * Mock implementation of the {@link jakarta.portlet.ResourceRequest} interface.
 *
 * @author  Juergen Hoeller
 * @since   3.0
 */
public class MockResourceRequest extends MockClientDataRequest implements ResourceRequest {

	private String resourceID;

	private String cacheability;

	private final Map<String, String[]> privateRenderParameterMap = new LinkedHashMap<String, String[]>();

	private ResourceParameters resourceParameters;

	/**
	 * Create a new MockResourceRequest with a default {@link MockPortalContext} and a default {@link
	 * MockPortletContext}.
	 *
	 * @see  com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortalContext
	 * @see  com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortletContext
	 */
	public MockResourceRequest() {
		super();
	}

	/**
	 * Create a new MockResourceRequest with a default {@link MockPortalContext} and a default {@link
	 * MockPortletContext}.
	 *
	 * @param  resourceID  the resource id for this request
	 */
	public MockResourceRequest(String resourceID) {
		super();
		this.resourceID = resourceID;
	}

	/**
	 * Create a new MockResourceRequest with a default {@link MockPortalContext} and a default {@link
	 * MockPortletContext}.
	 *
	 * @param  url  the resource URL for this request
	 */
	public MockResourceRequest(MockResourceURL url) {
		super();
		this.resourceID = url.getResourceID();
		this.cacheability = url.getCacheability();
	}

	/**
	 * Create a new MockResourceRequest with a default {@link MockPortalContext}.
	 *
	 * @param  portletContext  the PortletContext that the request runs in
	 */
	public MockResourceRequest(PortletContext portletContext) {
		super(portletContext);
	}

	/**
	 * Create a new MockResourceRequest.
	 *
	 * @param  portalContext   the PortalContext that the request runs in
	 * @param  portletContext  the PortletContext that the request runs in
	 */
	public MockResourceRequest(PortalContext portalContext, PortletContext portletContext) {
		super(portalContext, portletContext);
	}

	public void addPrivateRenderParameter(String key, String value) {
		this.privateRenderParameterMap.put(key, new String[] { value });
	}

	public void addPrivateRenderParameter(String key, String[] values) {
		this.privateRenderParameterMap.put(key, values);
	}

	@Override
	public String getCacheability() {
		return this.cacheability;
	}

	@Override
	public DispatcherType getDispatcherType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getETag() {
		return getProperty(RenderRequest.ETAG);
	}

	@Override
	public PortletAsyncContext getPortletAsyncContext() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, String[]> getPrivateRenderParameterMap() {
		return Collections.unmodifiableMap(this.privateRenderParameterMap);
	}

	@Override
	public String getResourceID() {
		return this.resourceID;
	}

	@Override
	public ResourceParameters getResourceParameters() {

		if (resourceParameters == null) {
			resourceParameters = new MockResourceParameters();
		}

		return resourceParameters;
	}

	@Override
	public boolean isAsyncStarted() {
		return false;
	}

	@Override
	public boolean isAsyncSupported() {
		return false;
	}

	public void setCacheability(String cacheLevel) {
		this.cacheability = cacheLevel;
	}

	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}

	@Override
	public PortletAsyncContext startPortletAsync() throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	public PortletAsyncContext startPortletAsync(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IllegalStateException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected String getLifecyclePhase() {
		return RESOURCE_PHASE;
	}

}
