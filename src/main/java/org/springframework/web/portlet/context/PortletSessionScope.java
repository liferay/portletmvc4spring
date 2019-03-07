/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.portlet.context;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.SessionScope;

/**
 * @author Neil Griffin
 */
public class PortletSessionScope extends SessionScope {

	private final int scope;

	/**
	 * Create a new SessionScope, storing attributes in a locally
	 * isolated session (or default session, if there is no distinction
	 * between a global session and a component-specific session).
	 */
	public PortletSessionScope() {
		this.scope = RequestAttributes.SCOPE_SESSION;
	}

	/**
	 * Create a new SessionScope, specifying whether to store attributes
	 * in the global session, provided that such a distinction is available.
	 * <p>This distinction is important for Portlet environments, where there
	 * are two notions of a session: "portlet scope" and "application scope".
	 * If this flag is on, objects will be put into the "application scope" session;
	 * else they will end up in the "portlet scope" session (the typical default).
	 * <p>In a Servlet environment, this flag is effectively ignored.
	 * @param globalSession {@code true} in case of the global session as target;
	 * {@code false} in case of a component-specific session as target
	 * @see org.springframework.web.portlet.context.PortletRequestAttributes
	 * @see RequestAttributes
	 */
	public PortletSessionScope(boolean globalSession) {
		this.scope = (globalSession ? PortletRequestAttributes.SCOPE_GLOBAL_SESSION : RequestAttributes.SCOPE_SESSION);
	}

	@Override
	public int getScope() {
		return this.scope;
	}
}
