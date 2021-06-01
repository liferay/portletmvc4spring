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
package com.liferay.portletmvc4spring.webflow.mvc.portlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.easymock.EasyMock;

import org.springframework.web.context.support.StaticWebApplicationContext;

import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;
import org.springframework.webflow.execution.repository.NoSuchFlowExecutionException;
import org.springframework.webflow.executor.FlowExecutionResult;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.test.MockFlowExecutionKey;

import com.liferay.portletmvc4spring.ModelAndView;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockActionRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockActionResponse;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortletContext;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockRenderRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockRenderResponse;
import com.liferay.portletmvc4spring.webflow.context.portlet.PortletExternalContext;

import junit.framework.TestCase;


/**
 * @author  Fabian Bouché
 */
public class FlowHandlerAdapterTests extends TestCase {

	private FlowHandlerAdapter controller;
	private FlowExecutor flowExecutor;
	private MockPortletContext portletContext;
	private MockActionRequest actionRequest;
	private MockActionResponse actionResponse;
	private MockRenderRequest renderRequest;
	private MockRenderResponse renderResponse;
	private PortletExternalContext actionContext;
	private PortletExternalContext renderContext;
	private FlowHandler flowHandler;
	private LocalAttributeMap<Object> flowInput = null;
	private boolean handleException;
	private boolean handleExecutionOutcome;
	private boolean handleExecutionOutcomeCalled;

	public void testDefaultHandleFlowException() throws Exception {
		PortletSession session = renderRequest.getPortletSession();
		final FlowException flowException = new FlowException("Error") {
			};
		session.setAttribute("actionRequestFlowException", flowException);

		try {
			controller.handleRender(renderRequest, renderResponse, flowHandler);
			fail("Should have thrown exception");
		}
		catch (FlowException e) {
			assertEquals(flowException, e);
		}
	}

	public void testDefaultHandleNoSuchFlowExecutionException() throws Exception {
		actionRequest.setContextPath("/springtravel");
		actionRequest.addParameter("execution", "12345");
		flowExecutor.resumeExecution("12345", actionContext);

		FlowException flowException = new NoSuchFlowExecutionException(new MockFlowExecutionKey("12345"), null);
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(new Object[] { flowExecutor });
		controller.handleAction(actionRequest, actionResponse, flowHandler);
		assertNotNull(actionRequest.getPortletSession().getAttribute("actionRequestFlowException"));
		EasyMock.verify(new Object[] { flowExecutor });

		Exception e = (Exception) actionRequest.getPortletSession().getAttribute("actionRequestFlowException");
		assertTrue(e instanceof NoSuchFlowExecutionException);
	}

	public void testHandleFlowExceptionCustomFlowHandler() throws Exception {
		handleException = true;

		final FlowException flowException = new FlowException("Error") {
			};
		renderRequest.setContextPath("/springtravel");
		flowExecutor.launchExecution("foo", flowInput, renderContext);
		EasyMock.expectLastCall().andThrow(flowException);
		EasyMock.replay(new Object[] { flowExecutor });

		ModelAndView mv = controller.handleRender(renderRequest, renderResponse, flowHandler);
		assertNotNull(mv);
		assertEquals("error", mv.getViewName());
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testHandleFlowExceptionFromSession() throws Exception {
		handleException = true;

		PortletSession session = renderRequest.getPortletSession();
		final FlowException flowException = new FlowException("Error") {
			};
		session.setAttribute("actionRequestFlowException", flowException);

		ModelAndView mv = controller.handleRender(renderRequest, renderResponse, flowHandler);
		assertEquals("error", mv.getViewName());
	}

	public void testHandleFlowOutcomeCustomFlowHandler() throws Exception {
		handleExecutionOutcome = true;
		actionRequest.setContextPath("/springtravel");
		actionRequest.addParameter("execution", "12345");

		LocalAttributeMap<Object> output = new LocalAttributeMap<Object>();
		output.put("bar", "baz");

		FlowExecutionOutcome outcome = new FlowExecutionOutcome("finish", output);
		flowExecutor.resumeExecution("12345", actionContext);
		EasyMock.expectLastCall().andReturn(FlowExecutionResult.createEndedResult("bar", outcome));
		EasyMock.replay(new Object[] { flowExecutor });
		controller.handleAction(actionRequest, actionResponse, flowHandler);
		assertTrue(handleExecutionOutcomeCalled);
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testLaunchFlowRequest() throws Exception {
		renderRequest.setContextPath("/springtravel");
		flowExecutor.launchExecution("foo", flowInput, renderContext);

		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "12345");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });

		ModelAndView mv = controller.handleRender(renderRequest, renderResponse, flowHandler);
		assertNull(mv);
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testPopulateConveniencePortletProperties() {
		controller.populateConveniencePortletProperties(renderRequest);
		assertEquals(renderRequest.getPortletMode().toString(), renderRequest.getAttribute("portletMode"));
		assertEquals(renderRequest.getWindowState().toString(), renderRequest.getAttribute("portletWindowState"));
	}

	public void testResumeFlowActionRequest() throws Exception {
		actionRequest.setContextPath("/springtravel");
		actionRequest.addParameter("execution", "12345");
		flowExecutor.resumeExecution("12345", actionContext);

		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "123456");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		controller.handleAction(actionRequest, actionResponse, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testResumeFlowRenderRequest() throws Exception {
		renderRequest.setContextPath("/springtravel");
		renderRequest.addParameter("execution", "12345");
		flowExecutor.resumeExecution("12345", renderContext);

		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "123456");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		controller.handleRender(renderRequest, renderResponse, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
	}

	public void testResumeFlowRenderRequestFromSession() throws Exception {
		renderRequest.setContextPath("/springtravel");

		PortletSession session = renderRequest.getPortletSession();
		session.setAttribute("execution", "12345");
		flowExecutor.resumeExecution("12345", renderContext);

		FlowExecutionResult result = FlowExecutionResult.createPausedResult("foo", "123456");
		EasyMock.expectLastCall().andReturn(result);
		EasyMock.replay(new Object[] { flowExecutor });
		controller.handleRender(renderRequest, renderResponse, flowHandler);
		EasyMock.verify(new Object[] { flowExecutor });
	}

	protected void setUp() throws Exception {
		flowExecutor = EasyMock.createMock(FlowExecutor.class);
		controller = new FlowHandlerAdapter() {
				protected PortletExternalContext createPortletExternalContext(PortletRequest request,
					PortletResponse response) {

					if (request instanceof ActionRequest) {
						return actionContext;
					}
					else {
						return renderContext;
					}
				}
			};
		controller.setFlowExecutor(flowExecutor);
		controller.setApplicationContext(new StaticWebApplicationContext());
		portletContext = new MockPortletContext();
		controller.setPortletContext(portletContext);
		controller.afterPropertiesSet();

		actionRequest = new MockActionRequest();
		actionResponse = new MockActionResponse();
		renderRequest = new MockRenderRequest();
		renderResponse = new MockRenderResponse();
		actionContext = new PortletExternalContext(portletContext, actionRequest, actionResponse,
				controller.getFlowUrlHandler());
		renderContext = new PortletExternalContext(portletContext, renderRequest, renderResponse,
				controller.getFlowUrlHandler());

		flowHandler = new FlowHandler() {
				public String getFlowId() {
					return "foo";
				}

				public MutableAttributeMap<Object> createExecutionInputMap(RenderRequest request) {
					return null;
				}

				public MutableAttributeMap<Object> createResourceExecutionInputMap(ResourceRequest request) {
					return null;
				}

				public boolean handleExecutionOutcome(FlowExecutionOutcome outcome, ActionRequest request,
					ActionResponse response) {
					handleExecutionOutcomeCalled = true;

					if (handleExecutionOutcome) {
						return true;
					}
					else {
						return false;
					}
				}

				public String handleException(FlowException e, RenderRequest request, RenderResponse response) {

					if (handleException) {
						return "error";
					}
					else {
						return null;
					}
				}

				public String handleResourceException(FlowException e, ResourceRequest request,
					ResourceResponse response) {

					if (handleException) {
						return "error";
					}
					else {
						return null;
					}
				}

			};
	}

}
