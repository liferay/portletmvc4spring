/**
 * Copyright (c) 2000-2019 the original author or authors.
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
package com.liferay.portletmvc4spring.demo.applicant.jsp.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;

import org.springframework.web.context.ServletContextAware;

import com.liferay.portletmvc4spring.demo.applicant.jsp.dto.Attachment;


/**
 * @author  Neil Griffin
 */
@Component
public class AttachmentManager implements ServletContextAware {

	private ServletContext servletContext;

	public File getAttachmentDir(String sessionId) {

		File tempDir = (File) servletContext.getAttribute(ServletContext.TEMPDIR);

		return new File(tempDir, sessionId);
	}

	public List<Attachment> getAttachments(String sessionId) {
		return getAttachments(getAttachmentDir(sessionId));
	}

	public List<Attachment> getAttachments(File attachmentDir) {

		List<Attachment> attachments = new ArrayList<>();

		if (attachmentDir.exists()) {

			File[] files = attachmentDir.listFiles();

			for (File file : files) {
				attachments.add(new Attachment(file));
			}
		}

		return attachments;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
