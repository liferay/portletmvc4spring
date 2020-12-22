package com.liferay.portletmvc4spring.demo.applicant.webflow.controller;

import com.liferay.portletmvc4spring.bind.annotation.ResourceMapping;
import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.City;
import com.liferay.portletmvc4spring.demo.applicant.webflow.service.CityService;

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("VIEW")
public class ApplicantController {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicantController.class);
	
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
			LOG.error(e.getMessage(), e);
		}
	}
	
}
