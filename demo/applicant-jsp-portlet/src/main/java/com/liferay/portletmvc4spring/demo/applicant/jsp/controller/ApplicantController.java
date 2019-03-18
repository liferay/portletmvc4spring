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
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.portlet.ActionResponse;
import javax.portlet.MutableRenderParameters;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import com.liferay.portletmvc4spring.bind.annotation.ActionMapping;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;
import com.liferay.portletmvc4spring.bind.annotation.ResourceMapping;
import com.liferay.portletmvc4spring.demo.applicant.jsp.dto.Applicant;
import com.liferay.portletmvc4spring.demo.applicant.jsp.dto.Attachment;
import com.liferay.portletmvc4spring.demo.applicant.jsp.dto.City;
import com.liferay.portletmvc4spring.demo.applicant.jsp.dto.TransientUpload;
import com.liferay.portletmvc4spring.demo.applicant.jsp.service.CityService;
import com.liferay.portletmvc4spring.demo.applicant.jsp.service.ProvinceService;


/**
 * @author  Neil Griffin
 */
@Controller
@RequestMapping("VIEW")
public class ApplicantController {

	private static final Logger logger = LoggerFactory.getLogger(ApplicantController.class);

	@Autowired
	private AttachmentManager attachmentManager;

	@Autowired
	private CityService cityService;

	@Autowired
	private LocalValidatorFactoryBean localValidatorFactoryBean;

	@Autowired
	private ProvinceService provinceService;

	@ResourceMapping("autoFill")
	public void autoFill(@RequestParam("postalCode") String postalCode, Writer writer) {

		try {
			City city = cityService.getCityByPostalCode(postalCode);

			if (city != null) {
				writer.write("{");
				writer.write("\"cityName\": \"");
				writer.write(city.getCityName());
				writer.write("\",");
				writer.write("\"provinceId\": \"");
				writer.write(String.valueOf(city.getProvinceId()));
				writer.write("\"}");
			}
		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@ResourceMapping("deleteFile")
	public void deleteFile(@ModelAttribute("applicant") Applicant applicant,
		@RequestParam("attachmentIndex") int attachmentIndex, Writer writer) {

		List<Attachment> attachments = applicant.getAttachments();
		Attachment attachment = attachments.remove(attachmentIndex);
		File file = attachment.getFile();
		file.delete();
		_writeTabularJSON(writer, attachments);
	}

	@RenderMapping
	public String prepareView(Model model, PortletPreferences portletPreferences) {

		model.addAttribute("jQueryDatePattern",
			_getJQueryDatePattern(portletPreferences.getValue("datePattern", null)));

		model.addAttribute("portletMVC4SpringVersion", _getPackageVersion("com.liferay.portletmvc4spring"));

		model.addAttribute("provinces", provinceService.getAllProvinces());

		model.addAttribute("springFrameworkVersion",
			_getPackageVersion("org.springframework.web.server", "org.springframework.ui"));

		return "applicant";
	}

	@RenderMapping(params = "javax.portlet.action=success")
	public String showConfirmation() {
		return "confirmation";
	}

	@ActionMapping
	public void submitApplicant(@ModelAttribute("applicant") Applicant applicant, BindingResult bindingResult,
		ActionResponse actionResponse, SessionStatus sessionStatus) {

		localValidatorFactoryBean.validate(applicant, bindingResult);

		if (!bindingResult.hasErrors()) {

			if (logger.isDebugEnabled()) {
				logger.debug("firstName=" + applicant.getFirstName());
				logger.debug("lastName=" + applicant.getLastName());
				logger.debug("emailAddress=" + applicant.getEmailAddress());
				logger.debug("phoneNumber=" + applicant.getPhoneNumber());
				logger.debug("dateOfBirth=" + applicant.getDateOfBirth());
				logger.debug("city=" + applicant.getCity());
				logger.debug("provinceId=" + applicant.getProvinceId());
				logger.debug("postalCode=" + applicant.getPostalCode());
				logger.debug("comments=" + applicant.getComments());
			}

			List<Attachment> attachments = applicant.getAttachments();

			for (Attachment attachment : attachments) {

				logger.debug("attachment=[{}]", attachment.getName());

				File file = attachment.getFile();
				file.delete();
			}

			MutableRenderParameters mutableRenderParameters = actionResponse.getRenderParameters();

			mutableRenderParameters.setValue("javax.portlet.action", "success");

			sessionStatus.setComplete();
		}
	}

	@ResourceMapping("uploadFiles")
	public void uploadFiles(@ModelAttribute("transientUpload") TransientUpload transientUpload,
		@ModelAttribute("applicant") Applicant applicant, PortletSession portletSession, Writer writer) {

		List<MultipartFile> transientMultipartFiles = transientUpload.getMultipartFiles();

		if (transientMultipartFiles != null) {

			File attachmentDir = attachmentManager.getAttachmentDir(portletSession.getId());

			if (!attachmentDir.exists()) {
				attachmentDir.mkdir();
			}

			List<Attachment> attachments = applicant.getAttachments();

			for (MultipartFile transientMultipartFile : transientMultipartFiles) {

				if (!transientMultipartFile.isEmpty()) {

					File copiedFile = new File(attachmentDir, transientMultipartFile.getOriginalFilename());

					try {
						transientMultipartFile.transferTo(copiedFile);
						attachments.add(new Attachment(copiedFile));
					}
					catch (IOException e) {
						logger.error(e.getMessage(), e);
					}
				}
			}

			_writeTabularJSON(writer, attachments);
		}
	}

	@InitBinder
	protected void initBinder(WebDataBinder webDataBinder, PortletPreferences portletPreferences) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(portletPreferences.getValue("datePattern", null));
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

	@ModelAttribute("applicant")
	private Applicant _getApplicantModelAttribute(PortletSession portletSession) {
		return new Applicant(attachmentManager.getAttachments(portletSession.getId()));
	}

	private String _getJQueryDatePattern(String datePattern) {

		String jQueryDatePattern = datePattern;

		if (datePattern.contains("yyyy")) {
			jQueryDatePattern = datePattern.replaceAll("yyyy", "yy");
		}
		else if (datePattern.contains("yy")) {
			jQueryDatePattern = datePattern.replaceAll("yy", "y");
		}

		jQueryDatePattern = jQueryDatePattern.replaceAll("M", "m");

		return jQueryDatePattern;
	}

	private String _getPackageVersion(String... packageNames) {

		Package pkg = null;

		for (String packageName : packageNames) {
			pkg = Package.getPackage(packageName);

			if (pkg != null) {
				break;
			}
		}

		if (pkg == null) {
			return "(Not Present)";
		}

		return pkg.getImplementationVersion();
	}

	private void _writeTabularJSON(Writer writer, List<Attachment> attachments) {

		try {

			writer.write("[");

			boolean first = true;

			for (Attachment attachment : attachments) {

				if (first) {
					first = false;
				}
				else {
					writer.write(",");
				}

				writer.write("{\"fileName\": \"");

				writer.write(attachment.getName());
				writer.write("\",\"size\":\"");
				writer.write(String.valueOf(attachment.getSize()));
				writer.write("\"}");
			}

			writer.write("]");
		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
