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

import jakarta.portlet.PortletMode;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.liferay.portletmvc4spring.context.ConfigurablePortletApplicationContext;
import com.liferay.portletmvc4spring.context.XmlPortletApplicationContext;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortletContext;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortletRequest;


/**
 * @author  Mark Fisher
 * @author  Sam Brannen
 */
public class PortletModeParameterHandlerMappingTests {

	public static final String CONF = "/com/liferay/portletmvc4spring/handler/portletModeParameterMapping.xml";

	private final ConfigurablePortletApplicationContext pac = new XmlPortletApplicationContext();

	private final MockPortletContext portletContext = new MockPortletContext();

	private final MockPortletRequest request = new MockPortletRequest();

	private PortletModeParameterHandlerMapping hm;

	@Test
	public void allowDuplicateMappingInDifferentPortletMode() throws Exception {
		hm.setAllowDuplicateParameters(true);

		Object editRemoveHandler = new Object();
		hm.registerHandler(PortletMode.EDIT, "remove", editRemoveHandler);

		request.setPortletMode(PortletMode.EDIT);
		request.setParameter("action", "remove");

		Object handler = hm.getHandler(request).getHandler();
		assertEquals(editRemoveHandler, handler);
	}

	@Test(expected = IllegalStateException.class)
	public void duplicateMappingInDifferentPortletMode() {
		hm.registerHandler(PortletMode.EDIT, "remove", new Object());
	}

	@Test(expected = IllegalStateException.class)
	public void duplicateMappingInSamePortletMode() {
		hm.registerHandler(PortletMode.VIEW, "remove", new Object());
	}

	@Test
	public void portletModeEditWithParameter() throws Exception {
		request.setPortletMode(PortletMode.EDIT);
		request.setParameter("action", "prefs");

		Object handler = hm.getHandler(request).getHandler();
		assertEquals(pac.getBean("preferencesHandler"), handler);
	}

	@Test
	public void portletModeViewWithParameter() throws Exception {
		MockPortletRequest addRequest = request;
		addRequest.setPortletMode(PortletMode.VIEW);
		addRequest.setParameter("action", "add");

		Object addHandler = hm.getHandler(addRequest).getHandler();
		assertEquals(pac.getBean("addItemHandler"), addHandler);

		MockPortletRequest removeRequest = new MockPortletRequest();
		removeRequest.setPortletMode(PortletMode.VIEW);
		removeRequest.setParameter("action", "remove");

		Object removeHandler = hm.getHandler(removeRequest).getHandler();
		assertEquals(pac.getBean("removeItemHandler"), removeHandler);
	}

	@Before
	public void setUp() throws Exception {
		pac.setPortletContext(portletContext);
		pac.setConfigLocations(new String[] { CONF });
		pac.refresh();

		hm = pac.getBean(PortletModeParameterHandlerMapping.class);
	}

}
