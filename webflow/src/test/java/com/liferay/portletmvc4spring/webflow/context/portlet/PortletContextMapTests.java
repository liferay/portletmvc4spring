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

import java.util.HashMap;
import java.util.Map;

import com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortletContext;

import junit.framework.TestCase;


/**
 * Test case for the {@link PortletContextMap} class.
 *
 * @author  Ulrik Sandberg
 * @author  Erwin Vervaet
 * @author  Scott Andrews
 */
public class PortletContextMapTests extends TestCase {

	private PortletContextMap tested;

	private MockPortletContext context;

	public void testClear() {
		tested.clear();
		assertTrue(tested.isEmpty());
	}

	public void testContainsKey() {
		assertEquals("containsKey,", true, tested.containsKey("SomeKey"));
	}

	public void testContainsValue() {
		assertTrue(tested.containsValue("SomeValue"));
	}

	public void testEntrySet() {
		assertEquals(1, tested.entrySet().size());
		assertEquals("SomeKey", tested.entrySet().iterator().next().getKey());
		assertEquals("SomeValue", tested.entrySet().iterator().next().getValue());
	}

	public void testGet() {
		assertEquals("get,", "SomeValue", tested.get("SomeKey"));
	}

	public void testIsEmpty() {
		tested.remove("SomeKey");
		assertEquals("size,", 0, tested.size());
		assertEquals("isEmpty,", true, tested.isEmpty());
	}

	public void testKeySet() {
		assertEquals(1, tested.keySet().size());
		assertTrue(tested.keySet().contains("SomeKey"));
	}

	public void testPut() {
		Object old = tested.put("SomeKey", "SomeNewValue");

		assertEquals("old value,", "SomeValue", old);
		assertEquals("new value,", "SomeNewValue", tested.get("SomeKey"));
	}

	public void testPutAll() {
		Map<String, Object> otherMap = new HashMap<String, Object>();
		otherMap.put("SomeOtherKey", "SomeOtherValue");
		otherMap.put("SomeKey", "SomeUpdatedValue");
		tested.putAll(otherMap);
		assertEquals("SomeOtherValue", tested.get("SomeOtherKey"));
		assertEquals("SomeUpdatedValue", tested.get("SomeKey"));
	}

	public void testRemove() {
		Object old = tested.remove("SomeKey");

		assertEquals("old value,", "SomeValue", old);
		assertNull("should be gone", tested.get("SomeKey"));
	}

	public void testSizeAddOne() {
		assertEquals("size,", 1, tested.size());
	}

	public void testSizeAddTwo() {
		tested.put("SomeOtherKey", "SomeOtherValue");
		assertEquals("size,", 2, tested.size());
	}

	public void testValues() {
		assertEquals(1, tested.values().size());
		assertTrue(tested.values().contains("SomeValue"));
	}

	protected void setUp() throws Exception {
		super.setUp();
		context = new MockPortletContext();

		// a fresh MockPortletContext seems to already contain an element;
		// that's confusing, so we remove it
		context.removeAttribute("jakarta.servlet.context.tempdir");
		tested = new PortletContextMap(context);
		tested.put("SomeKey", "SomeValue");
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		context = null;
		tested = null;
	}
}
