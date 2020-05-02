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
package com.liferay.portletmvc4spring.handler;

import javax.portlet.PortletMode;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.liferay.portletmvc4spring.context.ConfigurablePortletApplicationContext;
import com.liferay.portletmvc4spring.context.XmlPortletApplicationContext;

import com.liferay.spring.mock.web.portlet.MockPortletContext;
import com.liferay.spring.mock.web.portlet.MockPortletRequest;


/**
 * @author  Mark Fisher
 * @author  Sam Brannen
 */
public class PortletModeHandlerMappingTests {

	public static final String CONF = "/com/liferay/portletmvc4spring/handler/portletModeMapping.xml";

	private final ConfigurablePortletApplicationContext pac = new XmlPortletApplicationContext();

	private final MockPortletContext portletContext = new MockPortletContext();

	private final MockPortletRequest request = new MockPortletRequest();

	private PortletModeHandlerMapping hm;

	@Test(expected = IllegalStateException.class)
	public void duplicateMappingAttempt() {
		hm.registerHandler(PortletMode.VIEW, new Object());
	}

	@Test
	public void portletModeEdit() throws Exception {
		request.setPortletMode(PortletMode.EDIT);

		Object handler = hm.getHandler(request).getHandler();
		assertEquals(pac.getBean("editHandler"), handler);
	}

	@Test
	public void portletModeHelp() throws Exception {
		request.setPortletMode(PortletMode.HELP);

		Object handler = hm.getHandler(request).getHandler();
		assertEquals(pac.getBean("helpHandler"), handler);
	}

	@Test
	public void portletModeView() throws Exception {
		request.setPortletMode(PortletMode.VIEW);

		Object handler = hm.getHandler(request).getHandler();
		assertEquals(pac.getBean("viewHandler"), handler);
	}

	@Before
	public void setUp() throws Exception {
		pac.setPortletContext(portletContext);
		pac.setConfigLocations(new String[] { CONF });
		pac.refresh();

		hm = pac.getBean(PortletModeHandlerMapping.class);
	}

}
