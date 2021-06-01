/*
 * Copyright (c) 2000-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.portletmvc4spring.webflow.context.portlet;

import com.liferay.portletmvc4spring.test.mock.web.portlet.MockActionRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockActionResponse;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortletRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockRenderRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockRenderResponse;

import junit.framework.TestCase;


/**
 * @author  Fabian Bouché
 */
public class DefaultFlowUrlHandlerTests extends TestCase {
	private DefaultFlowUrlHandler urlHandler = new DefaultFlowUrlHandler();
	private MockPortletRequest request = new MockPortletRequest();
	private MockActionRequest actionRequest = new MockActionRequest();
	private MockRenderRequest renderRequest = new MockRenderRequest();
	private MockActionResponse actionResponse = new MockActionResponse();
	private MockRenderResponse renderResponse = new MockRenderResponse();

	public void testCreateFlowExecutionUrl() {
		String url = urlHandler.createFlowExecutionUrl("foo", "12345", renderResponse);
		assertEquals("http://localhost/mockportlet?urlType=action;param_execution=12345", url);
	}

	public void testGetFlowExecutionKey() {
		request.addParameter("execution", "12345");
		assertEquals("12345", urlHandler.getFlowExecutionKey(request));
	}

	public void testSessionFlowExecutionRemoval() {
		urlHandler.setFlowExecutionInSession("12345", renderRequest);
		assertEquals("12345", urlHandler.getFlowExecutionKey(renderRequest));
		actionRequest.setParameter("execution", "12345");
		assertEquals("12345", urlHandler.getFlowExecutionKey(actionRequest));
		assertNull(actionRequest.getPortletSession().getAttribute("execution"));
	}

	public void testSetFlowExecutionInSession() {
		urlHandler.setFlowExecutionInSession("12345", renderRequest);
		assertEquals("12345", renderRequest.getPortletSession().getAttribute("execution"));
	}

	public void testSetFlowExecutionRenderParameter() {
		urlHandler.setFlowExecutionRenderParameter("12345", actionResponse);
		assertEquals("12345", actionResponse.getRenderParameter("execution"));
	}
}
