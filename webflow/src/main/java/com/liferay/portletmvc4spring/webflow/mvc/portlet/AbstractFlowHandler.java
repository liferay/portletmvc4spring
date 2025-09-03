/*
 * Copyright (c) 2000-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.portletmvc4spring.webflow.mvc.portlet;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.springframework.webflow.core.FlowException;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.FlowExecutionOutcome;


/**
 * Trivial flow handler base class that simply returns null for all operations. Subclasses should extend and override
 * which operations they need.
 *
 * @author  Keith Donald
 * @author  Rossen Stoyanchev
 */
public class AbstractFlowHandler implements FlowHandler {

	public MutableAttributeMap<Object> createExecutionInputMap(RenderRequest request) {
		return null;
	}

	public MutableAttributeMap<Object> createResourceExecutionInputMap(ResourceRequest request) {
		return null;
	}

	public String getFlowId() {
		return null;
	}

	public String handleException(FlowException e, PortletRequest request, RenderResponse response) {
		return null;
	}

	public String handleException(FlowException e, RenderRequest request, RenderResponse response) {
		return null;
	}

	public boolean handleExecutionOutcome(FlowExecutionOutcome outcome, ActionRequest request,
		ActionResponse response) {
		return false;
	}

	public String handleResourceException(FlowException e, ResourceRequest request, ResourceResponse response) {
		return null;
	}

}
