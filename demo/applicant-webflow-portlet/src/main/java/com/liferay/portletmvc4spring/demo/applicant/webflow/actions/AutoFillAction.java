package com.liferay.portletmvc4spring.demo.applicant.webflow.actions;

import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.Applicant;
import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.City;
import com.liferay.portletmvc4spring.demo.applicant.webflow.service.CityService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.webflow.execution.RequestContext;

@Component
public class AutoFillAction {

	public void autoFillCity(Applicant applicant, RequestContext requestContext) {

		LOG.debug("Auto Fill City");
		
		City city = cityService.getCityByPostalCode(applicant.getPostalCode());
		
		if(city != null) {
			applicant.setCity(city.getCityName());
			applicant.setProvinceId(city.getProvinceId());
		}
		
	}
	
	private final static Logger LOG = LoggerFactory.getLogger(AutoFillAction.class);
	
	@Autowired
	private CityService cityService;
}
