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
package com.liferay.portletmvc4spring.mvc.method.annotation;

import java.lang.reflect.Method;

import org.springframework.lang.Nullable;

import org.springframework.ui.ExtendedModelMap;

import org.springframework.web.context.request.NativeWebRequest;

import com.liferay.portletmvc4spring.ModelAndView;


/**
 * This interface is inspired by the {@link org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver} and is
 * meant for use with the portlet version of {@link ModelAndView}.
 *
 * @author  Neil Griffin
 */
public interface PortletModelAndViewResolver {

	/** Marker to be returned when the resolver does not know how to handle the given method parameter. */
	ModelAndView UNRESOLVED = new ModelAndView();

	ModelAndView resolveModelAndView(Method handlerMethod, Class<?> handlerType, @Nullable Object returnValue,
		ExtendedModelMap implicitModel, NativeWebRequest webRequest);
}
