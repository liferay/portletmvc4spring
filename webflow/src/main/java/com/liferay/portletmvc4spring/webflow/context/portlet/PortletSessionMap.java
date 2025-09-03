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

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;

import org.springframework.binding.collection.SharedMap;
import org.springframework.binding.collection.StringKeyedMapAdapter;

import org.springframework.web.util.WebUtils;

import org.springframework.webflow.context.web.HttpSessionMapBindingListener;
import org.springframework.webflow.core.collection.AttributeMapBindingListener;
import org.springframework.webflow.core.collection.CollectionUtils;


/**
 * A Shared Map backed by the Portlet session, for accessing session scoped attributes.
 *
 * @author  Keith Donald
 * @author  Scott Andrews
 */
public class PortletSessionMap extends StringKeyedMapAdapter<Object> implements SharedMap<String, Object> {

	/** The wrapped portlet request, providing access to the session. */
	private PortletRequest request;

	/**
	 * Create a map wrapping the session of given request.
	 */
	public PortletSessionMap(PortletRequest request) {
		this.request = request;
	}

	public Object getMutex() {

		// force session creation
		PortletSession session = getSession();
		Object mutex = session.getAttribute(WebUtils.SESSION_MUTEX_ATTRIBUTE);

		return (mutex != null) ? mutex : session;
	}

	protected Object getAttribute(String key) {
		PortletSession session = getSession();

		Object value = session.getAttribute(key);

		if (value instanceof HttpSessionMapBindingListener) {

			// unwrap
			return ((HttpSessionMapBindingListener) value).getListener();
		}
		else {
			return value;
		}
	}

	protected Iterator<String> getAttributeNames() {
		PortletSession session = getSession();

		return CollectionUtils.toIterator(session.getAttributeNames());
	}

	protected void removeAttribute(String key) {
		PortletSession session = getSession();

		session.removeAttribute(key);
	}

	protected void setAttribute(String key, Object value) {

		// force session creation
		PortletSession session = getSession();

		if (value instanceof AttributeMapBindingListener) {

			// wrap
			session.setAttribute(key, new HttpSessionMapBindingListener((AttributeMapBindingListener) value, this));
		}
		else {
			session.setAttribute(key, value);
		}
	}

	/**
	 * Internal helper to get the portlet session associated with the wrapped request
	 *
	 * <p>Note that this method will not force session creation.
	 */
	private PortletSession getSession() {
		return request.getPortletSession(true);
	}
}
