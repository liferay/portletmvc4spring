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
package com.liferay.portletmvc4spring.test.mock.web.portlet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletResponse;
import jakarta.servlet.http.Cookie;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.util.Assert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Mock implementation of the {@link jakarta.portlet.PortletResponse} interface.
 *
 * @author  John A. Lewis
 * @author  Juergen Hoeller
 * @since   2.0
 */
public class MockPortletResponse implements PortletResponse {

	private final PortalContext portalContext;

	private final Map<String, String[]> properties = new LinkedHashMap<String, String[]>();

	private String namespace = "";

	private final Set<Cookie> cookies = new LinkedHashSet<Cookie>();

	private final Map<String, Element[]> xmlProperties = new LinkedHashMap<String, Element[]>();

	private Document xmlDocument;

	/**
	 * Create a new MockPortletResponse with a default {@link MockPortalContext}.
	 *
	 * @see  MockPortalContext
	 */
	public MockPortletResponse() {
		this(null);
	}

	/**
	 * Create a new MockPortletResponse.
	 *
	 * @param  portalContext  the PortalContext defining the supported PortletModes and WindowStates
	 */
	public MockPortletResponse(PortalContext portalContext) {
		this.portalContext = ((portalContext != null) ? portalContext : new MockPortalContext());
	}

	@Override
	public void addProperty(Cookie cookie) {
		Assert.notNull(cookie, "Cookie must not be null");
		this.cookies.add(cookie);
	}

	// ---------------------------------------------------------------------
	// PortletResponse methods
	// ---------------------------------------------------------------------

	@Override
	public void addProperty(String key, String value) {
		Assert.notNull(key, "Property key must not be null");

		String[] oldArr = this.properties.get(key);

		if (oldArr != null) {
			String[] newArr = new String[oldArr.length + 1];
			System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
			newArr[oldArr.length] = value;
			this.properties.put(key, newArr);
		}
		else {
			this.properties.put(key, new String[] { value });
		}
	}

	@Override
	public void addProperty(String key, Element value) {
		Assert.notNull(key, "Property key must not be null");

		Element[] oldArr = this.xmlProperties.get(key);

		if (oldArr != null) {
			Element[] newArr = new Element[oldArr.length + 1];
			System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
			newArr[oldArr.length] = value;
			this.xmlProperties.put(key, newArr);
		}
		else {
			this.xmlProperties.put(key, new Element[] { value });
		}
	}

	@Override
	public Element createElement(String tagName) throws DOMException {

		if (this.xmlDocument == null) {

			try {
				this.xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			}
			catch (ParserConfigurationException ex) {
				throw new DOMException(DOMException.INVALID_STATE_ERR, ex.toString());
			}
		}

		return this.xmlDocument.createElement(tagName);
	}

	@Override
	public String encodeURL(String path) {
		return path;
	}

	public Cookie getCookie(String name) {
		Assert.notNull(name, "Cookie name must not be null");

		for (Cookie cookie : this.cookies) {

			if (name.equals(cookie.getName())) {
				return cookie;
			}
		}

		return null;
	}

	public Cookie[] getCookies() {
		return this.cookies.toArray(new Cookie[this.cookies.size()]);
	}

	@Override
	public String getNamespace() {
		return this.namespace;
	}

	/**
	 * Return the PortalContext that this MockPortletResponse runs in, defining the supported PortletModes and
	 * WindowStates.
	 */
	public PortalContext getPortalContext() {
		return this.portalContext;
	}

	public String[] getProperties(String key) {
		Assert.notNull(key, "Property key must not be null");

		return this.properties.get(key);
	}

	public String getProperty(String key) {
		Assert.notNull(key, "Property key must not be null");

		String[] arr = this.properties.get(key);

		return (((arr != null) && (arr.length > 0)) ? arr[0] : null);
	}

	public Set<String> getPropertyNames() {
		return Collections.unmodifiableSet(this.properties.keySet());
	}

	@Override
	public Collection<String> getPropertyValues(String key) {
		Assert.notNull(key, "Property key must not be null");

		return Arrays.asList(this.properties.get(key));
	}

	public Element[] getXmlProperties(String key) {
		Assert.notNull(key, "Property key must not be null");

		return this.xmlProperties.get(key);
	}

	public Element getXmlProperty(String key) {
		Assert.notNull(key, "Property key must not be null");

		Element[] arr = this.xmlProperties.get(key);

		return (((arr != null) && (arr.length > 0)) ? arr[0] : null);
	}

	public Set<String> getXmlPropertyNames() {
		return Collections.unmodifiableSet(this.xmlProperties.keySet());
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	@Override
	public void setProperty(String key, String value) {
		Assert.notNull(key, "Property key must not be null");
		this.properties.put(key, new String[] { value });
	}

}
