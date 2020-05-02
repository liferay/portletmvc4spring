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
package com.liferay.portletmvc4spring.demo.applicant.thymeleaf.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.portlet.ResourceResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;

import org.springframework.ui.ModelMap;

import org.springframework.web.bind.annotation.RequestMapping;

import com.liferay.portletmvc4spring.bind.annotation.ResourceMapping;


/**
 * @author  Neil Griffin
 */
@Controller
@RequestMapping("VIEW")
public class TermsController {

	private static final Logger logger = LoggerFactory.getLogger(TermsController.class);

	@ResourceMapping("acceptTerms")
	public String acceptTerms(ModelMap modelMap, ResourceResponse resourceResponse) {

		// Thymeleaf
		modelMap.put("viewTermsAgainResourceURL", ControllerUtil.createResourceURL(resourceResponse, "viewTermsAgain"));

		return "acceptance";
	}

	@ResourceMapping("viewTerms")
	public String viewTerms(ModelMap modelMap, ResourceResponse resourceResponse) {

		_populateViewTermsModel(modelMap, resourceResponse);

		return "terms";
	}

	@ResourceMapping("viewTermsAgain")
	public String viewTermsAgain(ModelMap modelMap, ResourceResponse resourceResponse) {
		logger.debug("Navigating to Terms of Service");

		_populateViewTermsModel(modelMap, resourceResponse);

		return "terms";
	}

	private void _populateViewTermsModel(ModelMap modelMap, ResourceResponse resourceResponse) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy");

		Calendar todayCalendar = Calendar.getInstance();

		modelMap.put("thisYear", dateFormat.format(todayCalendar.getTime()));

		// Thymeleaf
		modelMap.put("acceptTermsResourceURL", ControllerUtil.createResourceURL(resourceResponse, "acceptTerms"));
	}
}
