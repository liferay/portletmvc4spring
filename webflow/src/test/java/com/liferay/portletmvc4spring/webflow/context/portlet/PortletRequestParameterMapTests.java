/*
 * Copyright 2004-2012 the original author or authors.
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

import com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortletRequest;

import java.util.Iterator;

import junit.framework.TestCase;

/**
 * Unit test for the {@link PortletRequestParameterMap} class.
 * 
 * @author Ulrik Sandberg
 * @author Scott Andrews
 */
public class PortletRequestParameterMapTests extends TestCase {

	private PortletRequestParameterMap tested;

	private MockPortletRequest request;

	protected void setUp() throws Exception {
		super.setUp();
		request = new MockPortletRequest();
		tested = new PortletRequestParameterMap(request);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		request = null;
		tested = null;
	}

	public void testGetAttribute() {
		request.setParameter("Some param", "Some value");
		// perform test
		Object result = tested.getAttribute("Some param");
		assertEquals("Some value", result);
	}

	public void testSetAttribute() {
		// perform test
		try {
			tested.setAttribute("Some key", "Some value");
			fail("UnsupportedOperationException expected");
		} catch (UnsupportedOperationException expected) {
			// expected
		}
	}

	public void testRemoveAttribute() {
		request.setParameter("Some param", "Some value");
		// perform test
		try {
			tested.removeAttribute("Some param");
			fail("UnsupportedOperationException expected");
		} catch (UnsupportedOperationException expected) {
			// expected
		}
	}

	public void testGetAttributeNames() {
		request.setParameter("Some param", "Some value");
		// perform test
		Iterator<String> names = tested.getAttributeNames();
		assertNotNull("Null result unexpected", names);
		assertTrue("More elements", names.hasNext());
		String name = names.next();
		assertEquals("Some param", name);
	}
}
