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
package com.liferay.portletmvc4spring.context;

import java.util.Enumeration;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.junit.Assert.*;

import org.junit.Test;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import org.springframework.mock.web.MockServletContext;

import org.springframework.web.context.ContextCleanupListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.support.GenericWebApplicationContext;

import com.liferay.portletmvc4spring.test.mock.web.portlet.MockRenderRequest;
import com.liferay.portletmvc4spring.test.mock.web.portlet.ServletWrappingPortletContext;

import com.liferay.spring.tests.sample.beans.DerivedTestBean;


/**
 * @author  Juergen Hoeller
 */
public class PortletApplicationContextScopeTests {

	private static final String NAME = "scoped";

	@Test
	public void testApplicationScope() {
		ConfigurablePortletApplicationContext ac = initApplicationContext(WebApplicationContext.SCOPE_APPLICATION);
		assertNull(ac.getPortletContext().getAttribute(NAME));

		DerivedTestBean bean = ac.getBean(NAME, DerivedTestBean.class);
		assertSame(bean, ac.getPortletContext().getAttribute(NAME));
		assertSame(bean, ac.getBean(NAME));
		new PortletContextCleanupListener().contextDestroyed(new ServletContextEvent(ac.getServletContext()));
		assertTrue(bean.wasDestroyed());
	}

	@Test
	public void testGlobalSessionScope() {
		WebApplicationContext ac = initApplicationContext(PortletApplicationContext.SCOPE_GLOBAL_SESSION);
		MockRenderRequest request = new MockRenderRequest();
		PortletRequestAttributes requestAttributes = new PortletRequestAttributes(request);
		RequestContextHolder.setRequestAttributes(requestAttributes);

		try {
			assertNull(request.getPortletSession().getAttribute(NAME, PortletSession.APPLICATION_SCOPE));

			DerivedTestBean bean = ac.getBean(NAME, DerivedTestBean.class);
			assertSame(bean, request.getPortletSession().getAttribute(NAME, PortletSession.APPLICATION_SCOPE));
			assertSame(bean, ac.getBean(NAME));
			request.getPortletSession().invalidate();
			assertTrue(bean.wasDestroyed());
		}
		finally {
			RequestContextHolder.setRequestAttributes(null);
		}
	}

	@Test
	public void testRequestScope() {
		WebApplicationContext ac = initApplicationContext(WebApplicationContext.SCOPE_REQUEST);
		MockRenderRequest request = new MockRenderRequest();
		PortletRequestAttributes requestAttributes = new PortletRequestAttributes(request);
		RequestContextHolder.setRequestAttributes(requestAttributes);

		try {
			assertNull(request.getAttribute(NAME));

			DerivedTestBean bean = ac.getBean(NAME, DerivedTestBean.class);
			assertSame(bean, request.getAttribute(NAME));
			assertSame(bean, ac.getBean(NAME));
			requestAttributes.requestCompleted();
			assertTrue(bean.wasDestroyed());
		}
		finally {
			RequestContextHolder.setRequestAttributes(null);
		}
	}

	@Test
	public void testSessionScope() {
		WebApplicationContext ac = initApplicationContext(WebApplicationContext.SCOPE_SESSION);
		MockRenderRequest request = new MockRenderRequest();
		PortletRequestAttributes requestAttributes = new PortletRequestAttributes(request);
		RequestContextHolder.setRequestAttributes(requestAttributes);

		try {
			assertNull(request.getPortletSession().getAttribute(NAME));

			DerivedTestBean bean = ac.getBean(NAME, DerivedTestBean.class);
			assertSame(bean, request.getPortletSession().getAttribute(NAME));
			assertSame(bean, ac.getBean(NAME));
			request.getPortletSession().invalidate();
			assertTrue(bean.wasDestroyed());
		}
		finally {
			RequestContextHolder.setRequestAttributes(null);
		}
	}

	private ConfigurablePortletApplicationContext initApplicationContext(String scope) {
		MockServletContext sc = new MockServletContext();
		GenericWebApplicationContext rac = new GenericWebApplicationContext(sc);
		rac.refresh();

		PortletContext pc = new ServletWrappingPortletContext(sc);
		StaticPortletApplicationContext ac = new StaticPortletApplicationContext();
		ac.setParent(rac);
		ac.setPortletContext(pc);

		GenericBeanDefinition bd = new GenericBeanDefinition();
		bd.setBeanClass(DerivedTestBean.class);
		bd.setScope(scope);
		ac.registerBeanDefinition(NAME, bd);
		ac.refresh();

		return ac;
	}

	private static class PortletContextCleanupListener extends ContextCleanupListener {

		private static final Log logger = LogFactory.getLog(PortletContextCleanupListener.class);

		/**
		 * Find all ServletContext attributes which implement {@link DisposableBean} and destroy them, removing all
		 * affected ServletContext attributes eventually.
		 *
		 * @param  sc  the ServletContext to check
		 */
		static void cleanupAttributes(ServletContext sc) {
			Enumeration<String> attrNames = sc.getAttributeNames();

			while (attrNames.hasMoreElements()) {
				String attrName = attrNames.nextElement();

				if (attrName.startsWith("com.liferay.portletmvc4spring.")) {
					Object attrValue = sc.getAttribute(attrName);

					if (attrValue instanceof DisposableBean) {

						try {
							((DisposableBean) attrValue).destroy();
						}
						catch (Throwable ex) {
							logger.error("Couldn't invoke destroy method of attribute with name '" + attrName + "'",
								ex);
						}
					}
				}
			}
		}

		@Override
		public void contextDestroyed(ServletContextEvent event) {
			cleanupAttributes(event.getServletContext());
		}
	}
}
