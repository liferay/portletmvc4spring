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
package com.liferay.portletmvc4spring.demo.applicant.webflow.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;


/**
 * @author  Neil Griffin
 */
public class Applicant implements Serializable {

	private static final long serialVersionUID = 2774594923346476261L;

	private List<Attachment> attachments;

	@NotNull
	@Pattern(regexp = "\\S+", message = "Value is required")
	// @NotBlank - Requires validation-api-2.0
	private String city;

	private String comments;

	@Past
	@DateTimeFormat(pattern = "MM-dd-yyyy")
	private Date dateOfBirth;

	// @Email - Requires validation-api-2.0
	@NotNull
	@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,6}$", message = "Valid email is required")
	// @NotBlank - Requires validation-api-2.0
	private String emailAddress;

	@NotNull
	@Pattern(regexp = "\\S+", message = "Value is required")
	// @NotBlank - Requires validation-api-2.0
	private String firstName;

	@NotNull
	@Pattern(regexp = "\\S+", message = "Value is required")
	// @NotBlank - Requires validation-api-2.0
	private String lastName;

	@NotNull
	// @NotBlank - Requires validation-api-2.0
	@Pattern(regexp = "^(\\(\\d{3}\\)|\\d{3})[.-]?\\d{3}[.-]?\\d{4}$", message = "Valid phone number xxx-xxx-xxxx is required")
	private String phoneNumber;

	@NotNull
	@Pattern(regexp = "\\S+", message = "Value is required")
	// @NotBlank - Requires validation-api-2.0
	private String postalCode;

	@NotNull
	// @Positive - Requires validation-api-2.0
	@Min(value = 1, message = "Positive numeric value is required")
	private Long provinceId;

	public Applicant(List<Attachment> attachments) {
		this.attachments = attachments;

		/*
		Calendar calendar = new GregorianCalendar();
		this.dateOfBirth = calendar.getTime();
		*/
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public String getCity() {
		return city;
	}

	public String getComments() {
		return comments;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public Long getProvinceId() {
		return provinceId;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public void setProvinceId(Long provinceId) {
		this.provinceId = provinceId;
	}

}
