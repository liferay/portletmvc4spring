package com.liferay.portletmvc4spring.demo.applicant.webflow.actions;

import com.liferay.portletmvc4spring.demo.applicant.webflow.dto.Applicant;
import com.liferay.portletmvc4spring.webflow.context.portlet.PortletExternalContext;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.webflow.execution.RequestContext;

@Component
public class DeleteAction {

	public void deleteAttachment(Applicant applicant, RequestContext requestContext) {

		final PortletExternalContext context = (PortletExternalContext) requestContext.getExternalContext();
		
		int attachmentIndex = Integer.valueOf(context.getRequestParameterMap().get("attachmentIndex", "-1"));

		LOG.debug("Remove attachment with index {}", attachmentIndex);
		
		File file = applicant.getAttachments().get(attachmentIndex).getFile();

		file.delete();

		applicant.getAttachments().remove(attachmentIndex);
		
	}
	
	private final static Logger LOG = LoggerFactory.getLogger(DeleteAction.class); 
}
