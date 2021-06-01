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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import org.springframework.webflow.execution.RequestContext;

import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.Applicant;
import com.liferay.portletmvc4spring.webflow.context.portlet.PortletExternalContext;


/**
 * @author  Fabian Bouché
 */
@Component
public class DeleteAction {

	private static final Logger logger = LoggerFactory.getLogger(DeleteAction.class);

	public void deleteAttachment(Applicant applicant, RequestContext requestContext) {

		final PortletExternalContext context = (PortletExternalContext) requestContext.getExternalContext();

		int attachmentIndex = Integer.valueOf(context.getRequestParameterMap().get("attachmentIndex", "-1"));

		logger.debug("Remove attachment with index {}", attachmentIndex);

		File file = applicant.getAttachments().get(attachmentIndex).getFile();

		file.delete();

		applicant.getAttachments().remove(attachmentIndex);

	}
}
