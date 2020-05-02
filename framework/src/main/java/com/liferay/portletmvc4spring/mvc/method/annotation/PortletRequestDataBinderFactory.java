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

import java.util.List;

import org.springframework.lang.Nullable;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.support.InvocableHandlerMethod;

import com.liferay.portletmvc4spring.bind.PortletRequestDataBinder;


/**
 * Creates a {@code PortletRequestDataBinder}.
 *
 * @author  Rossen Stoyanchev
 * @author  Neil Griffin
 * @since   5.1
 */
public class PortletRequestDataBinderFactory extends InitBinderDataBinderFactory {

	public PortletRequestDataBinderFactory(@Nullable List<InvocableHandlerMethod> binderMethods,
		@Nullable WebBindingInitializer initializer) {
		super(binderMethods, initializer);
	}

	@Override
	protected WebDataBinder createBinderInstance(@Nullable Object target, String objectName,
		NativeWebRequest webRequest) throws Exception {
		return new PortletRequestDataBinder(target, objectName);
	}
}
