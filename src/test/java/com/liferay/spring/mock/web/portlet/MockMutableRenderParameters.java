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

import javax.portlet.MutableRenderParameters;


/**
 * Mock implementation of the {@link MutableRenderParameters} interface.
 *
 * @author  Neil Griffin
 * @since   5.1.0
 */
public class MockMutableRenderParameters extends MockMutablePortletParameters implements MutableRenderParameters {

	@Override
	public void clearPrivate() {
	}

	@Override
	public void clearPublic() {
	}

	@Override
	public MutableRenderParameters clone() {
		return null;
	}

	@Override
	public boolean isPublic(String name) {
		return false;
	}
}
