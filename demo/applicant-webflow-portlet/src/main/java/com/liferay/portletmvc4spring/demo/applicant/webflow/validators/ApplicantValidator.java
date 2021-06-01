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
package com.liferay.portletmvc4spring.demo.applicant.webflow.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

import org.springframework.stereotype.Component;

import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.Applicant;


/**
 * @author  Fabian Bouché
 */
@Component
public class ApplicantValidator {

	private static final Logger logger = LoggerFactory.getLogger(ApplicantValidator.class);

	public void validatePage1(Applicant applicant, ValidationContext context) {
		MessageContext messages = context.getMessageContext();

		if (!messages.hasErrorMessages()) {

			if (logger.isDebugEnabled()) {
				logger.debug("firstName=" + applicant.getFirstName());
				logger.debug("lastName=" + applicant.getLastName());
				logger.debug("emailAddress=" + applicant.getEmailAddress());
				logger.debug("phoneNumber=" + applicant.getPhoneNumber());
				logger.debug("dateOfBirth=" + applicant.getDateOfBirth());
				logger.debug("city=" + applicant.getCity());
				logger.debug("provinceId=" + applicant.getProvinceId());
				logger.debug("postalCode=" + applicant.getPostalCode());
				logger.debug("comments=" + applicant.getComments());
			}

		}

	}

}
