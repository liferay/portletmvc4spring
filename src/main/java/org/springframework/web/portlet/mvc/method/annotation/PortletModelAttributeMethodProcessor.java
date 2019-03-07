package org.springframework.web.portlet.mvc.method.annotation;

import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.portlet.PortletRequest;

/**
 * A Portlet-specific {@link ModelAttributeMethodProcessor} that applies data
 * binding through a WebDataBinder of type {@link PortletRequestDataBinder}.
 *
 * <p>Also adds a fall-back strategy to instantiate the model attribute from a
 * URI template variable or from a request parameter if the name matches the
 * model attribute name and there is an appropriate type conversion strategy.
 *
 * @author Neil Griffin
 * @since 5.1
 */
public class PortletModelAttributeMethodProcessor extends
	ServletModelAttributeMethodProcessor {

	public PortletModelAttributeMethodProcessor(boolean annotationNotRequired) {
		super(annotationNotRequired);
	}

	/**
	 * This implementation downcasts {@link WebDataBinder} to
	 * {@link PortletRequestDataBinder} before binding.
	 * @see PortletRequestDataBinderFactory
	 */
	@Override
	protected void bindRequestParameters(
		WebDataBinder binder, NativeWebRequest request) {
		PortletRequest portletRequest = request.getNativeRequest(PortletRequest.class);
		Assert.state(portletRequest != null, "No PortletRequest");
		PortletRequestDataBinder portletBinder = (PortletRequestDataBinder) binder;
		portletBinder.bind(portletRequest);
	}
}
