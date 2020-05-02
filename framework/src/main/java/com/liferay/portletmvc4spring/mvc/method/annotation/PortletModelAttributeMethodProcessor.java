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
package com.liferay.portletmvc4spring.mvc.method.annotation;

import javax.portlet.PortletRequest;

import org.springframework.util.Assert;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import com.liferay.portletmvc4spring.bind.PortletRequestDataBinder;


/**
 * A Portlet-specific {@link ModelAttributeMethodProcessor} that applies data binding through a WebDataBinder of type
 * {@link PortletRequestDataBinder}.
 *
 * <p>Also adds a fall-back strategy to instantiate the model attribute from a URI template variable or from a request
 * parameter if the name matches the model attribute name and there is an appropriate type conversion strategy.
 *
 * @author  Neil Griffin
 * @since   5.1
 */
public class PortletModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor {

	public PortletModelAttributeMethodProcessor(boolean annotationNotRequired) {
		super(annotationNotRequired);
	}

	/**
	 * This implementation downcasts {@link WebDataBinder} to {@link PortletRequestDataBinder} before binding.
	 *
	 * @see  PortletRequestDataBinderFactory
	 */
	@Override
	protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
		PortletRequest portletRequest = request.getNativeRequest(PortletRequest.class);
		Assert.state(portletRequest != null, "No PortletRequest");

		PortletRequestDataBinder portletBinder = (PortletRequestDataBinder) binder;
		portletBinder.bind(portletRequest);
	}
}
