package com.liferay.portletmvc4spring.demo.applicant.webflow.actions;

import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.Applicant;
import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.Attachment;
import com.liferay.portletmvc4spring.multipart.DefaultMultipartActionRequest;
import com.liferay.portletmvc4spring.webflow.context.portlet.PortletExternalContext;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.webflow.execution.RequestContext;

@Component
public class UploadAction implements ServletContextAware {

	private ServletContext servletContext;

	public void uploadFile(Applicant applicant, RequestContext requestContext) {

		final PortletExternalContext context = (PortletExternalContext) requestContext.getExternalContext();
		
		final DefaultMultipartActionRequest defaultMultipartActionRequest = (DefaultMultipartActionRequest)context.getNativeRequest();
		
		Map<String, MultipartFile> fileMap = defaultMultipartActionRequest.getFileMap();
		fileMap.forEach((key, multipartFile) -> {

			LOG.debug("Uploading file... {}", key);

			File tempDirectory = (File) servletContext.getAttribute(ServletContext.TEMPDIR);
			
		    File file = new File(tempDirectory.getAbsolutePath() + "/" + multipartFile.getOriginalFilename());
		    try {
				multipartFile.transferTo(file);
				Attachment attachment = new Attachment(file);
				applicant.getAttachments().add(attachment);
			} catch (IllegalStateException e) {
				LOG.error("Failed to upload file", e);
			} catch (IOException e) {
				LOG.error("Failed to upload file", e);
			}

			LOG.debug("Successfully uploaded file {}", key);

		});

	}
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	private final static Logger LOG = LoggerFactory.getLogger(UploadAction.class); 
}
