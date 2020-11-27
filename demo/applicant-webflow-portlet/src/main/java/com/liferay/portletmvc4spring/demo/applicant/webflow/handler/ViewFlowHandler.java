package com.liferay.portletmvc4spring.demo.applicant.webflow.handler;

import com.liferay.portletmvc4spring.webflow.mvc.portlet.AbstractFlowHandler;

/**
 * 
 * @author Fabian Bouché
 *
 */
public class ViewFlowHandler extends AbstractFlowHandler {

	public String getFlowId() {
		return "applicantFlow";
	}
	
}
