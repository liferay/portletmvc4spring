package portlet1.controller;

import portlet1.dto.Car;

import com.liferay.portletmvc4spring.bind.annotation.ActionMapping;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import java.util.Locale;

import javax.portlet.ActionResponse;
import javax.portlet.MutableRenderParameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.MessageSource;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Controller
@RequestMapping("VIEW")
public class CarController {

	private static final Logger logger = LoggerFactory.getLogger(CarController.class);

	@Autowired
	private LocalValidatorFactoryBean localValidatorFactoryBean;

	@Autowired
	private MessageSource messageSource;

	@ActionMapping
	public void submitApplicant(@ModelAttribute("car") Car car, BindingResult bindingResult, ModelMap modelMap,
								Locale locale, ActionResponse actionResponse, SessionStatus sessionStatus) {

		localValidatorFactoryBean.validate(car, bindingResult);

		if (!bindingResult.hasErrors()) {

			if (logger.isDebugEnabled()) {
				logger.debug("make=" + car.getMake());
				logger.debug("model=" + car.getModel());
				logger.debug("year=" + car.getYear());
			}

			MutableRenderParameters mutableRenderParameters = actionResponse.getRenderParameters();

			mutableRenderParameters.setValue("javax.portlet.action", "success");

			sessionStatus.setComplete();

			modelMap.addAttribute("globalInfoMessage",
				messageSource.getMessage("your-request-processed-successfully", null, locale));
		}
	}

	@RenderMapping
	public String prepareView(Model model) {
		return "car";
	}

	@ModelAttribute("car")
	private Car _getCarModelAttribute() {
		return new Car();
	}
}