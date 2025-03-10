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

import jakarta.servlet.ServletException;

import static org.junit.Assert.*;

import org.junit.Test;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.liferay.spring.context.AbstractApplicationContextTests;
import com.liferay.spring.context.TestListener;
import com.liferay.spring.tests.sample.beans.TestBean;


/**
 * Should ideally be eliminated. Copied when splitting .testsuite up into individual bundles.
 *
 * @see     org.springframework.web.context.XmlWebApplicationContextTests
 * @author  Rod Johnson
 * @author  Juergen Hoeller
 * @author  Chris Beams
 */
public abstract class AbstractXmlWebApplicationContextTests extends AbstractApplicationContextTests {

	private ConfigurableWebApplicationContext root;

	@Test
	public void contextNesting() {
		TestBean father = (TestBean) this.applicationContext.getBean("father");
		assertTrue("Bean from root context", father != null);
		assertTrue("Custom BeanPostProcessor applied", father.getFriends().contains("myFriend"));

		TestBean rod = (TestBean) this.applicationContext.getBean("rod");
		assertTrue("Bean from child context", "Rod".equals(rod.getName()));
		assertTrue("Bean has external reference", rod.getSpouse() == father);
		assertTrue("Custom BeanPostProcessor not applied", !rod.getFriends().contains("myFriend"));

		rod = (TestBean) this.root.getBean("rod");
		assertTrue("Bean from root context", "Roderick".equals(rod.getName()));
		assertTrue("Custom BeanPostProcessor applied", rod.getFriends().contains("myFriend"));
	}

	@Test
	@Override
	public void count() {
		assertTrue("should have 14 beans, not " + this.applicationContext.getBeanDefinitionCount(),
			this.applicationContext.getBeanDefinitionCount() == 14);
	}

	@Test
	public void initializingBeanAndInitMethod() throws Exception {
		assertFalse(InitAndIB.constructed);

		InitAndIB iib = (InitAndIB) this.applicationContext.getBean("init-and-ib");
		assertTrue(InitAndIB.constructed);
		assertTrue(iib.afterPropertiesSetInvoked && iib.initMethodInvoked);
		assertTrue(!iib.destroyed && !iib.customDestroyed);
		this.applicationContext.close();
		assertTrue(!iib.destroyed && !iib.customDestroyed);

		ConfigurableApplicationContext parent = (ConfigurableApplicationContext) this.applicationContext.getParent();
		parent.close();
		assertTrue(iib.destroyed && iib.customDestroyed);
		parent.close();
		assertTrue(iib.destroyed && iib.customDestroyed);
	}

	/**
	 * Overridden as we can't trust superclass method
	 *
	 * @see  org.springframework.context.AbstractApplicationContextTests#testEvents()
	 */
	@Override
	protected void doTestEvents(TestListener listener, TestListener parentListener, MyEvent event) {
		TestListener listenerBean = (TestListener) this.applicationContext.getBean("testListener");
		TestListener parentListenerBean = (TestListener) this.applicationContext.getParent().getBean("parentListener");
		super.doTestEvents(listenerBean, parentListenerBean, event);

	}

	public static class InitAndIB implements InitializingBean, DisposableBean {

		public static boolean constructed;

		public boolean afterPropertiesSetInvoked;
		public boolean initMethodInvoked;
		public boolean destroyed;
		public boolean customDestroyed;

		public InitAndIB() {
			constructed = true;
		}

		@Override
		public void afterPropertiesSet() {

			if (this.initMethodInvoked)
				fail();

			this.afterPropertiesSetInvoked = true;
		}

		public void customDestroy() {

			if (!this.destroyed)
				fail();

			if (this.customDestroyed) {
				throw new IllegalStateException("Already customDestroyed");
			}

			this.customDestroyed = true;
		}

		/**
		 * Init method
		 */
		public void customInit() throws ServletException {

			if (!this.afterPropertiesSetInvoked)
				fail();

			this.initMethodInvoked = true;
		}

		@Override
		public void destroy() {

			if (this.customDestroyed)
				fail();

			if (this.destroyed) {
				throw new IllegalStateException("Already destroyed");
			}

			this.destroyed = true;
		}
	}

}
