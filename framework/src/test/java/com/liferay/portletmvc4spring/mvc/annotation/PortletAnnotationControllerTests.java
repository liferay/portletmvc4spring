/**
 * Copyright (c) 2000-2020 the original author or authors.
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
package com.liferay.portletmvc4spring.mvc.annotation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import static org.junit.Assert.*;

import org.junit.Test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.propertyeditors.CustomDateEditor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;

import org.springframework.core.MethodParameter;

import org.springframework.lang.Nullable;

import org.springframework.stereotype.Controller;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.liferay.portletmvc4spring.DispatcherPortlet;
import com.liferay.portletmvc4spring.ModelAndView;
import com.liferay.portletmvc4spring.NoHandlerFoundException;
import com.liferay.portletmvc4spring.context.StaticPortletApplicationContext;
import com.liferay.portletmvc4spring.mvc.AbstractController;
import com.liferay.portletmvc4spring.mvc.method.annotation.PortletRequestMappingHandlerAdapter;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockActionRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockActionResponse;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortletConfig;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortletContext;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockRenderRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockRenderResponse;

import com.liferay.spring.tests.sample.beans.DerivedTestBean;
import com.liferay.spring.tests.sample.beans.ITestBean;
import com.liferay.spring.tests.sample.beans.TestBean;


/**
 * @author  Juergen Hoeller
 * @author  Neil Griffin
 * @since   2.5
 */
public class PortletAnnotationControllerTests {

	@Test
	public void adaptedHandleMethods() throws Exception {
		doTestAdaptedHandleMethods(MyAdaptedController.class);
	}

	@Test
	public void adaptedHandleMethods2() throws Exception {
		doTestAdaptedHandleMethods(MyAdaptedController2.class);
	}

	@Test
	public void adaptedHandleMethods3() throws Exception {
		doTestAdaptedHandleMethods(MyAdaptedController3.class);
	}

	@Test
	public void binderInitializingCommandProvidingFormController() throws Exception {
		DispatcherPortlet portlet = new DispatcherPortlet() {
				@Override
				protected ApplicationContext createPortletApplicationContext(ApplicationContext parent)
					throws BeansException {
					GenericWebApplicationContext wac = new GenericWebApplicationContext();
					wac.registerBeanDefinition("controller",
						new RootBeanDefinition(MyBinderInitializingCommandProvidingFormController.class));
					wac.refresh();

					return wac;
				}

				@Override
				protected void render(ModelAndView mv, PortletRequest request, MimeResponse response) throws Exception {
					new TestView().render(mv.getViewName(), mv.getModel(), request, response);
				}
			};
		portlet.init(new MockPortletConfig());

		MockRenderRequest request = new MockRenderRequest(PortletMode.VIEW);
		request.addParameter("defaultName", "myDefaultName");
		request.addParameter("age", "value2");
		request.addParameter("date", "2007-10-02");

		MockRenderResponse response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myView-String:myDefaultName-typeMismatch-tb1-myOriginalValue", response.getContentAsString());
	}

	@Test
	public void commandProvidingFormController() throws Exception {
		DispatcherPortlet portlet = new DispatcherPortlet() {
				@Override
				protected ApplicationContext createPortletApplicationContext(ApplicationContext parent)
					throws BeansException {
					GenericWebApplicationContext wac = new GenericWebApplicationContext();
					wac.registerBeanDefinition("controller",
						new RootBeanDefinition(MyCommandProvidingFormController.class));

					RootBeanDefinition adapterDef = new RootBeanDefinition(PortletRequestMappingHandlerAdapter.class);
					adapterDef.getPropertyValues().add("webBindingInitializer", new MyWebBindingInitializer());
					wac.registerBeanDefinition("handlerAdapter", adapterDef);
					wac.refresh();

					return wac;
				}

				@Override
				protected void render(ModelAndView mv, PortletRequest request, MimeResponse response) throws Exception {
					new TestView().render(mv.getViewName(), mv.getModel(), request, response);
				}
			};
		portlet.init(new MockPortletConfig());

		MockRenderRequest request = new MockRenderRequest(PortletMode.VIEW);
		request.addParameter("defaultName", "myDefaultName");
		request.addParameter("age", "value2");
		request.addParameter("date", "2007-10-02");

		MockRenderResponse response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myView-String:myDefaultName-typeMismatch-tb1-myOriginalValue", response.getContentAsString());
	}

	public void doTestAdaptedHandleMethods(final Class<?> controllerClass) throws Exception {
		DispatcherPortlet portlet = new DispatcherPortlet() {
				@Override
				protected ApplicationContext createPortletApplicationContext(ApplicationContext parent)
					throws BeansException {
					GenericWebApplicationContext wac = new GenericWebApplicationContext();
					wac.registerBeanDefinition("controller", new RootBeanDefinition(controllerClass));
					wac.refresh();

					return wac;
				}
			};
		portlet.init(new MockPortletConfig());

		MockActionRequest actionRequest = new MockActionRequest(PortletMode.VIEW);
		MockActionResponse actionResponse = new MockActionResponse();
		portlet.processAction(actionRequest, actionResponse);
		assertEquals("value", actionResponse.getRenderParameter("test"));

		MockRenderRequest request = new MockRenderRequest(PortletMode.EDIT);
		request.addParameter("param1", "value1");
		request.addParameter("param2", "2");

		MockRenderResponse response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("test-value1-2", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.HELP);
		request.addParameter("name", "name1");
		request.addParameter("age", "2");
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("test-name1-2", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.VIEW);
		request.addParameter("name", "name1");
		request.addParameter("age", "value2");
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("test-name1-typeMismatch", response.getContentAsString());
	}

	@Test
	public void formController() throws Exception {
		DispatcherPortlet portlet = new DispatcherPortlet() {
				@Override
				protected ApplicationContext createPortletApplicationContext(ApplicationContext parent)
					throws BeansException {
					GenericWebApplicationContext wac = new GenericWebApplicationContext();
					wac.registerBeanDefinition("controller", new RootBeanDefinition(MyFormController.class));
					wac.refresh();

					return wac;
				}

				@Override
				protected void render(ModelAndView mv, PortletRequest request, MimeResponse response) throws Exception {
					new TestView().render(mv.getViewName(), mv.getModel(), request, response);
				}
			};
		portlet.init(new MockPortletConfig());

		MockRenderRequest request = new MockRenderRequest(PortletMode.VIEW);
		request.addParameter("name", "name1");
		request.addParameter("age", "value2");

		MockRenderResponse response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myView-name1-typeMismatch-tb1-myValue", response.getContentAsString());
	}

	@Test
	public void modelFormController() throws Exception {
		DispatcherPortlet portlet = new DispatcherPortlet() {
				@Override
				protected ApplicationContext createPortletApplicationContext(ApplicationContext parent)
					throws BeansException {
					GenericWebApplicationContext wac = new GenericWebApplicationContext();
					wac.registerBeanDefinition("controller", new RootBeanDefinition(MyModelFormController.class));
					wac.refresh();

					return wac;
				}

				@Override
				protected void render(ModelAndView mv, PortletRequest request, MimeResponse response) throws Exception {
					new TestView().render(mv.getViewName(), mv.getModel(), request, response);
				}
			};
		portlet.init(new MockPortletConfig());

		MockRenderRequest request = new MockRenderRequest(PortletMode.VIEW);
		request.addParameter("name", "name1");
		request.addParameter("age", "value2");

		MockRenderResponse response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myView-name1-typeMismatch-tb1-myValue", response.getContentAsString());
	}

	@Test
	public void parameterDispatchingController() throws Exception {
		DispatcherPortlet portlet = new DispatcherPortlet() {
				@Override
				protected ApplicationContext createPortletApplicationContext(ApplicationContext parent)
					throws BeansException {
					StaticPortletApplicationContext wac = new StaticPortletApplicationContext();
					wac.setPortletContext(new MockPortletContext());

					RootBeanDefinition bd = new RootBeanDefinition(MyParameterDispatchingController.class);
					bd.setScope(WebApplicationContext.SCOPE_REQUEST);
					wac.registerBeanDefinition("controller", bd);
					AnnotationConfigUtils.registerAnnotationConfigProcessors(wac);
					wac.refresh();

					return wac;
				}
			};
		portlet.init(new MockPortletConfig());

		MockRenderRequest request = new MockRenderRequest(PortletMode.VIEW);
		MockRenderResponse response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myView", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.VIEW);
		request.addParameter("view", "other");
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myOtherView", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.VIEW);
		request.addParameter("view", "my");
		request.addParameter("lang", "de");
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myLangView", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.VIEW);
		request.addParameter("surprise", "!");
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("mySurpriseView", response.getContentAsString());
	}

	@Test
	public void specificBinderInitializingCommandProvidingFormController() throws Exception {
		DispatcherPortlet portlet = new DispatcherPortlet() {
				@Override
				protected ApplicationContext createPortletApplicationContext(ApplicationContext parent)
					throws BeansException {
					StaticPortletApplicationContext wac = new StaticPortletApplicationContext();
					wac.registerBeanDefinition("controller",
						new RootBeanDefinition(MySpecificBinderInitializingCommandProvidingFormController.class));
					wac.refresh();

					return wac;
				}

				@Override
				protected void render(ModelAndView mv, PortletRequest request, MimeResponse response) throws Exception {
					new TestView().render(mv.getViewName(), mv.getModel(), request, response);
				}
			};
		portlet.init(new MockPortletConfig());

		MockRenderRequest request = new MockRenderRequest(PortletMode.VIEW);
		request.addParameter("defaultName", "myDefaultName");
		request.addParameter("age", "value2");
		request.addParameter("date", "2007-10-02");

		MockRenderResponse response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myView-String:myDefaultName-typeMismatch-tb1-myOriginalValue", response.getContentAsString());
	}

	@Test
	public void standardHandleMethod() throws Exception {
		DispatcherPortlet portlet = new DispatcherPortlet() {
				@Override
				protected ApplicationContext createPortletApplicationContext(ApplicationContext parent)
					throws BeansException {
					GenericWebApplicationContext wac = new GenericWebApplicationContext();
					wac.registerBeanDefinition("controller", new RootBeanDefinition(MyController.class));
					wac.refresh();

					return wac;
				}
			};
		portlet.init(new MockPortletConfig());

		MockRenderRequest request = new MockRenderRequest(PortletMode.VIEW);
		MockRenderResponse response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("test", response.getContentAsString());
	}

	@Test
	public void typedCommandProvidingFormController() throws Exception {
		DispatcherPortlet portlet = new DispatcherPortlet() {
				@Override
				protected ApplicationContext createPortletApplicationContext(ApplicationContext parent)
					throws BeansException {
					GenericWebApplicationContext wac = new GenericWebApplicationContext();
					wac.registerBeanDefinition("controller",
						new RootBeanDefinition(MyTypedCommandProvidingFormController.class));
					wac.registerBeanDefinition("controller2",
						new RootBeanDefinition(MyOtherTypedCommandProvidingFormController.class));

					RootBeanDefinition adapterDef = new RootBeanDefinition(PortletRequestMappingHandlerAdapter.class);
					adapterDef.getPropertyValues().add("webBindingInitializer", new MyWebBindingInitializer());

					List<HandlerMethodArgumentResolver> customArgumentResolvers = new ArrayList<>();
					customArgumentResolvers.add(new MySpecialArgumentResolver());
					adapterDef.getPropertyValues().add("customArgumentResolvers", customArgumentResolvers);
					wac.registerBeanDefinition("handlerAdapter", adapterDef);
					wac.refresh();

					return wac;
				}

				@Override
				protected void render(ModelAndView mv, PortletRequest request, MimeResponse response) throws Exception {
					new TestView().render(mv.getViewName(), mv.getModel(), request, response);
				}
			};
		portlet.init(new MockPortletConfig());

		MockRenderRequest request = new MockRenderRequest(PortletMode.VIEW);
		request.addParameter("myParam", "myValue");
		request.addParameter("defaultName", "10");
		request.addParameter("age", "value2");
		request.addParameter("date", "2007-10-02");

		MockRenderResponse response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myView-Integer:10-typeMismatch-tb1-myOriginalValue", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.VIEW);
		request.addParameter("myParam", "myOtherValue");
		request.addParameter("defaultName", "10");
		request.addParameter("age", "value2");
		request.addParameter("date", "2007-10-02");
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myOtherView-Integer:10-typeMismatch-tb1-myOriginalValue", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.EDIT);
		request.addParameter("myParam", "myValue");
		request.addParameter("defaultName", "10");
		request.addParameter("age", "value2");
		request.addParameter("date", "2007-10-02");
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myView-myName-typeMismatch-tb1-myOriginalValue", response.getContentAsString());
	}

	@Test
	public void typeLevelParameterDispatchingController() throws Exception {
		DispatcherPortlet portlet = new DispatcherPortlet() {
				@Override
				protected ApplicationContext createPortletApplicationContext(ApplicationContext parent)
					throws BeansException {
					StaticPortletApplicationContext wac = new StaticPortletApplicationContext();
					wac.setPortletContext(new MockPortletContext());

					RootBeanDefinition bd = new RootBeanDefinition(MyTypeLevelParameterDispatchingController.class);
					bd.setScope(WebApplicationContext.SCOPE_REQUEST);
					wac.registerBeanDefinition("controller", bd);

					RootBeanDefinition bd2 = new RootBeanDefinition(MySpecialParameterDispatchingController.class);
					bd2.setScope(WebApplicationContext.SCOPE_REQUEST);
					wac.registerBeanDefinition("controller2", bd2);

					RootBeanDefinition bd3 = new RootBeanDefinition(MyOtherSpecialParameterDispatchingController.class);
					bd3.setScope(WebApplicationContext.SCOPE_REQUEST);
					wac.registerBeanDefinition("controller3", bd3);

					RootBeanDefinition bd4 = new RootBeanDefinition(MyParameterDispatchingController.class);
					bd4.setScope(WebApplicationContext.SCOPE_REQUEST);
					wac.registerBeanDefinition("controller4", bd4);
					AnnotationConfigUtils.registerAnnotationConfigProcessors(wac);
					wac.refresh();

					return wac;
				}
			};
		portlet.init(new MockPortletConfig());

		MockRenderRequest request = new MockRenderRequest(PortletMode.HELP);
		MockRenderResponse response = new MockRenderResponse();

		try {
			portlet.render(request, response);
			fail("Should have thrown NoHandlerFoundException");
		}
		catch (NoHandlerFoundException ex) {
			// expected
		}

		request = new MockRenderRequest(PortletMode.EDIT);
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myDefaultView", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.EDIT);
		request.addParameter("myParam", "myValue");
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myView", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.EDIT);
		request.addParameter("myParam", "mySpecialValue");
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("mySpecialView", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.EDIT);
		request.addParameter("myParam", "myOtherSpecialValue");
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myOtherSpecialView", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.VIEW);
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myView", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.EDIT);
		request.addParameter("myParam", "myValue");
		request.addParameter("view", "other");
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myOtherView", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.EDIT);
		request.addParameter("myParam", "myValue");
		request.addParameter("view", "my");
		request.addParameter("lang", "de");
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("myLangView", response.getContentAsString());

		request = new MockRenderRequest(PortletMode.EDIT);
		request.addParameter("myParam", "myValue");
		request.addParameter("surprise", "!");
		response = new MockRenderResponse();
		portlet.render(request, response);
		assertEquals("mySurpriseView", response.getContentAsString());
	}

	@Controller
	public static class ModelAndViewResolverController {

		@RequestMapping("VIEW")
		public MySpecialArg handle() {
			return new MySpecialArg("foo");
		}
	}

	@Controller
	private static class MyAdaptedController {

		@RequestMapping("VIEW")
		public void myHandle(ActionRequest request, ActionResponse response) throws IOException {
			response.setRenderParameter("test", "value");
		}

		@RequestMapping("HELP")
		public void myHandle(TestBean tb, RenderResponse response) throws IOException {
			response.getWriter().write("test-" + tb.getName() + "-" + tb.getAge());
		}

		@RequestMapping("EDIT")
		public void myHandle(@RequestParam("param1") String p1,
			@RequestParam("param2") int p2, RenderResponse response) throws IOException {
			response.getWriter().write("test-" + p1 + "-" + p2);
		}

		@RequestMapping("VIEW")
		public void myHandle(TestBean tb, Errors errors, RenderResponse response) throws IOException {
			response.getWriter().write("test-" + tb.getName() + "-" + errors.getFieldError("age").getCode());
		}
	}

	@Controller
	private static class MyAdaptedController2 {

		@RequestMapping("VIEW")
		public void myHandle(ActionRequest request, ActionResponse response) throws IOException {
			response.setRenderParameter("test", "value");
		}

		@RequestMapping("HELP")
		public void myHandle(TestBean tb, RenderResponse response) throws IOException {
			response.getWriter().write("test-" + tb.getName() + "-" + tb.getAge());
		}

		@RequestMapping("EDIT")
		public void myHandle(@RequestParam("param1") String p1, int param2, RenderResponse response)
			throws IOException {
			response.getWriter().write("test-" + p1 + "-" + param2);
		}

		@RequestMapping("VIEW")
		public void myHandle(TestBean tb, Errors errors, RenderResponse response) throws IOException {
			response.getWriter().write("test-" + tb.getName() + "-" + errors.getFieldError("age").getCode());
		}
	}

	@Controller
	@RequestMapping({ "VIEW", "EDIT", "HELP" })
	private static class MyAdaptedController3 {

		@RequestMapping
		public void myHandle(ActionRequest request, ActionResponse response) {
			response.setRenderParameter("test", "value");
		}

		@RequestMapping("HELP")
		public void myHandle(TestBean tb, RenderResponse response) throws IOException {
			response.getWriter().write("test-" + tb.getName() + "-" + tb.getAge());
		}

		@RequestMapping("EDIT")
		public void myHandle(@RequestParam("param1") String p1,
			@RequestParam("param2") int p2, RenderResponse response) throws IOException {
			response.getWriter().write("test-" + p1 + "-" + p2);
		}

		@RequestMapping
		public void myHandle(TestBean tb, Errors errors, RenderResponse response) throws IOException {
			response.getWriter().write("test-" + tb.getName() + "-" + errors.getFieldError("age").getCode());
		}
	}

	@Controller
	@SuppressWarnings("rawtypes")
	private static class MyBinderInitializingCommandProvidingFormController extends MyCommandProvidingFormController {

		@InitBinder
		private void initBinder(WebDataBinder binder) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateFormat.setLenient(false);
			binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
		}
	}

	@Controller
	private static class MyCommandProvidingFormController<T, TB, TB2> extends MyFormController {

		@Override
		@RequestMapping("VIEW")
		public String myHandle(@ModelAttribute("myCommand") TestBean tb, BindingResult errors, ModelMap model) {

			if (!model.containsKey("myKey")) {
				model.addAttribute("myKey", "myValue");
			}

			return "myView";
		}

		@RequestMapping("EDIT")
		public String myOtherHandle(TB tb, BindingResult errors, ExtendedModelMap model, MySpecialArg arg) {
			TestBean tbReal = (TestBean) tb;
			tbReal.setName("myName");
			assertTrue(model.get("ITestBean") instanceof DerivedTestBean);
			assertNotNull(arg);

			return super.myHandle(tbReal, errors, model);
		}

		@ModelAttribute
		@SuppressWarnings("unchecked")
		protected TB2 getModelAttr() {
			return (TB2) new DerivedTestBean();
		}

		@ModelAttribute("myCommand")
		private TestBean createTestBean(@RequestParam T defaultName, Map<String, Object> model,
			@RequestParam Date date) {
			model.put("myKey", "myOriginalValue");

			return new TestBean(defaultName.getClass().getSimpleName() + ":" + defaultName.toString());
		}
	}

	@RequestMapping("VIEW")
	private static class MyController extends AbstractController {

		@Override
		protected ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response)
			throws Exception {
			response.getWriter().write("test");

			return null;
		}
	}

	@Controller
	private static class MyFormController {

		@ModelAttribute("testBeanList")
		public List<TestBean> getTestBeans() {
			List<TestBean> list = new LinkedList<TestBean>();
			list.add(new TestBean("tb1"));
			list.add(new TestBean("tb2"));

			return list;
		}

		@RequestMapping("VIEW")
		public String myHandle(@ModelAttribute("myCommand") TestBean tb, BindingResult errors, ModelMap model) {

			if (!model.containsKey("myKey")) {
				model.addAttribute("myKey", "myValue");
			}

			return "myView";
		}
	}

	@Controller
	private static class MyModelFormController {

		@ModelAttribute
		public List<TestBean> getTestBeans() {
			List<TestBean> list = new LinkedList<TestBean>();
			list.add(new TestBean("tb1"));
			list.add(new TestBean("tb2"));

			return list;
		}

		@RequestMapping("VIEW")
		public String myHandle(@ModelAttribute("myCommand") TestBean tb, BindingResult errors, Model model) {

			if (!model.containsAttribute("myKey")) {
				model.addAttribute("myKey", "myValue");
			}

			return "myView";
		}
	}

	@Controller
	@RequestMapping("EDIT")
	private static class MyOtherSpecialParameterDispatchingController {

		@RequestMapping(params = "myParam=myOtherSpecialValue")
		public void myHandle(RenderResponse response) throws IOException {
			response.getWriter().write("myOtherSpecialView");
		}
	}

	@Controller
	@RequestMapping(params = "myParam=myOtherValue")
	private static class MyOtherTypedCommandProvidingFormController
		extends MyCommandProvidingFormController<Integer, TestBean, ITestBean> {

		@Override
		@RequestMapping("VIEW")
		public String myHandle(@ModelAttribute("myCommand") TestBean tb, BindingResult errors, ModelMap model) {

			if (!model.containsKey("myKey")) {
				model.addAttribute("myKey", "myValue");
			}

			return "myOtherView";
		}
	}

	@Controller
	@RequestMapping("VIEW")
	private static class MyParameterDispatchingController {

		@Autowired
		private PortletContext portletContext;

		@Autowired
		private PortletSession session;

		@Autowired
		private PortletRequest request;

		@RequestMapping
		public void myHandle(RenderResponse response) throws IOException {

			if ((this.portletContext == null) || (this.session == null) || (this.request == null)) {
				throw new IllegalStateException();
			}

			response.getWriter().write("myView");
		}

		@RequestMapping(params = { "view=my", "lang=de" })
		public void myLangHandle(RenderResponse response) throws IOException {
			response.getWriter().write("myLangView");
		}

		@RequestMapping(params = { "view", "!lang" })
		public void myOtherHandle(RenderResponse response) throws IOException {
			response.getWriter().write("myOtherView");
		}

		@RequestMapping(params = "surprise")
		public void mySurpriseHandle(RenderResponse response) throws IOException {
			response.getWriter().write("mySurpriseView");
		}
	}

	private static class MySpecialArg {

		public MySpecialArg(String value) {
		}
	}

	private static class MySpecialArgumentResolver implements HandlerMethodArgumentResolver {

		@Nullable
		@Override
		public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {

			return new MySpecialArg("myValue");
		}

		@Override
		public boolean supportsParameter(MethodParameter methodParameter) {
			return MySpecialArg.class.isAssignableFrom(methodParameter.getParameterType());
		}
	}

	@Controller
	@RequestMapping("EDIT")
	private static class MySpecialParameterDispatchingController {

		@RequestMapping
		public void myDefaultHandle(RenderResponse response) throws IOException {
			response.getWriter().write("myDefaultView");
		}

		@RequestMapping(params = "myParam=mySpecialValue")
		public void myHandle(RenderResponse response) throws IOException {
			response.getWriter().write("mySpecialView");
		}
	}

	@Controller
	@SuppressWarnings("rawtypes")
	private static class MySpecificBinderInitializingCommandProvidingFormController
		extends MyCommandProvidingFormController {

		@InitBinder({ "myCommand", "date" })
		private void initBinder(WebDataBinder binder) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateFormat.setLenient(false);
			binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
		}
	}

	@Controller
	@RequestMapping(params = "myParam=myValue")
	private static class MyTypedCommandProvidingFormController
		extends MyCommandProvidingFormController<Integer, TestBean, ITestBean> {

	}

	@Controller
	@RequestMapping(value = "EDIT", params = "myParam=myValue")
	private static class MyTypeLevelParameterDispatchingController extends MyParameterDispatchingController {

	}

	private static class MyWebBindingInitializer implements WebBindingInitializer {

		@Override
		public void initBinder(WebDataBinder binder) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateFormat.setLenient(false);
			binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
		}
	}

	private static class TestView {

		@SuppressWarnings("deprecation")
		public void render(String viewName, Map<String, Object> model, PortletRequest request, MimeResponse response)
			throws Exception {
			TestBean tb = (TestBean) model.get("testBean");

			if (tb == null) {
				tb = (TestBean) model.get("myCommand");
			}

			if (tb.getName().endsWith("myDefaultName")) {
				assertEquals(107, tb.getDate().getYear());
			}

			Errors errors = (Errors) model.get(BindingResult.MODEL_KEY_PREFIX + "testBean");

			if (errors == null) {
				errors = (Errors) model.get(BindingResult.MODEL_KEY_PREFIX + "myCommand");
			}

			if (errors.hasFieldErrors("date")) {
				throw new IllegalStateException();
			}

			List<?> testBeans = (List<?>) model.get("testBeanList");
			response.getWriter().write(viewName + "-" + tb.getName() + "-" + errors.getFieldError("age").getCode() +
				"-" + ((TestBean) testBeans.get(0)).getName() + "-" + model.get("myKey"));
		}
	}

}
