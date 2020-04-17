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
import java.util.List;

import org.springframework.core.MethodParameter;

import org.springframework.lang.Nullable;

import org.springframework.ui.ExtendedModelMap;

import org.springframework.util.Assert;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.liferay.portletmvc4spring.ModelAndView;


/**
 * This class is inspired by the {@link
 * org.springframework.web.servlet.mvc.method.annotation.ModelAndViewResolverMethodReturnValueHandler} and is meant for
 * use with {@link PortletModelAndViewResolver} rather than {@link
 * org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver}.
 *
 * @author  Neil Griffin
 */
public class PortletModelAndViewResolverMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

	@Nullable
	private final List<PortletModelAndViewResolver> mavResolvers;

	private final ModelAttributeMethodProcessor modelAttributeProcessor = new ModelAttributeMethodProcessor(true);

	/**
	 * Create a new instance.
	 */
	public PortletModelAndViewResolverMethodReturnValueHandler(
		@Nullable List<PortletModelAndViewResolver> mavResolvers) {
		this.mavResolvers = mavResolvers;
	}

	@Override
	public void handleReturnValue(@Nullable Object returnValue, MethodParameter returnType,
		ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {

		if (this.mavResolvers != null) {

			for (PortletModelAndViewResolver mavResolver : this.mavResolvers) {
				Class<?> handlerType = returnType.getContainingClass();
				Method method = returnType.getMethod();
				Assert.state(method != null, "No handler method");

				ExtendedModelMap model = (ExtendedModelMap) mavContainer.getModel();
				ModelAndView mav = mavResolver.resolveModelAndView(method, handlerType, returnValue, model, webRequest);

				if (mav != PortletModelAndViewResolver.UNRESOLVED) {
					mavContainer.addAllAttributes(mav.getModel());
					mavContainer.setViewName(mav.getViewName());

					if (!mav.isReference()) {
						mavContainer.setView(mav.getView());
					}

					return;
				}
			}
		}

		// No suitable ModelAndViewResolver...
		if (this.modelAttributeProcessor.supportsReturnType(returnType)) {
			this.modelAttributeProcessor.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
		}
		else {
			throw new UnsupportedOperationException("Unexpected return type: " +
				returnType.getParameterType().getName() + " in method: " + returnType.getMethod());
		}
	}

	/**
	 * Always returns {@code true}. See class-level note.
	 */
	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return true;
	}
}
