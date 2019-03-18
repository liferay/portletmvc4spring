/**
 * Copyright (c) 2000-2019 the original author or authors.
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
package com.liferay.portletmvc4spring.mvc;

import javax.portlet.PortletException;
import javax.portlet.PortletMode;

import static org.junit.Assert.*;

import org.junit.Test;

import com.liferay.portletmvc4spring.ModelAndView;

import com.liferay.spring.mock.web.portlet.MockActionRequest;
import com.liferay.spring.mock.web.portlet.MockActionResponse;
import com.liferay.spring.mock.web.portlet.MockRenderRequest;
import com.liferay.spring.mock.web.portlet.MockRenderResponse;


/**
 * @author  Mark Fisher
 * @author  Sam Brannen
 */
public class PortletModeNameViewControllerTests {

	private final PortletModeNameViewController controller = new PortletModeNameViewController();

	private final MockRenderRequest request = new MockRenderRequest();

	private final MockRenderResponse response = new MockRenderResponse();

	@Test(expected = PortletException.class)
	public void actionRequest() throws Exception {
		controller.handleActionRequest(new MockActionRequest(), new MockActionResponse());
	}

	@Test
	public void editPortletMode() throws Exception {
		request.setPortletMode(PortletMode.EDIT);

		ModelAndView mav = controller.handleRenderRequest(request, response);
		assertEquals("edit", mav.getViewName());
	}

	@Test
	public void helpPortletMode() throws Exception {
		request.setPortletMode(PortletMode.HELP);

		ModelAndView mav = controller.handleRenderRequest(request, response);
		assertEquals("help", mav.getViewName());
	}

	@Test
	public void viewPortletMode() throws Exception {
		request.setPortletMode(PortletMode.VIEW);

		ModelAndView mav = controller.handleRenderRequest(request, response);
		assertEquals("view", mav.getViewName());
	}

}
