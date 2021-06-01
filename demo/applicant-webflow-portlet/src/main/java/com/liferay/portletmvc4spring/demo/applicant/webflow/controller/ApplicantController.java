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
package com.liferay.portletmvc4spring.demo.applicant.webflow.controller;

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.liferay.portletmvc4spring.bind.annotation.ResourceMapping;
import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.City;
import com.liferay.portletmvc4spring.demo.applicant.webflow.service.CityService;


/**
 * @author  Neil Griffin
 */
@Controller
@RequestMapping("VIEW")
public class ApplicantController {

	private static final Logger logger = LoggerFactory.getLogger(ApplicantController.class);

	@Autowired
	private CityService cityService;

	@ResourceMapping("autoFill")
	public void autoFill(@RequestParam("postalCode") String postalCode, Writer writer) {

		try {
			City city = cityService.getCityByPostalCode(postalCode);

			if (city != null) {
				writer.write("{");
				writer.write("\"cityName\": \"");
				writer.write(city.getCityName());
				writer.write("\",");
				writer.write("\"provinceId\": \"");
				writer.write(String.valueOf(city.getProvinceId()));
				writer.write("\"}");
			}
		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
