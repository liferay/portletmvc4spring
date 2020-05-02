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
public class ParameterHandlerMappingTests {

	public static final String CONF = "/com/liferay/portletmvc4spring/handler/parameterMapping.xml";

	private final ConfigurablePortletApplicationContext pac = new XmlPortletApplicationContext();

	private final MockPortletContext portletContext = new MockPortletContext();

	private final MockPortletRequest request = new MockPortletRequest();

	private ParameterHandlerMapping hm;

	@Test
	public void configuredParameterName() throws Exception {
		hm.setParameterName("someParam");
		request.addParameter("someParam", "add");

		Object handler = hm.getHandler(request).getHandler();
		assertEquals(pac.getBean("addItemHandler"), handler);
	}

	@Test(expected = IllegalStateException.class)
	public void duplicateMappingAttempt() {
		hm.registerHandler("add", new Object());
	}

	@Test
	public void parameterMapping() throws Exception {
		MockPortletRequest addRequest = request;
		addRequest.addParameter("action", "add");

		Object addHandler = hm.getHandler(addRequest).getHandler();
		assertEquals(pac.getBean("addItemHandler"), addHandler);

		MockPortletRequest removeRequest = new MockPortletRequest();
		removeRequest.addParameter("action", "remove");

		Object removeHandler = hm.getHandler(removeRequest).getHandler();
		assertEquals(pac.getBean("removeItemHandler"), removeHandler);
	}

	@Before
	public void setUp() throws Exception {
		pac.setPortletContext(portletContext);
		pac.setConfigLocations(new String[] { CONF });
		pac.refresh();

		hm = pac.getBean(ParameterHandlerMapping.class);
	}

	@Test
	public void unregisteredHandlerWithDefault() throws Exception {
		Object defaultHandler = new Object();
		hm.setDefaultHandler(defaultHandler);
		request.addParameter("action", "modify");

		assertNotNull(hm.getHandler(request));
		assertEquals(defaultHandler, hm.getHandler(request).getHandler());
	}

	@Test
	public void unregisteredHandlerWithNoDefault() throws Exception {
		request.addParameter("action", "modify");

		assertNull(hm.getHandler(request));
	}

}
