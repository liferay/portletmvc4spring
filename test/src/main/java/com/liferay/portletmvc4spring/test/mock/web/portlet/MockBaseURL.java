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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jakarta.portlet.BaseURL;
import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletSecurityException;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * Mock implementation of the {@link jakarta.portlet.BaseURL} interface.
 *
 * @author  Juergen Hoeller
 * @since   3.0
 */
public abstract class MockBaseURL implements BaseURL {

	public static final String URL_TYPE_RENDER = "render";

	public static final String URL_TYPE_ACTION = "action";

	private static final String ENCODING = "UTF-8";

	protected final Map<String, String[]> parameters = new LinkedHashMap<String, String[]>();

	private MutableRenderParameters mutableRenderParameters;

	private boolean secure = false;

	private final Map<String, String[]> properties = new LinkedHashMap<String, String[]>();

	@Override
	public void addProperty(String key, String value) {
		String[] values = this.properties.get(key);

		if (values != null) {
			this.properties.put(key, StringUtils.addStringToArray(values, value));
		}
		else {
			this.properties.put(key, new String[] { value });
		}
	}

	@Override
	public Appendable append(Appendable appendable) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Appendable append(Appendable appendable, boolean b) throws IOException {
		throw new UnsupportedOperationException();
	}

	public String getParameter(String name) {
		String[] arr = this.parameters.get(name);

		return (((arr != null) && (arr.length > 0)) ? arr[0] : null);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return Collections.unmodifiableMap(this.parameters);
	}

	public Set<String> getParameterNames() {
		return this.parameters.keySet();
	}

	public String[] getParameterValues(String name) {
		return this.parameters.get(name);
	}

	public Map<String, String[]> getProperties() {
		return Collections.unmodifiableMap(this.properties);
	}

	@Override
	public MutableRenderParameters getRenderParameters() {

		if (mutableRenderParameters == null) {
			mutableRenderParameters = new MockMutableRenderParameters();
		}

		return mutableRenderParameters;
	}

	public boolean isSecure() {
		return this.secure;
	}

	// ---------------------------------------------------------------------
	// BaseURL methods
	// ---------------------------------------------------------------------

	@Override
	public void setParameter(String key, String value) {
		Assert.notNull(key, "Parameter key must be null");
		Assert.notNull(value, "Parameter value must not be null");
		this.parameters.put(key, new String[] { value });
	}

	@Override
	public void setParameter(String key, String[] values) {
		Assert.notNull(key, "Parameter key must be null");
		Assert.notNull(values, "Parameter values must not be null");
		this.parameters.put(key, values);
	}

	@Override
	public void setParameters(Map<String, String[]> parameters) {
		Assert.notNull(parameters, "Parameters Map must not be null");
		this.parameters.clear();
		this.parameters.putAll(parameters);
	}

	@Override
	public void setProperty(String key, String value) {
		this.properties.put(key, new String[] { value });
	}

	@Override
	public void setSecure(boolean secure) throws PortletSecurityException {
		this.secure = secure;
	}

	@Override
	public void write(Writer out) throws IOException {
		out.write(toString());
	}

	@Override
	public void write(Writer out, boolean escapeXML) throws IOException {
		out.write(toString());
	}

	protected String encodeParameter(String name, String value) {

		try {
			return URLEncoder.encode(name, ENCODING) + "=" + URLEncoder.encode(value, ENCODING);
		}
		catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	protected String encodeParameter(String name, String[] values) {

		try {
			StringBuilder sb = new StringBuilder();

			for (int i = 0, n = values.length; i < n; i++) {
				sb.append((i > 0) ? ";" : "").append(URLEncoder.encode(name, ENCODING)).append("=").append(URLEncoder
					.encode(values[i], ENCODING));
			}

			return sb.toString();
		}
		catch (UnsupportedEncodingException ex) {
			return null;
		}
	}
}
