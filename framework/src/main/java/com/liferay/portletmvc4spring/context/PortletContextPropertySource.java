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
package com.liferay.portletmvc4spring.context;

import jakarta.portlet.PortletContext;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import org.springframework.util.StringUtils;


/**
 * {@link PropertySource} that reads init parameters from a {@link PortletContext} object.
 *
 * @author  Chris Beams
 * @since   3.1
 * @see     PortletConfigPropertySource
 */
public class PortletContextPropertySource extends EnumerablePropertySource<PortletContext> {

	public PortletContextPropertySource(String name, PortletContext portletContext) {
		super(name, portletContext);
	}

	@Override
	public String getProperty(String name) {
		return this.source.getInitParameter(name);
	}

	@Override
	public String[] getPropertyNames() {
		return StringUtils.toStringArray(this.source.getInitParameterNames());
	}

}
