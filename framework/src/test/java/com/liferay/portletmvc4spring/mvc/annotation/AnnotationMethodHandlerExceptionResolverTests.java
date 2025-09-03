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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.net.SocketException;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import org.springframework.stereotype.Controller;

import org.springframework.util.ClassUtils;

import org.springframework.web.bind.annotation.ExceptionHandler;

import com.liferay.portletmvc4spring.ModelAndView;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockRenderRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.MockRenderResponse;


/**
 * @author  Arjen Poutsma
 * @author  Juergen Hoeller
 */
public class AnnotationMethodHandlerExceptionResolverTests {

	private AnnotationMethodHandlerExceptionResolver exceptionResolver;

	private MockRenderRequest request;

	private MockRenderResponse response;

	@Test(expected = IllegalStateException.class)
	public void ambiguous() {
		IllegalArgumentException ex = new IllegalArgumentException();
		AmbiguousController controller = new AmbiguousController();
		exceptionResolver.resolveException(request, response, controller, ex);
	}

	// SPR-9209

	@Test
	public void cachingSideEffect() {
		IllegalArgumentException ex = new IllegalArgumentException();
		SimpleController controller = new SimpleController();

		ModelAndView mav = exceptionResolver.resolveException(request, response, controller, ex);
		assertNotNull("No ModelAndView returned", mav);

		mav = exceptionResolver.resolveException(request, response, controller, new NullPointerException());
		assertNull(mav);
	}

	@Test
	public void inherited() {
		IOException ex = new IOException();
		InheritedController controller = new InheritedController();
		ModelAndView mav = exceptionResolver.resolveException(request, response, controller, ex);
		assertNotNull("No ModelAndView returned", mav);
		assertEquals("Invalid view name returned", "GenericError", mav.getViewName());
	}

	@Before
	public void setUp() {
		exceptionResolver = new AnnotationMethodHandlerExceptionResolver();
		request = new MockRenderRequest();
		response = new MockRenderResponse();
	}

	@Test
	public void simpleWithBindException() {
		BindException ex = new BindException();
		SimpleController controller = new SimpleController();
		ModelAndView mav = exceptionResolver.resolveException(request, response, controller, ex);
		assertNotNull("No ModelAndView returned", mav);
		assertEquals("Invalid view name returned", "Y:BindException", mav.getViewName());
	}

	@Test
	public void simpleWithFileNotFoundException() {
		FileNotFoundException ex = new FileNotFoundException();
		SimpleController controller = new SimpleController();
		ModelAndView mav = exceptionResolver.resolveException(request, response, controller, ex);
		assertNotNull("No ModelAndView returned", mav);
		assertEquals("Invalid view name returned", "X:FileNotFoundException", mav.getViewName());
	}

	@Test
	public void simpleWithIOException() {
		IOException ex = new IOException();
		SimpleController controller = new SimpleController();
		ModelAndView mav = exceptionResolver.resolveException(request, response, controller, ex);
		assertNotNull("No ModelAndView returned", mav);
		assertEquals("Invalid view name returned", "X:IOException", mav.getViewName());
	}

	@Test
	public void simpleWithSocketException() {
		SocketException ex = new SocketException();
		SimpleController controller = new SimpleController();
		ModelAndView mav = exceptionResolver.resolveException(request, response, controller, ex);
		assertNotNull("No ModelAndView returned", mav);
		assertEquals("Invalid view name returned", "Y:SocketException", mav.getViewName());
	}

	@Controller
	private static class AmbiguousController {

		@ExceptionHandler({ BindException.class, IllegalArgumentException.class })
		public String handle1(Exception ex, PortletRequest request, PortletResponse response) {
			return ClassUtils.getShortName(ex.getClass());
		}

		@ExceptionHandler
		public String handle2(IllegalArgumentException ex) {
			return ClassUtils.getShortName(ex.getClass());
		}

	}

	@Controller
	private static class InheritedController extends SimpleController {

		@Override
		public String handleIOException(IOException ex, PortletRequest request) {
			return "GenericError";
		}
	}

	@Controller
	private static class SimpleController {

		@ExceptionHandler(IllegalArgumentException.class)
		public String handleIllegalArgumentException(Exception ex) {
			return ClassUtils.getShortName(ex.getClass());
		}

		@ExceptionHandler(IOException.class)
		public String handleIOException(IOException ex, PortletRequest request) {
			return "X:" + ClassUtils.getShortName(ex.getClass());
		}

		@ExceptionHandler(SocketException.class)
		public String handleSocketException(Exception ex, PortletResponse response) {
			return "Y:" + ClassUtils.getShortName(ex.getClass());
		}

	}

}
