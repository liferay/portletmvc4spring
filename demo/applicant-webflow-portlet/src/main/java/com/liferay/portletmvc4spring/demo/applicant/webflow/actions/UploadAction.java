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
import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.webflow.execution.RequestContext;

import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.Applicant;
import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.Attachment;
import com.liferay.portletmvc4spring.multipart.DefaultMultipartActionRequest;
import com.liferay.portletmvc4spring.webflow.context.portlet.PortletExternalContext;


/**
 * @author  Fabian Bouché
 */
@Component
public class UploadAction implements ServletContextAware {

	private static final Logger logger = LoggerFactory.getLogger(UploadAction.class);

	private ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void uploadFile(Applicant applicant, RequestContext requestContext) {

		final PortletExternalContext context = (PortletExternalContext) requestContext.getExternalContext();

		final DefaultMultipartActionRequest defaultMultipartActionRequest = (DefaultMultipartActionRequest)
			context.getNativeRequest();

		Map<String, MultipartFile> fileMap = defaultMultipartActionRequest.getFileMap();
		fileMap.forEach((key, multipartFile) -> {

			logger.debug("Uploading file... {}", key);

			File tempDirectory = (File) servletContext.getAttribute(ServletContext.TEMPDIR);

			File file = new File(tempDirectory.getAbsolutePath() + "/" + multipartFile.getOriginalFilename());

			try {
				multipartFile.transferTo(file);

				Attachment attachment = new Attachment(file);
				applicant.getAttachments().add(attachment);
			}
			catch (IllegalStateException e) {
				logger.error("Failed to upload file", e);
			}
			catch (IOException e) {
				logger.error("Failed to upload file", e);
			}

			logger.debug("Successfully uploaded file {}", key);

		});

	}
}
