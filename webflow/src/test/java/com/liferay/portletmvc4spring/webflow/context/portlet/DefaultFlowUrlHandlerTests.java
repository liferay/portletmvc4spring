package com.liferay.portletmvc4spring.webflow.context.portlet;

import com.liferay.portletmvc4spring.test.mock.web.portlet.MockActionRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockActionResponse;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortletRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockRenderRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockRenderResponse;

import junit.framework.TestCase;

public class DefaultFlowUrlHandlerTests extends TestCase {
	private DefaultFlowUrlHandler urlHandler = new DefaultFlowUrlHandler();
	private MockPortletRequest request = new MockPortletRequest();
	private MockActionRequest actionRequest = new MockActionRequest();
	private MockRenderRequest renderRequest = new MockRenderRequest();
	private MockActionResponse actionResponse = new MockActionResponse();
	private MockRenderResponse renderResponse = new MockRenderResponse();

	public void testGetFlowExecutionKey() {
		request.addParameter("execution", "12345");
		assertEquals("12345", urlHandler.getFlowExecutionKey(request));
	}

	public void testSetFlowExecutionRenderParameter() {
		urlHandler.setFlowExecutionRenderParameter("12345", actionResponse);
		assertEquals("12345", actionResponse.getRenderParameter("execution"));
	}

	public void testSetFlowExecutionInSession() {
		urlHandler.setFlowExecutionInSession("12345", renderRequest);
		assertEquals("12345", renderRequest.getPortletSession().getAttribute("execution"));
	}

	public void testSessionFlowExecutionRemoval() {
		urlHandler.setFlowExecutionInSession("12345", renderRequest);
		assertEquals("12345", urlHandler.getFlowExecutionKey(renderRequest));
		actionRequest.setParameter("execution", "12345");
		assertEquals("12345", urlHandler.getFlowExecutionKey(actionRequest));
		assertNull(actionRequest.getPortletSession().getAttribute("execution"));
	}

	public void testCreateFlowExecutionUrl() {
		String url = urlHandler.createFlowExecutionUrl("foo", "12345", renderResponse);
		assertEquals("http://localhost/mockportlet?urlType=action;param_execution=12345", url);
	}
}