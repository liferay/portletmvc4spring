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

import java.util.Iterator;

import com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortletRequest;

import junit.framework.TestCase;


/**
 * Unit test for the {@link PortletRequestMap} class.
 *
 * @author  Ulrik Sandberg
 * @author  Scott Andrews
 */
public class PortletRequestMapTests extends TestCase {

	private PortletRequestMap tested;

	private MockPortletRequest request;

	public void testGetAttribute() {
		request.setAttribute("Some key", "Some value");

		// perform test
		Object result = tested.getAttribute("Some key");
		assertEquals("Some value", result);
	}

	public void testGetAttributeNames() {
		request.setAttribute("Some key", "Some value");

		// perform test
		Iterator<String> names = tested.getAttributeNames();
		assertNotNull("Null result unexpected", names);
		assertTrue("More elements", names.hasNext());

		while (names.hasNext()) {
			String name = names.next();

			if ("Some key".equals(name)) {
				return;
			}
		}

		fail("Expected to find: 'Some key'");
	}

	public void testRemoveAttribute() {
		request.setAttribute("Some key", "Some value");

		// perform test
		tested.removeAttribute("Some key");
		assertNull(request.getAttribute("Some key"));
	}

	public void testSetAttribute() {

		// perform test
		tested.setAttribute("Some key", "Some value");
		assertEquals("Some value", request.getAttribute("Some key"));
	}

	protected void setUp() throws Exception {
		super.setUp();
		request = new MockPortletRequest();
		tested = new PortletRequestMap(request);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		request = null;
		tested = null;
	}
}
