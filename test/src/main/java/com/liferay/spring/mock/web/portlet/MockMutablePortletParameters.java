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
package com.liferay.spring.mock.web.portlet;

import java.util.Set;

import javax.portlet.MutablePortletParameters;
import javax.portlet.PortletParameters;


/**
 * Mock implementation of the {@link MutablePortletParameters} interface.
 *
 * @author  Neil Griffin
 * @since   5.1.0
 */
public class MockMutablePortletParameters extends MockPortletParameters implements MutablePortletParameters {

	@Override
	public MutablePortletParameters add(PortletParameters portletParameters) {

		MutablePortletParameters mutablePortletParameters = clone();

		if (portletParameters != null) {
			Set<String> names = portletParameters.getNames();

			for (String name : names) {
				parameters.put(name, portletParameters.getValues(name));
			}
		}

		return mutablePortletParameters;
	}

	@Override
	public void clear() {
		parameters.clear();
	}

	@Override
	public boolean removeParameter(String name) {

		boolean wasPresent = parameters.containsKey(name);

		parameters.remove(name);

		return wasPresent;
	}

	@Override
	public MutablePortletParameters set(PortletParameters portletParameters) {

		MutablePortletParameters mutablePortletParameters = clone();

		parameters.clear();

		if (portletParameters != null) {
			Set<String> names = portletParameters.getNames();

			for (String name : names) {
				parameters.put(name, portletParameters.getValues(name));
			}
		}

		return mutablePortletParameters;
	}

	@Override
	public String setValue(String name, String value) {

		String[] prevValues = parameters.remove(name);

		if (value == null) {
			parameters.put(name, null);
		}
		else {
			parameters.put(name, new String[] { value });
		}

		if ((prevValues != null) && (prevValues.length > 0)) {
			return prevValues[0];
		}

		return null;
	}

	@Override
	public String[] setValues(String name, String... values) {

		String[] prevValues = parameters.remove(name);

		parameters.put(name, values);

		return prevValues;
	}
}
