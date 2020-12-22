package com.liferay.portletmvc4spring.demo.applicant.webflow.validators;

import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.Applicant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Component
public class ApplicantValidator {

	public void validatePage1(Applicant applicant, ValidationContext context) {
        MessageContext messages = context.getMessageContext();

		if (!messages.hasErrorMessages()) {

			if (LOG.isDebugEnabled()) {
				LOG.debug("firstName=" + applicant.getFirstName());
				LOG.debug("lastName=" + applicant.getLastName());
				LOG.debug("emailAddress=" + applicant.getEmailAddress());
				LOG.debug("phoneNumber=" + applicant.getPhoneNumber());
				LOG.debug("dateOfBirth=" + applicant.getDateOfBirth());
				LOG.debug("city=" + applicant.getCity());
				LOG.debug("provinceId=" + applicant.getProvinceId());
				LOG.debug("postalCode=" + applicant.getPostalCode());
				LOG.debug("comments=" + applicant.getComments());
			}

		}
        
        
    }

	private final static Logger LOG = LoggerFactory.getLogger(ApplicantValidator.class);

	
}
