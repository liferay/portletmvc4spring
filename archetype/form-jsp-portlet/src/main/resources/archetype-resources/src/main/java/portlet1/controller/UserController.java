package portlet1.controller;

import portlet1.dto.User;

import com.liferay.portletmvc4spring.bind.annotation.ActionMapping;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import java.util.Calendar;
import java.util.Locale;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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

import org.springframework.ui.ModelMap;

import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Controller
@RequestMapping("VIEW")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private LocalValidatorFactoryBean localValidatorFactoryBean;

	@Autowired
	private MessageSource messageSource;

	@ActionMapping
	public void submitApplicant(@ModelAttribute("user") User user, BindingResult bindingResult, ModelMap modelMap,
								Locale locale, ActionResponse actionResponse, SessionStatus sessionStatus) {

		localValidatorFactoryBean.validate(user, bindingResult);

		if (!bindingResult.hasErrors()) {

			if (logger.isDebugEnabled()) {
				logger.debug("firstName=" + user.getFirstName());
				logger.debug("lastName=" + user.getLastName());
			}

			MutableRenderParameters mutableRenderParameters = actionResponse.getRenderParameters();

			mutableRenderParameters.setValue("javax.portlet.action", "success");

			sessionStatus.setComplete();
		}
	}

	@RenderMapping
	public String prepareView() {
		return "user";
	}

	@RenderMapping(params = "javax.portlet.action=success")
	public String showGreeting(ModelMap modelMap) {

		DateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy G");

		Calendar todayCalendar = Calendar.getInstance();

		modelMap.put("todaysDate", dateFormat.format(todayCalendar.getTime()));

		return "greeting";
	}

	@ModelAttribute("user")
	private User _getUserModelAttribute() {
		return new User();
	}
}