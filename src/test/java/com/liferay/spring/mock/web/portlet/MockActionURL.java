/**
 * Copyright (c) 2000-2019 the original author or authors.
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
package com.liferay.spring.mock.web.portlet;

import javax.portlet.ActionURL;
import javax.portlet.MimeResponse;
import javax.portlet.MutableActionParameters;
import javax.portlet.MutableRenderParameters;
import javax.portlet.PortalContext;


/**
 * Mock implementation of the {@link ActionURL} interface.
 *
 * @author  Neil Griffin
 * @since   5.1.0
 */
public class MockActionURL extends MockPortletURL implements ActionURL {

	private MimeResponse.Copy copy;
	private MutableActionParameters mutableActionParameters;
	private MutableRenderParameters mutableRenderParameters;

	public MockActionURL(PortalContext portalContext, MimeResponse.Copy copy) {
		super(portalContext, URL_TYPE_ACTION);
		this.copy = copy;
	}

	@Override
	public MutableActionParameters getActionParameters() {

		if (mutableActionParameters == null) {
			mutableActionParameters = new MockMutableActionParameters();
		}

		return mutableActionParameters;
	}

	@Override
	public MutableRenderParameters getRenderParameters() {

		if (mutableRenderParameters == null) {
			mutableRenderParameters = new MockMutableRenderParameters();
		}

		return mutableRenderParameters;
	}
}
