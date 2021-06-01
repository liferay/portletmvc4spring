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
package com.liferay.portletmvc4spring.demo.applicant.webflow.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.springframework.webflow.execution.RequestContext;

import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.Applicant;
import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.City;
import com.liferay.portletmvc4spring.demo.applicant.webflow.service.CityService;


/**
 * @author  Fabian Bouché
 */
@Component
public class AutoFillAction {

	private static final Logger logger = LoggerFactory.getLogger(AutoFillAction.class);

	@Autowired
	private CityService cityService;

	public void autoFillCity(Applicant applicant, RequestContext requestContext) {

		logger.debug("Auto Fill City");

		City city = cityService.getCityByPostalCode(applicant.getPostalCode());

		if (city != null) {
			applicant.setCity(city.getCityName());
			applicant.setProvinceId(city.getProvinceId());
		}

	}
}
