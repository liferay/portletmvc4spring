/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.portletmvc4spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import com.liferay.spring.mock.web.portlet.MockPortletConfig;
import com.liferay.spring.mock.web.portlet.MockPortletContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import com.liferay.portletmvc4spring.bind.PortletRequestBindingException;
import com.liferay.portletmvc4spring.context.PortletRequestHandledEvent;
import com.liferay.portletmvc4spring.context.StaticPortletApplicationContext;
import com.liferay.portletmvc4spring.handler.HandlerInterceptorAdapter;
import com.liferay.portletmvc4spring.handler.ParameterHandlerMapping;
import com.liferay.portletmvc4spring.handler.ParameterMappingInterceptor;
import com.liferay.portletmvc4spring.handler.PortletModeHandlerMapping;
import com.liferay.portletmvc4spring.handler.PortletModeParameterHandlerMapping;
import com.liferay.portletmvc4spring.handler.SimpleMappingExceptionResolver;
import com.liferay.portletmvc4spring.handler.SimplePortletHandlerAdapter;
import com.liferay.portletmvc4spring.handler.SimplePortletPostProcessor;
import com.liferay.portletmvc4spring.handler.UserRoleAuthorizationInterceptor;
import com.liferay.portletmvc4spring.multipart.DefaultMultipartActionRequest;
import com.liferay.portletmvc4spring.multipart.DefaultMultipartResourceRequest;
import com.liferay.portletmvc4spring.multipart.MultipartActionRequest;
import com.liferay.portletmvc4spring.multipart.MultipartResourceRequest;
import com.liferay.portletmvc4spring.multipart.PortletMultipartResolver;
import com.liferay.portletmvc4spring.mvc.Controller;
import com.liferay.portletmvc4spring.mvc.SimpleControllerHandlerAdapter;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Arjen Poutsma
 */
public class ComplexPortletApplicationContext extends StaticPortletApplicationContext {

	@Override
	public void refresh() throws BeansException {
		registerSingleton("standardHandlerAdapter", SimpleControllerHandlerAdapter.class);
		registerSingleton("portletHandlerAdapter", SimplePortletHandlerAdapter.class);
		registerSingleton("myHandlerAdapter", MyHandlerAdapter.class);

		registerSingleton("viewController", ViewController.class);
		registerSingleton("editController", EditController.class);
		registerSingleton("helpController1", HelpController1.class);
		registerSingleton("helpController2", HelpController2.class);
		registerSingleton("testController1", TestController1.class);
		registerSingleton("testController2", TestController2.class);
		registerSingleton("requestLocaleCheckingController", RequestLocaleCheckingController.class);
		registerSingleton("localeContextCheckingController", LocaleContextCheckingController.class);

		registerSingleton("exceptionThrowingHandler1", ExceptionThrowingHandler.class);
		registerSingleton("exceptionThrowingHandler2", ExceptionThrowingHandler.class);
		registerSingleton("unknownHandler", Object.class);

		registerSingleton("myPortlet", MyPortlet.class);
		registerSingleton("portletMultipartResolver", MockMultipartResolver.class);
		registerSingleton("portletPostProcessor", SimplePortletPostProcessor.class);
		registerSingleton("testListener", TestApplicationListener.class);

		ConstructorArgumentValues cvs = new ConstructorArgumentValues();
		cvs.addIndexedArgumentValue(0, new MockPortletContext());
		cvs.addIndexedArgumentValue(1, "complex");
		registerBeanDefinition("portletConfig", new RootBeanDefinition(MockPortletConfig.class, cvs, null));

		UserRoleAuthorizationInterceptor userRoleInterceptor = new UserRoleAuthorizationInterceptor();
		userRoleInterceptor.setAuthorizedRoles(new String[] {"role1", "role2"});

		ParameterHandlerMapping interceptingHandlerMapping = new ParameterHandlerMapping();
		interceptingHandlerMapping.setParameterName("interceptingParam");
		ParameterMappingInterceptor parameterMappingInterceptor = new ParameterMappingInterceptor();
		parameterMappingInterceptor.setParameterName("interceptingParam");

		List<HandlerInterceptor> interceptors = new ArrayList<HandlerInterceptor>(4);
		interceptors.add(parameterMappingInterceptor);
		interceptors.add(userRoleInterceptor);
		interceptors.add(new MyHandlerInterceptor1());
		interceptors.add(new MyHandlerInterceptor2());

		MutablePropertyValues pvs = new MutablePropertyValues();
		Map<String, BeanReference> portletModeMap = new ManagedMap<String, BeanReference>();
		portletModeMap.put("view", new RuntimeBeanReference("viewController"));
		portletModeMap.put("edit", new RuntimeBeanReference("editController"));
		pvs.add("portletModeMap", portletModeMap);
		pvs.add("interceptors", interceptors);
		registerSingleton("handlerMapping3", PortletModeHandlerMapping.class, pvs);

		pvs = new MutablePropertyValues();
		Map<String, BeanReference> parameterMap = new ManagedMap<String, BeanReference>();
		parameterMap.put("test1", new RuntimeBeanReference("testController1"));
		parameterMap.put("test2", new RuntimeBeanReference("testController2"));
		parameterMap.put("requestLocaleChecker", new RuntimeBeanReference("requestLocaleCheckingController"));
		parameterMap.put("contextLocaleChecker", new RuntimeBeanReference("localeContextCheckingController"));
		parameterMap.put("exception1", new RuntimeBeanReference("exceptionThrowingHandler1"));
		parameterMap.put("exception2", new RuntimeBeanReference("exceptionThrowingHandler2"));
		parameterMap.put("myPortlet", new RuntimeBeanReference("myPortlet"));
		parameterMap.put("unknown", new RuntimeBeanReference("unknownHandler"));
		pvs.add("parameterMap", parameterMap);
		pvs.add("parameterName", "myParam");
		pvs.add("order", "2");
		registerSingleton("handlerMapping2", ParameterHandlerMapping.class, pvs);

		pvs = new MutablePropertyValues();
		Map<String, Object> innerMap = new ManagedMap<String, Object>();
		innerMap.put("help1", new RuntimeBeanReference("helpController1"));
		innerMap.put("help2", new RuntimeBeanReference("helpController2"));
		Map<String, Object> outerMap = new ManagedMap<String, Object>();
		outerMap.put("help", innerMap);
		pvs.add("portletModeParameterMap", outerMap);
		pvs.add("order", "1");
		registerSingleton("handlerMapping1", PortletModeParameterHandlerMapping.class, pvs);

		pvs = new MutablePropertyValues();
		pvs.add("order", "1");
		pvs.add("exceptionMappings",
			"java.lang.IllegalAccessException=failed-illegalaccess\n" +
			"PortletRequestBindingException=failed-binding\n" +
			"NoHandlerFoundException=failed-unavailable");
		pvs.add("defaultErrorView", "failed-default-1");
		registerSingleton("exceptionResolver", SimpleMappingExceptionResolver.class, pvs);

		pvs = new MutablePropertyValues();
		pvs.add("order", "0");
		pvs.add("exceptionMappings",
				"java.lang.Exception=failed-exception\n" +
				"java.lang.RuntimeException=failed-runtime");
		List<BeanReference> mappedHandlers = new ManagedList<BeanReference>();
		mappedHandlers.add(new RuntimeBeanReference("exceptionThrowingHandler1"));
		pvs.add("mappedHandlers", mappedHandlers);
		pvs.add("defaultErrorView", "failed-default-0");
		registerSingleton("handlerExceptionResolver", SimpleMappingExceptionResolver.class, pvs);

		addMessage("test", Locale.ENGLISH, "test message");
		addMessage("test", Locale.CANADA, "Canadian & test message");
		addMessage("test.args", Locale.ENGLISH, "test {0} and {1}");

		super.refresh();
	}


	public static class TestController1 implements Controller {

		@Override
		public void handleActionRequest(ActionRequest request, ActionResponse response) {
			response.setRenderParameter("result", "test1-action");
		}

		@Override
		public ModelAndView handleRenderRequest(RenderRequest request, RenderResponse response) throws Exception {
			return null;
		}
	}


	public static class TestController2 implements Controller {

		@Override
		public void handleActionRequest(ActionRequest request, ActionResponse response) {}

		@Override
		public ModelAndView handleRenderRequest(RenderRequest request, RenderResponse response) throws Exception {
			response.setProperty("result", "test2-view");
			return null;
		}
	}


	public static class ViewController implements Controller {

		@Override
		public void handleActionRequest(ActionRequest request, ActionResponse response) {}

		@Override
		public ModelAndView handleRenderRequest(RenderRequest request, RenderResponse response) throws Exception {
			return new ModelAndView("someViewName", "result", "view was here");
		}
	}


	public static class EditController implements Controller {

		@Override
		public void handleActionRequest(ActionRequest request, ActionResponse response) {
			response.setRenderParameter("param", "edit was here");
		}

		@Override
		public ModelAndView handleRenderRequest(RenderRequest request, RenderResponse response) throws Exception {
			return new ModelAndView(request.getParameter("param"));
		}
	}


	public static class HelpController1 implements Controller {

		@Override
		public void handleActionRequest(ActionRequest request, ActionResponse response) {
			response.setRenderParameter("param", "help1 was here");
		}

		@Override
		public ModelAndView handleRenderRequest(RenderRequest request, RenderResponse response) throws Exception {
			return new ModelAndView("help1-view");
		}
	}


	public static class HelpController2 implements Controller {

		@Override
		public void handleActionRequest(ActionRequest request, ActionResponse response) {
			response.setRenderParameter("param", "help2 was here");
		}

		@Override
		public ModelAndView handleRenderRequest(RenderRequest request, RenderResponse response) throws Exception {
			return new ModelAndView("help2-view");
		}
	}

	public static class RequestLocaleCheckingController implements Controller {

		@Override
		public void handleActionRequest(ActionRequest request, ActionResponse response) throws PortletException {
			if (!Locale.CANADA.equals(request.getLocale())) {
				throw new PortletException("Incorrect Locale in ActionRequest");
			}
		}

		@Override
		public ModelAndView handleRenderRequest(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
			if (!Locale.CANADA.equals(request.getLocale())) {
				throw new PortletException("Incorrect Locale in RenderRequest");
			}
			response.getWriter().write("locale-ok");
			return null;
		}
	}


	public static class LocaleContextCheckingController implements Controller {

		@Override
		public void handleActionRequest(ActionRequest request, ActionResponse response) throws PortletException {
			if (!Locale.CANADA.equals(LocaleContextHolder.getLocale())) {
				throw new PortletException("Incorrect Locale in LocaleContextHolder");
			}
		}

		@Override
		public ModelAndView handleRenderRequest(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
			if (!Locale.CANADA.equals(LocaleContextHolder.getLocale())) {
				throw new PortletException("Incorrect Locale in LocaleContextHolder");
			}
			response.getWriter().write("locale-ok");
			return null;
		}
	}


	public static class MyPortlet implements Portlet {

		private PortletConfig portletConfig;

		@Override
		public void init(PortletConfig portletConfig) throws PortletException {
			this.portletConfig = portletConfig;
		}

		@Override
		public void processAction(ActionRequest request, ActionResponse response) throws PortletException {
			response.setRenderParameter("result", "myPortlet action called");
		}

		@Override
		public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
			response.getWriter().write("myPortlet was here");
		}

		public PortletConfig getPortletConfig() {
			return this.portletConfig;
		}

		@Override
		public void destroy() {
			this.portletConfig = null;
		}
	}


	public static interface MyHandler {

		public void doSomething(PortletRequest request) throws Exception;
	}


	public static class ExceptionThrowingHandler implements MyHandler {

		@Override
		public void doSomething(PortletRequest request) throws Exception {
			if (request.getParameter("fail") != null) {
				throw new ModelAndViewDefiningException(new ModelAndView("failed-modelandview"));
			}
			if (request.getParameter("access") != null) {
				throw new IllegalAccessException("portlet-illegalaccess");
			}
			if (request.getParameter("binding") != null) {
				throw new PortletRequestBindingException("portlet-binding");
			}
			if (request.getParameter("generic") != null) {
				throw new Exception("portlet-generic");
			}
			if (request.getParameter("runtime") != null) {
				throw new RuntimeException("portlet-runtime");
			}
			throw new IllegalArgumentException("illegal argument");
		}
	}


	public static class MyHandlerAdapter implements HandlerAdapter, Ordered {

		@Override
		public int getOrder() {
			return 99;
		}

		@Override
		public boolean supports(Object handler) {
			return handler != null && MyHandler.class.isAssignableFrom(handler.getClass());
		}

		@Override
		public void handleAction(ActionRequest request, ActionResponse response, Object delegate) throws Exception {
			((MyHandler) delegate).doSomething(request);
		}

		@Override
		public ModelAndView handleRender(RenderRequest request, RenderResponse response, Object delegate) throws Exception {
			((MyHandler) delegate).doSomething(request);
			return null;
		}

		@Override
		public ModelAndView handleResource(ResourceRequest request, ResourceResponse response, Object handler)
				throws Exception {
			return null;
		}

		@Override
		public void handleEvent(EventRequest request, EventResponse response, Object handler) throws Exception {
		}
	}


	public static class MyHandlerInterceptor1 extends HandlerInterceptorAdapter {

		@Override
		public boolean preHandleRender(RenderRequest request, RenderResponse response, Object handler)
			throws PortletException {
			if (request.getAttribute("test2-remove-never") != null) {
				throw new PortletException("Wrong interceptor order");
			}
			request.setAttribute("test1-remove-never", "test1-remove-never");
			request.setAttribute("test1-remove-post", "test1-remove-post");
			request.setAttribute("test1-remove-after", "test1-remove-after");
			return true;
		}

		@Override
		public void postHandleRender(
				RenderRequest request, RenderResponse response, Object handler, ModelAndView modelAndView)
				throws PortletException {
			if (request.getAttribute("test2-remove-post") != null) {
				throw new PortletException("Wrong interceptor order");
			}
			if (!"test1-remove-post".equals(request.getAttribute("test1-remove-post"))) {
				throw new PortletException("Incorrect request attribute");
			}
			request.removeAttribute("test1-remove-post");
		}

		@Override
		public void afterRenderCompletion(
				RenderRequest request, RenderResponse response, Object handler, Exception ex)
				throws PortletException {
			if (request.getAttribute("test2-remove-after") != null) {
				throw new PortletException("Wrong interceptor order");
			}
			request.removeAttribute("test1-remove-after");
		}
	}


	public static class MyHandlerInterceptor2 extends HandlerInterceptorAdapter {

		@Override
		public boolean preHandleRender(RenderRequest request, RenderResponse response, Object handler)
			throws PortletException {
			if (request.getAttribute("test1-remove-post") == null) {
				throw new PortletException("Wrong interceptor order");
			}
			if ("true".equals(request.getParameter("abort"))) {
				return false;
			}
			request.setAttribute("test2-remove-never", "test2-remove-never");
			request.setAttribute("test2-remove-post", "test2-remove-post");
			request.setAttribute("test2-remove-after", "test2-remove-after");
			return true;
		}

		@Override
		public void postHandleRender(
				RenderRequest request, RenderResponse response, Object handler, ModelAndView modelAndView)
				throws PortletException {
			if ("true".equals(request.getParameter("noView"))) {
				modelAndView.clear();
			}
			if (request.getAttribute("test1-remove-post") == null) {
				throw new PortletException("Wrong interceptor order");
			}
			if (!"test2-remove-post".equals(request.getAttribute("test2-remove-post"))) {
				throw new PortletException("Incorrect request attribute");
			}
			request.removeAttribute("test2-remove-post");
		}

		@Override
		public void afterRenderCompletion(
				RenderRequest request, RenderResponse response, Object handler, Exception ex)
				throws Exception {
			if (request.getAttribute("test1-remove-after") == null) {
				throw new PortletException("Wrong interceptor order");
			}
			request.removeAttribute("test2-remove-after");
		}
	}


	public static class MultipartCheckingHandler implements MyHandler {

		@Override
		public void doSomething(PortletRequest request) throws PortletException, IllegalAccessException {
			if (!(request instanceof MultipartActionRequest)) {
				throw new PortletException("Not in a MultipartActionRequest");
			}
		}
	}


	public static class MockMultipartResolver implements PortletMultipartResolver {

		@Override
		public boolean isMultipart(ActionRequest request) {
			return true;
		}

		@Override
		public boolean isMultipart(ResourceRequest request) {
			return true;
		}

		@Override
		public MultipartActionRequest resolveMultipart(ActionRequest request) throws MultipartException {
			if (request.getAttribute("fail") != null) {
				throw new MaxUploadSizeExceededException(1000);
			}
			if (request instanceof MultipartActionRequest) {
				throw new IllegalStateException("Already a multipart request");
			}
			if (request.getAttribute("resolved") != null) {
				throw new IllegalStateException("Already resolved");
			}
			request.setAttribute("resolved", Boolean.TRUE);
			MultiValueMap<String, MultipartFile> files = new LinkedMultiValueMap<String, MultipartFile>();
			files.set("someFile", new MockMultipartFile("someFile", "someContent".getBytes()));
			Map<String, String[]> params = new HashMap<String, String[]>();
			params.put("someParam", new String[] {"someParam"});
			return new DefaultMultipartActionRequest(request, files, params, Collections.<String, String>emptyMap());
		}

		@Override
		public MultipartResourceRequest resolveMultipart(ResourceRequest request) throws MultipartException {
			if (request.getAttribute("fail") != null) {
				throw new MaxUploadSizeExceededException(1000);
			}
			if (request instanceof MultipartActionRequest) {
				throw new IllegalStateException("Already a multipart request");
			}
			if (request.getAttribute("resolved") != null) {
				throw new IllegalStateException("Already resolved");
			}
			request.setAttribute("resolved", Boolean.TRUE);
			MultiValueMap<String, MultipartFile> files = new LinkedMultiValueMap<String, MultipartFile>();
			files.set("someFile", new MockMultipartFile("someFile", "someContent".getBytes()));
			Map<String, String[]> params = new HashMap<String, String[]>();
			params.put("someParam", new String[] {"someParam"});
			return new DefaultMultipartResourceRequest(request, files, params, Collections.<String, String>emptyMap());
		}

		@Override
		public void cleanupMultipart(MultipartActionRequest request) {
			if (request.getAttribute("cleanedUp") != null) {
				throw new IllegalStateException("Already cleaned up");
			}
			request.setAttribute("cleanedUp", Boolean.TRUE);
		}

		@Override
		public void cleanupMultipart(MultipartResourceRequest request) {
			if (request.getAttribute("cleanedUp") != null) {
				throw new IllegalStateException("Already cleaned up");
			}
			request.setAttribute("cleanedUp", Boolean.TRUE);
		}
	}


	public static class TestApplicationListener implements ApplicationListener<ApplicationEvent> {

		public int counter = 0;

		@Override
		public void onApplicationEvent(ApplicationEvent event) {
			if (event instanceof PortletRequestHandledEvent) {
				this.counter++;
			}
		}
	}

}
