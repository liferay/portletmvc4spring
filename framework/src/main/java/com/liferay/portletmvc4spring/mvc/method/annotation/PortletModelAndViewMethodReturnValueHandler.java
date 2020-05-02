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

import org.springframework.core.MethodParameter;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;

import com.liferay.portletmvc4spring.ModelAndView;


/**
 * @author  Neil Griffin
 */
public class PortletModelAndViewMethodReturnValueHandler extends ModelAndViewMethodReturnValueHandler {

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest) throws Exception {

		if (returnValue == null) {
			mavContainer.setRequestHandled(true);

			return;
		}

		ModelAndView modelAndView = (ModelAndView) returnValue;

		if (modelAndView.isReference()) {
			String viewName = modelAndView.getViewName();
			mavContainer.setViewName(viewName);

			if ((viewName != null) && isRedirectViewName(viewName)) {
				mavContainer.setRedirectModelScenario(true);
			}
		}
		else {
			Object view = modelAndView.getView();
			mavContainer.setView(view);

			if ((view instanceof SmartView) && ((SmartView) view).isRedirectView()) {
				mavContainer.setRedirectModelScenario(true);
			}
		}

		mavContainer.addAllAttributes(modelAndView.getModel());

	}

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return ModelAndView.class.isAssignableFrom(returnType.getParameterType());
	}
}
