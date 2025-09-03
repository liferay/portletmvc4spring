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

import jakarta.portlet.MutableResourceParameters;
import jakarta.portlet.ResourceParameters;


/**
 * Mock implementation of the {@link ResourceParameters} interface.
 *
 * @author  Neil Griffin
 * @since   5.1.0
 */
public class MockResourceParameters extends MockPortletParameters implements ResourceParameters {

	@Override
	public MutableResourceParameters clone() {
		return null;
	}
}
