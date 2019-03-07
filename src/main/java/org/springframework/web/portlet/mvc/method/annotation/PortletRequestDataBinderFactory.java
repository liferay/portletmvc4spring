package org.springframework.web.portlet.mvc.method.annotation;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;

import java.util.List;

/**
 * Creates a {@code PortletRequestDataBinder}.
 *
 * @author Rossen Stoyanchev
 * @author Neil Griffin
 * @since 5.1
 */
public class PortletRequestDataBinderFactory extends
	InitBinderDataBinderFactory {

	public PortletRequestDataBinderFactory(
		@Nullable List<InvocableHandlerMethod> binderMethods,
		@Nullable WebBindingInitializer initializer) {
		super(binderMethods, initializer);
	}

	@Override
	protected WebDataBinder createBinderInstance(
		@Nullable Object target, String objectName, NativeWebRequest webRequest) throws Exception {
		return new PortletRequestDataBinder(target, objectName);
	}
}

