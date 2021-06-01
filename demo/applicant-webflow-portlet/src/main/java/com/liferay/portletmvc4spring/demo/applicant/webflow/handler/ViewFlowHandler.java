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
package com.liferay.portletmvc4spring.demo.applicant.webflow.handler;

import com.liferay.portletmvc4spring.webflow.mvc.portlet.AbstractFlowHandler;


/**
 * @author  Fabian Bouché
 */
public class ViewFlowHandler extends AbstractFlowHandler {

	public String getFlowId() {
		return "applicantFlow";
	}

}
