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
package com.liferay.portletmvc4spring.mvc;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.EventPortlet;
import jakarta.portlet.EventRequest;
import jakarta.portlet.EventResponse;
import jakarta.portlet.Portlet;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.ResourceServingPortlet;
import jakarta.portlet.WindowState;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.liferay.portletmvc4spring.ModelAndView;
import com.liferay.portletmvc4spring.NoHandlerFoundException;
import com.liferay.portletmvc4spring.context.PortletConfigAware;
import com.liferay.portletmvc4spring.context.PortletContextAware;
import com.liferay.portletmvc4spring.util.PortletUtils;


/**
 * {@link Controller} implementation that wraps a portlet instance which it manages internally. Such a wrapped portlet
 * is not known outside of this controller; its entire lifecycle is covered here.
 *
 * <p>Useful to invoke an existing portlet via Spring's dispatching infrastructure, for example to apply Spring {@link
 * com.liferay.portletmvc4spring.HandlerInterceptor HandlerInterceptors} to its requests.
 *
 * <p><b>Example:</b>
 *
 * <pre class="code">&lt;bean id="wrappingController" class="com.liferay.portletmvc4spring.mvc.PortletWrappingController"&gt;
     &lt;property name="portletClass"&gt;
       &lt;value&gt;com.liferay.portletmvc4spring.sample.HelloWorldPortlet&lt;/value&gt;
     &lt;/property&gt;
     &lt;property name="portletName"&gt;
       &lt;value&gt;hello-world&lt;/value&gt;
     &lt;/property&gt;
     &lt;property name="initParameters"&gt;
       &lt;props&gt;
         &lt;prop key="config"&gt;/WEB-INF/hello-world-portlet-config.xml&lt;/prop&gt;
       &lt;/props&gt;
     &lt;/property&gt;
   &lt;/bean&gt;</pre>
 *
 * @author  Juergen Hoeller
 * @author  John A. Lewis
 * @since   2.0
 */
public class PortletWrappingController extends AbstractController implements ResourceAwareController,
	EventAwareController, BeanNameAware, InitializingBean, DisposableBean, PortletContextAware, PortletConfigAware {

	private boolean useSharedPortletConfig = true;

	private PortletContext portletContext;

	private PortletConfig portletConfig;

	private Class<?> portletClass;

	private String portletName;

	private Map<String, String> initParameters = new LinkedHashMap<String, String>();

	private String beanName;

	private Portlet portletInstance;

	@Override
	public void afterPropertiesSet() throws Exception {

		if (this.portletClass == null) {
			throw new IllegalArgumentException("portletClass is required");
		}

		if (!Portlet.class.isAssignableFrom(this.portletClass)) {
			throw new IllegalArgumentException("portletClass [" + this.portletClass.getName() +
				"] needs to implement interface [jakarta.portlet.Portlet]");
		}

		if (this.portletName == null) {
			this.portletName = this.beanName;
		}

		PortletConfig config = this.portletConfig;

		if ((config == null) || !this.useSharedPortletConfig) {
			config = new DelegatingPortletConfig();
		}

		this.portletInstance = (Portlet) this.portletClass.newInstance();
		this.portletInstance.init(config);
	}

	@Override
	public void destroy() {
		this.portletInstance.destroy();
	}

	@Override
	public void handleEventRequest(EventRequest request, EventResponse response) throws Exception {

		if (!(this.portletInstance instanceof EventPortlet)) {
			logger.debug("Ignoring event request for non-event target portlet: " + this.portletInstance.getClass());

			return;
		}

		EventPortlet eventPortlet = (EventPortlet) this.portletInstance;

		// Delegate to PortletContentGenerator for checking and preparing.
		check(request, response);

		// Execute in synchronized block if required.
		if (isSynchronizeOnSession()) {
			PortletSession session = request.getPortletSession(false);

			if (session != null) {
				Object mutex = PortletUtils.getSessionMutex(session);

				synchronized (mutex) {
					eventPortlet.processEvent(request, response);

					return;
				}
			}
		}

		eventPortlet.processEvent(request, response);
	}

	@Override
	public ModelAndView handleResourceRequest(ResourceRequest request, ResourceResponse response) throws Exception {

		if (!(this.portletInstance instanceof ResourceServingPortlet)) {
			throw new NoHandlerFoundException("Cannot handle resource request - target portlet [" +
				this.portletInstance.getClass() + " does not implement ResourceServingPortlet");
		}

		ResourceServingPortlet resourcePortlet = (ResourceServingPortlet) this.portletInstance;

		// Delegate to PortletContentGenerator for checking and preparing.
		checkAndPrepare(request, response);

		// Execute in synchronized block if required.
		if (isSynchronizeOnSession()) {
			PortletSession session = request.getPortletSession(false);

			if (session != null) {
				Object mutex = PortletUtils.getSessionMutex(session);

				synchronized (mutex) {
					resourcePortlet.serveResource(request, response);

					return null;
				}
			}
		}

		resourcePortlet.serveResource(request, response);

		return null;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	/**
	 * Specify init parameters for the portlet to wrap, as name-value pairs.
	 */
	public void setInitParameters(Map<String, String> initParameters) {
		this.initParameters = initParameters;
	}

	/**
	 * Set the class of the Portlet to wrap. Needs to implement {@code jakarta.portlet.Portlet}.
	 *
	 * @see  jakarta.portlet.Portlet
	 */
	public void setPortletClass(Class<?> portletClass) {
		this.portletClass = portletClass;
	}

	@Override
	public void setPortletConfig(PortletConfig portletConfig) {
		this.portletConfig = portletConfig;
	}

	@Override
	public void setPortletContext(PortletContext portletContext) {
		this.portletContext = portletContext;
	}

	/**
	 * Set the name of the Portlet to wrap. Default is the bean name of this controller.
	 */
	public void setPortletName(String portletName) {
		this.portletName = portletName;
	}

	/**
	 * Set whether to use the shared PortletConfig object passed in through {@code setPortletConfig}, if available.
	 *
	 * <p>Default is "true". Turn this setting to "false" to pass in a mock PortletConfig object with the bean name as
	 * portlet name, holding the current PortletContext.
	 *
	 * @see  #setPortletConfig
	 */
	public void setUseSharedPortletConfig(boolean useSharedPortletConfig) {
		this.useSharedPortletConfig = useSharedPortletConfig;
	}

	@Override
	protected void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {

		this.portletInstance.processAction(request, response);
	}

	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response)
		throws Exception {

		this.portletInstance.render(request, response);

		return null;
	}

	/**
	 * Internal implementation of the PortletConfig interface, to be passed to the wrapped portlet.
	 *
	 * <p>Delegates to {@link PortletWrappingController} fields and methods to provide init parameters and other
	 * environment info.
	 */
	private class DelegatingPortletConfig implements PortletConfig {

		@Override
		public Map<String, String[]> getContainerRuntimeOptions() {
			return ((portletConfig != null) ? portletConfig.getContainerRuntimeOptions() : null);
		}

		@Override
		public String getDefaultNamespace() {
			return XMLConstants.NULL_NS_URI;
		}

		@Override
		public String getInitParameter(String paramName) {
			return initParameters.get(paramName);
		}

		@Override
		public Enumeration<String> getInitParameterNames() {
			return Collections.enumeration(initParameters.keySet());
		}

		@Override
		public PortletContext getPortletContext() {
			return portletContext;
		}

		@Override
		public Enumeration<PortletMode> getPortletModes(String mimeType) {
			return ((portletConfig != null) ? portletConfig.getPortletModes(mimeType) : Collections.emptyEnumeration());
		}

		@Override
		public String getPortletName() {
			return portletName;
		}

		@Override
		public Enumeration<QName> getProcessingEventQNames() {
			return Collections.enumeration(Collections.<QName>emptySet());
		}

		@Override
		public Map<String, QName> getPublicRenderParameterDefinitions() {
			return ((portletConfig != null) ? portletConfig.getPublicRenderParameterDefinitions()
											: Collections.emptyMap());
		}

		@Override
		public Enumeration<String> getPublicRenderParameterNames() {
			return Collections.enumeration(Collections.<String>emptySet());
		}

		@Override
		public Enumeration<QName> getPublishingEventQNames() {
			return Collections.enumeration(Collections.<QName>emptySet());
		}

		@Override
		public ResourceBundle getResourceBundle(Locale locale) {
			return ((portletConfig != null) ? portletConfig.getResourceBundle(locale) : null);
		}

		@Override
		public Enumeration<Locale> getSupportedLocales() {
			return Collections.enumeration(Collections.<Locale>emptySet());
		}

		@Override
		public Enumeration<WindowState> getWindowStates(String mimeType) {
			return ((portletConfig != null) ? portletConfig.getWindowStates(mimeType) : Collections.emptyEnumeration());
		}
	}

}
