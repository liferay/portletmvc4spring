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
package com.liferay.portletmvc4spring.demo.applicant.jsp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


/**
 * @author  Neil Griffin
 */
public class Preferences {

	@NotNull
	@Pattern(regexp = "\\S+", message = "Value is required")
	// @NotBlank - Requires validation-api-2.0
	private String datePattern;

	public Preferences() {
	}

	public Preferences(String datePattern) {
		this.datePattern = datePattern;
	}

	public String getDatePattern() {
		return datePattern;
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}
}
