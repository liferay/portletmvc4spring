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

import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortalContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderParameters;
import javax.portlet.WindowState;
import jakarta.servlet.http.Cookie;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;


/**
 * Mock implementation of the {@link javax.portlet.PortletRequest} interface.
 *
 * @author  John A. Lewis
 * @author  Juergen Hoeller
 * @since   2.0
 */
public class MockPortletRequest implements PortletRequest {

	private boolean active = true;

	private final PortalContext portalContext;

	private final PortletContext portletContext;

	private PortletSession session;

	private WindowState windowState = WindowState.NORMAL;

	private PortletMode portletMode = PortletMode.VIEW;

	private PortletPreferences portletPreferences = new MockPortletPreferences();

	private final Map<String, List<String>> properties = new LinkedHashMap<String, List<String>>();

	private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();

	private final Map<String, String[]> parameters = new LinkedHashMap<String, String[]>();

	private String authType = null;

	private String contextPath = "";

	private String remoteUser = null;

	private Principal userPrincipal = null;

	private final Set<String> userRoles = new HashSet<String>();

	private boolean secure = false;

	private RenderParameters renderParameters;

	private boolean requestedSessionIdValid = true;

	private final List<String> responseContentTypes = new LinkedList<String>();

	private final List<Locale> locales = new LinkedList<Locale>();

	private String scheme = "http";

	private String serverName = "localhost";

	private int serverPort = 80;

	private String windowID;

	private Cookie[] cookies;

	private final Set<String> publicParameterNames = new HashSet<String>();

	/**
	 * Create a new MockPortletRequest with a default {@link MockPortalContext} and a default {@link
	 * MockPortletContext}.
	 *
	 * @see  MockPortalContext
	 * @see  MockPortletContext
	 */
	public MockPortletRequest() {
		this(null, null);
	}

	/**
	 * Create a new MockPortletRequest with a default {@link MockPortalContext}.
	 *
	 * @param  portletContext  the PortletContext that the request runs in
	 *
	 * @see    MockPortalContext
	 */
	public MockPortletRequest(PortletContext portletContext) {
		this(null, portletContext);
	}

	/**
	 * Create a new MockPortletRequest.
	 *
	 * @param  portalContext   the PortalContext that the request runs in
	 * @param  portletContext  the PortletContext that the request runs in
	 */
	public MockPortletRequest(PortalContext portalContext, PortletContext portletContext) {
		this.portalContext = ((portalContext != null) ? portalContext : new MockPortalContext());
		this.portletContext = ((portletContext != null) ? portletContext : new MockPortletContext());
		this.responseContentTypes.add("text/html");
		this.locales.add(Locale.ENGLISH);
		this.attributes.put(LIFECYCLE_PHASE, getLifecyclePhase());
	}

	public void addLocale(Locale locale) {
		this.locales.add(locale);
	}

	public void addParameter(String name, String value) {
		addParameter(name, new String[] { value });
	}

	public void addParameter(String name, String[] values) {
		String[] oldArr = this.parameters.get(name);

		if (oldArr != null) {
			String[] newArr = new String[oldArr.length + values.length];
			System.arraycopy(oldArr, 0, newArr, 0, oldArr.length);
			System.arraycopy(values, 0, newArr, oldArr.length, values.length);
			this.parameters.put(name, newArr);
		}
		else {
			this.parameters.put(name, values);
		}
	}

	public void addPreferredLocale(Locale locale) {
		this.locales.add(0, locale);
	}

	public void addPreferredResponseContentType(String responseContentType) {
		this.responseContentTypes.add(0, responseContentType);
	}

	/**
	 * Add a single value for the specified property.
	 *
	 * <p>If there are already one or more values registered for the given property key, the given value will be added
	 * to the end of the list.
	 */
	public void addProperty(String key, String value) {
		Assert.notNull(key, "Property key must not be null");

		List<String> oldList = this.properties.get(key);

		if (oldList != null) {
			oldList.add(value);
		}
		else {
			List<String> list = new LinkedList<String>();
			list.add(value);
			this.properties.put(key, list);
		}
	}

	public void addResponseContentType(String responseContentType) {
		this.responseContentTypes.add(responseContentType);
	}

	public void addUserRole(String role) {
		this.userRoles.add(role);
	}

	/**
	 * Mark this request as completed.
	 */
	public void close() {
		this.active = false;
	}

	@Override
	public Object getAttribute(String name) {
		checkActive();

		return this.attributes.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		checkActive();

		return Collections.enumeration(this.attributes.keySet());
	}

	@Override
	public String getAuthType() {
		return this.authType;
	}

	@Override
	public String getContextPath() {
		return this.contextPath;
	}

	@Override
	public Cookie[] getCookies() {
		return this.cookies;
	}

	@Override
	public Locale getLocale() {
		return this.locales.get(0);
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return Collections.enumeration(this.locales);
	}

	@Override
	public String getParameter(String name) {
		String[] arr = this.parameters.get(name);

		return (((arr != null) && (arr.length > 0)) ? arr[0] : null);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return Collections.unmodifiableMap(this.parameters);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(this.parameters.keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		return this.parameters.get(name);
	}

	@Override
	public PortalContext getPortalContext() {
		return this.portalContext;
	}

	@Override
	public PortletContext getPortletContext() {
		return this.portletContext;
	}

	@Override
	public PortletMode getPortletMode() {
		return this.portletMode;
	}

	@Override
	public PortletSession getPortletSession() {
		return getPortletSession(true);
	}

	@Override
	public PortletSession getPortletSession(boolean create) {
		checkActive();

		// Reset session if invalidated.
		if ((this.session instanceof MockPortletSession) && ((MockPortletSession) this.session).isInvalid()) {
			this.session = null;
		}

		// Create new session if necessary.
		if ((this.session == null) && create) {
			this.session = new MockPortletSession(this.portletContext);
		}

		return this.session;
	}

	@Override
	public PortletPreferences getPreferences() {
		return this.portletPreferences;
	}

	@Override
	public Map<String, String[]> getPrivateParameterMap() {

		if (!this.publicParameterNames.isEmpty()) {
			Map<String, String[]> filtered = new LinkedHashMap<String, String[]>();

			for (String key : this.parameters.keySet()) {

				if (!this.publicParameterNames.contains(key)) {
					filtered.put(key, this.parameters.get(key));
				}
			}

			return filtered;
		}
		else {
			return Collections.unmodifiableMap(this.parameters);
		}
	}

	@Override
	public Enumeration<String> getProperties(String key) {
		Assert.notNull(key, "property key must not be null");

		return Collections.enumeration(this.properties.get(key));
	}

	@Override
	public String getProperty(String key) {
		Assert.notNull(key, "Property key must not be null");

		List<String> list = this.properties.get(key);

		return (((list != null) && (list.size() > 0)) ? list.get(0) : null);
	}

	@Override
	public Enumeration<String> getPropertyNames() {
		return Collections.enumeration(this.properties.keySet());
	}

	@Override
	public Map<String, String[]> getPublicParameterMap() {

		if (!this.publicParameterNames.isEmpty()) {
			Map<String, String[]> filtered = new LinkedHashMap<String, String[]>();

			for (String key : this.parameters.keySet()) {

				if (this.publicParameterNames.contains(key)) {
					filtered.put(key, this.parameters.get(key));
				}
			}

			return filtered;
		}
		else {
			return Collections.emptyMap();
		}
	}

	@Override
	public String getRemoteUser() {
		return this.remoteUser;
	}

	@Override
	public RenderParameters getRenderParameters() {

		if (renderParameters == null) {
			renderParameters = new MockRenderParameters();
		}

		return renderParameters;
	}

	@Override
	public String getRequestedSessionId() {
		PortletSession session = this.getPortletSession();

		return ((session != null) ? session.getId() : null);
	}

	@Override
	public String getResponseContentType() {
		return this.responseContentTypes.get(0);
	}

	@Override
	public Enumeration<String> getResponseContentTypes() {
		return Collections.enumeration(this.responseContentTypes);
	}

	@Override
	public String getScheme() {
		return this.scheme;
	}

	@Override
	public String getServerName() {
		return this.serverName;
	}

	@Override
	public int getServerPort() {
		return this.serverPort;
	}

	@Override
	public String getUserAgent() {
		return null;
	}

	@Override
	public Principal getUserPrincipal() {
		return this.userPrincipal;
	}

	@Override
	public String getWindowID() {
		return this.windowID;
	}

	@Override
	public WindowState getWindowState() {
		return this.windowState;
	}

	/**
	 * Return whether this request is still active (that is, not completed yet).
	 */
	public boolean isActive() {
		return this.active;
	}

	@Override
	public boolean isPortletModeAllowed(PortletMode portletMode) {
		return CollectionUtils.contains(this.portalContext.getSupportedPortletModes(), portletMode);
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return this.requestedSessionIdValid;
	}

	@Override
	public boolean isSecure() {
		return this.secure;
	}

	@Override
	public boolean isUserInRole(String role) {
		return this.userRoles.contains(role);
	}

	// ---------------------------------------------------------------------
	// PortletRequest methods
	// ---------------------------------------------------------------------

	@Override
	public boolean isWindowStateAllowed(WindowState windowState) {
		return CollectionUtils.contains(this.portalContext.getSupportedWindowStates(), windowState);
	}

	public void registerPublicParameter(String name) {
		this.publicParameterNames.add(name);
	}

	@Override
	public void removeAttribute(String name) {
		checkActive();
		this.attributes.remove(name);
	}

	@Override
	public void setAttribute(String name, Object value) {
		checkActive();

		if (value != null) {
			this.attributes.put(name, value);
		}
		else {
			this.attributes.remove(name);
		}
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public void setCookies(Cookie... cookies) {
		this.cookies = cookies;
	}

	public void setParameter(String key, String value) {
		Assert.notNull(key, "Parameter key must be null");
		Assert.notNull(value, "Parameter value must not be null");
		this.parameters.put(key, new String[] { value });
	}

	public void setParameter(String key, String[] values) {
		Assert.notNull(key, "Parameter key must be null");
		Assert.notNull(values, "Parameter values must not be null");
		this.parameters.put(key, values);
	}

	public void setParameters(Map<String, String[]> parameters) {
		Assert.notNull(parameters, "Parameters Map must not be null");
		this.parameters.clear();
		this.parameters.putAll(parameters);
	}

	public void setPortletMode(PortletMode portletMode) {
		Assert.notNull(portletMode, "PortletMode must not be null");
		this.portletMode = portletMode;
	}

	public void setPreferences(PortletPreferences preferences) {
		Assert.notNull(preferences, "PortletPreferences must not be null");
		this.portletPreferences = preferences;
	}

	/**
	 * Set a single value for the specified property.
	 *
	 * <p>If there are already one or more values registered for the given property key, they will be replaced.
	 */
	public void setProperty(String key, String value) {
		Assert.notNull(key, "Property key must not be null");

		List<String> list = new LinkedList<String>();
		list.add(value);
		this.properties.put(key, list);
	}

	public void setRemoteUser(String remoteUser) {
		this.remoteUser = remoteUser;
	}

	public void setRequestedSessionIdValid(boolean requestedSessionIdValid) {
		this.requestedSessionIdValid = requestedSessionIdValid;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void setSession(PortletSession session) {
		this.session = session;

		if (session instanceof MockPortletSession) {
			MockPortletSession mockSession = ((MockPortletSession) session);
			mockSession.access();
		}
	}

	public void setUserPrincipal(Principal userPrincipal) {
		this.userPrincipal = userPrincipal;
	}

	public void setWindowID(String windowID) {
		this.windowID = windowID;
	}

	public void setWindowState(WindowState windowState) {
		Assert.notNull(windowState, "WindowState must not be null");
		this.windowState = windowState;
	}

	/**
	 * Check whether this request is still active (that is, not completed yet), throwing an IllegalStateException if not
	 * active anymore.
	 */
	protected void checkActive() throws IllegalStateException {

		if (!this.active) {
			throw new IllegalStateException("Request is not active anymore");
		}
	}

	// ---------------------------------------------------------------------
	// Lifecycle methods
	// ---------------------------------------------------------------------

	/**
	 * Return the Portlet 2.0 lifecycle id for the current phase.
	 */
	protected String getLifecyclePhase() {
		return null;
	}

}
