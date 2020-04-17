/**
 * Copyright (c) 2000-2019 the original author or authors.
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
package com.liferay.portletmvc4spring.mvc.method.annotation;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.ClientDataRequest;
import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.MimeResponse;
import javax.portlet.MutableRenderParameters;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderParameters;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.StateAwareResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;

import org.springframework.lang.Nullable;

import org.springframework.ui.ModelMap;

import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.bind.support.DefaultSessionAttributeStore;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ErrorsMethodArgumentResolver;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.annotation.RequestHeaderMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMapMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.annotation.SessionAttributesHandler;
import org.springframework.web.method.annotation.SessionStatusMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.AsyncTaskMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.CallableMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.DeferredResultMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMapMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.MatrixVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMapMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RedirectAttributesMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestAttributeMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestPartMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitterReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.SessionAttributeMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBodyReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.UriComponentsBuilderMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.liferay.portletmvc4spring.ModelAndView;
import com.liferay.portletmvc4spring.NoHandlerFoundException;
import com.liferay.portletmvc4spring.bind.annotation.ActionMapping;
import com.liferay.portletmvc4spring.bind.annotation.EventMapping;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;
import com.liferay.portletmvc4spring.bind.annotation.ResourceMapping;
import com.liferay.portletmvc4spring.bind.annotation.support.HandlerMethodResolver;
import com.liferay.portletmvc4spring.context.PortletWebRequest;
import com.liferay.portletmvc4spring.mvc.annotation.PortletAnnotationMappingUtils;
import com.liferay.portletmvc4spring.mvc.method.AbstractPortletHandlerMethodAdapter;
import com.liferay.portletmvc4spring.util.PortletUtils;


/**
 * Extension of {@link AbstractPortletHandlerMethodAdapter} that supports {@code HandlerMethods} annotated with {@link
 * com.liferay.portletmvc4spring.bind.annotation.ActionMapping}, {@link
 * com.liferay.portletmvc4spring.bind.annotation.EventMapping}, {@link
 * com.liferay.portletmvc4spring.bind.annotation.RenderMapping}, or {@link
 * com.liferay.portletmvc4spring.bind.annotation.ResourceMapping}.
 *
 * <p>Support for custom argument and return value types can be added via {@link #setCustomArgumentResolvers} and {@link
 * #setCustomReturnValueHandlers}, or alternatively, to re-configure all argument and return value types, use {@link
 * #setArgumentResolvers} and {@link #setReturnValueHandlers}.
 *
 * @author  Rossen Stoyanchev
 * @author  Juergen Hoeller
 * @author  Neil Griffin
 * @since   5.1
 * @see     HandlerMethodArgumentResolver
 * @see     HandlerMethodReturnValueHandler
 */
public class PortletRequestMappingHandlerAdapter extends AbstractPortletHandlerMethodAdapter
	implements BeanFactoryAware, InitializingBean {

	public static final String IMPLICIT_MODEL_SESSION_ATTRIBUTE = PortletRequestMappingHandlerAdapter.class.getName() +
		".IMPLICIT_MODEL";

	public static final String IMPLICIT_MODEL_RENDER_PARAMETER = "implicitModel";

	/** MethodFilter that matches {@link InitBinder @InitBinder} methods. */
	public static final ReflectionUtils.MethodFilter INIT_BINDER_METHODS = method ->
		AnnotatedElementUtils.hasAnnotation(method, InitBinder.class);

	/** MethodFilter that matches {@link ModelAttribute @ModelAttribute} methods. */
	public static final ReflectionUtils.MethodFilter MODEL_ATTRIBUTE_METHODS = method ->
		(!AnnotatedElementUtils.hasAnnotation(method, RequestMapping.class) &&
		AnnotatedElementUtils.hasAnnotation(method, ModelAttribute.class));

	private static final String SESSION_COMPLETE_RENDER_PARAMETER = "sessionComplete";

	@Nullable
	private List<HandlerMethodArgumentResolver> customArgumentResolvers;

	@Nullable
	private HandlerMethodArgumentResolverComposite argumentResolvers;

	@Nullable
	private HandlerMethodArgumentResolverComposite initBinderArgumentResolvers;

	@Nullable
	private List<HandlerMethodReturnValueHandler> customReturnValueHandlers;

	@Nullable
	private HandlerMethodReturnValueHandlerComposite returnValueHandlers;

	@Nullable
	private List<PortletModelAndViewResolver> modelAndViewResolvers;

	private ContentNegotiationManager contentNegotiationManager = new ContentNegotiationManager();

	private List<HttpMessageConverter<?>> messageConverters;

	private List<Object> requestResponseBodyAdvice = new ArrayList<>();

	@Nullable
	private WebBindingInitializer webBindingInitializer;

	private boolean ignoreDefaultModelOnRedirect = false;

	private int cacheSecondsForSessionAttributeHandlers = 0;

	private boolean synchronizeOnSession = false;

	private AsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("MvcAsync");

	private SessionAttributeStore sessionAttributeStore = new DefaultSessionAttributeStore();

	private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

	@Nullable
	private ConfigurableBeanFactory beanFactory;

	private final Map<Class<?>, SessionAttributesHandler> sessionAttributesHandlerCache = new ConcurrentHashMap<>(64);

	private final Map<Class<?>, Set<Method>> initBinderCache = new ConcurrentHashMap<>(64);

	private final Map<ControllerAdviceBean, Set<Method>> initBinderAdviceCache = new LinkedHashMap<>();

	private final Map<Class<?>, PortletHandlerMethodResolver> methodResolverCache = new ConcurrentHashMap<>(64);

	private final Map<Class<?>, Set<Method>> modelAttributeCache = new ConcurrentHashMap<>(64);

	private final Map<ControllerAdviceBean, Set<Method>> modelAttributeAdviceCache = new LinkedHashMap<>();

	public PortletRequestMappingHandlerAdapter() {
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setWriteAcceptCharset(false); // see SPR-7316

		this.messageConverters = new ArrayList<>(4);
		this.messageConverters.add(new ByteArrayHttpMessageConverter());
		this.messageConverters.add(stringHttpMessageConverter);

		try {
			this.messageConverters.add(new SourceHttpMessageConverter<>());
		}
		catch (Error err) {
			// Ignore when no TransformerFactory implementation is available
		}

		this.messageConverters.add(new AllEncompassingFormHttpMessageConverter());
	}

	@Override
	public void afterPropertiesSet() {

		// Do this first, it may add ResponseBody advice beans
		initControllerAdviceCache();

		if (this.argumentResolvers == null) {
			List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
			this.argumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
		}

		if (this.initBinderArgumentResolvers == null) {
			List<HandlerMethodArgumentResolver> resolvers = getDefaultInitBinderArgumentResolvers();
			this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
		}

		if (this.returnValueHandlers == null) {
			List<HandlerMethodReturnValueHandler> handlers = getDefaultReturnValueHandlers();
			this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite().addHandlers(handlers);
		}
	}

	/**
	 * Return the configured argument resolvers, or possibly {@code null} if not initialized yet via {@link
	 * #afterPropertiesSet()}.
	 */
	@Nullable
	public List<HandlerMethodArgumentResolver> getArgumentResolvers() {
		return ((this.argumentResolvers != null) ? this.argumentResolvers.getResolvers() : null);
	}

	/**
	 * Return the custom argument resolvers, or {@code null}.
	 */
	@Nullable
	public List<HandlerMethodArgumentResolver> getCustomArgumentResolvers() {
		return this.customArgumentResolvers;
	}

	/**
	 * Return the custom return value handlers, or {@code null}.
	 */
	@Nullable
	public List<HandlerMethodReturnValueHandler> getCustomReturnValueHandlers() {
		return this.customReturnValueHandlers;
	}

	/**
	 * Return the argument resolvers for {@code @InitBinder} methods, or possibly {@code null} if not initialized yet
	 * via {@link #afterPropertiesSet()}.
	 */
	@Nullable
	public List<HandlerMethodArgumentResolver> getInitBinderArgumentResolvers() {
		return ((this.initBinderArgumentResolvers != null) ? this.initBinderArgumentResolvers.getResolvers() : null);
	}

	/**
	 * Return the configured message body converters.
	 */
	public List<HttpMessageConverter<?>> getMessageConverters() {
		return this.messageConverters;
	}

	/**
	 * Return the configured {@link ModelAndViewResolver ModelAndViewResolvers}, or {@code null}.
	 */
	@Nullable
	public List<PortletModelAndViewResolver> getModelAndViewResolvers() {
		return this.modelAndViewResolvers;
	}

	/**
	 * Return the configured handlers, or possibly {@code null} if not initialized yet via {@link
	 * #afterPropertiesSet()}.
	 */
	@Nullable
	public List<HandlerMethodReturnValueHandler> getReturnValueHandlers() {
		return ((this.returnValueHandlers != null) ? this.returnValueHandlers.getHandlers() : null);
	}

	/**
	 * Return the configured WebBindingInitializer, or {@code null} if none.
	 */
	@Nullable
	public WebBindingInitializer getWebBindingInitializer() {
		return this.webBindingInitializer;
	}

	/**
	 * Configure the complete list of supported argument types thus overriding the resolvers that would otherwise be
	 * configured by default.
	 */
	public void setArgumentResolvers(@Nullable List<HandlerMethodArgumentResolver> argumentResolvers) {

		if (argumentResolvers == null) {
			this.argumentResolvers = null;
		}
		else {
			this.argumentResolvers = new HandlerMethodArgumentResolverComposite();
			this.argumentResolvers.addResolvers(argumentResolvers);
		}
	}

	/**
	 * A {@link ConfigurableBeanFactory} is expected for resolving expressions in method argument default values.
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) {

		if (beanFactory instanceof ConfigurableBeanFactory) {
			this.beanFactory = (ConfigurableBeanFactory) beanFactory;
		}
	}

	/**
	 * Set the {@link ContentNegotiationManager} to use to determine requested media types. If not set, the default
	 * constructor is used.
	 */
	public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
		this.contentNegotiationManager = contentNegotiationManager;
	}

	/**
	 * Provide resolvers for custom argument types. Custom resolvers are ordered after built-in ones. To override the
	 * built-in support for argument resolution use {@link #setArgumentResolvers} instead.
	 */
	public void setCustomArgumentResolvers(@Nullable List<HandlerMethodArgumentResolver> argumentResolvers) {
		this.customArgumentResolvers = argumentResolvers;
	}

	/**
	 * Provide handlers for custom return value types. Custom handlers are ordered after built-in ones. To override the
	 * built-in support for return value handling use {@link #setReturnValueHandlers}.
	 */
	public void setCustomReturnValueHandlers(@Nullable List<HandlerMethodReturnValueHandler> returnValueHandlers) {
		this.customReturnValueHandlers = returnValueHandlers;
	}

	/**
	 * Configure the supported argument types in {@code @InitBinder} methods.
	 */
	public void setInitBinderArgumentResolvers(@Nullable List<HandlerMethodArgumentResolver> argumentResolvers) {

		if (argumentResolvers == null) {
			this.initBinderArgumentResolvers = null;
		}
		else {
			this.initBinderArgumentResolvers = new HandlerMethodArgumentResolverComposite();
			this.initBinderArgumentResolvers.addResolvers(argumentResolvers);
		}
	}

	/**
	 * Provide the converters to use in argument resolvers and return value handlers that support reading and/or writing
	 * to the body of the request and response.
	 */
	public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		this.messageConverters = messageConverters;
	}

	/**
	 * Provide custom {@link ModelAndViewResolver ModelAndViewResolvers}.
	 *
	 * <p><strong>Note:</strong> This method is available for backwards compatibility only. However, it is recommended
	 * to re-write a {@code ModelAndViewResolver} as {@link HandlerMethodReturnValueHandler}. An adapter between the two
	 * interfaces is not possible since the {@link HandlerMethodReturnValueHandler#supportsReturnType} method cannot be
	 * implemented. Hence {@code ModelAndViewResolver}s are limited to always being invoked at the end after all other
	 * return value handlers have been given a chance.
	 *
	 * <p>A {@code HandlerMethodReturnValueHandler} provides better access to the return type and controller method
	 * information and can be ordered freely relative to other return value handlers.
	 */
	public void setModelAndViewResolvers(@Nullable List<PortletModelAndViewResolver> modelAndViewResolvers) {
		this.modelAndViewResolvers = modelAndViewResolvers;
	}

	/**
	 * Set the ParameterNameDiscoverer to use for resolving method parameter names if needed (e.g. for default attribute
	 * names).
	 *
	 * <p>Default is a {@link org.springframework.core.DefaultParameterNameDiscoverer}.
	 */
	public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
		this.parameterNameDiscoverer = parameterNameDiscoverer;
	}

	/**
	 * Configure the complete list of supported return value types thus overriding handlers that would otherwise be
	 * configured by default.
	 */
	public void setReturnValueHandlers(@Nullable List<HandlerMethodReturnValueHandler> returnValueHandlers) {

		if (returnValueHandlers == null) {
			this.returnValueHandlers = null;
		}
		else {
			this.returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
			this.returnValueHandlers.addHandlers(returnValueHandlers);
		}
	}

	/**
	 * Specify the strategy to store session attributes with. The default is {@link
	 * org.springframework.web.bind.support.DefaultSessionAttributeStore}, storing session attributes in the HttpSession
	 * with the same attribute name as in the model.
	 */
	public void setSessionAttributeStore(SessionAttributeStore sessionAttributeStore) {
		this.sessionAttributeStore = sessionAttributeStore;
	}

	/**
	 * Set the default {@link AsyncTaskExecutor} to use when a controller method return a {@link Callable}. Controller
	 * methods can override this default on a per-request basis by returning an {@link WebAsyncTask}.
	 *
	 * <p>By default a {@link SimpleAsyncTaskExecutor} instance is used. It's recommended to change that default in
	 * production as the simple executor does not re-use threads.
	 */
	public void setTaskExecutor(AsyncTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	/**
	 * Provide a WebBindingInitializer with "global" initialization to apply to every DataBinder instance.
	 */
	public void setWebBindingInitializer(@Nullable WebBindingInitializer webBindingInitializer) {
		this.webBindingInitializer = webBindingInitializer;
	}

	/**
	 * Template method to create a new InitBinderDataBinderFactory instance.
	 *
	 * <p>The default implementation creates a PortletRequestDataBinderFactory. This can be overridden for custom
	 * ServletRequestDataBinder subclasses.
	 *
	 * @param   binderMethods  {@code @InitBinder} methods
	 *
	 * @return  the InitBinderDataBinderFactory instance to use
	 *
	 * @throws  Exception  in case of invalid state or arguments
	 */
	protected InitBinderDataBinderFactory createDataBinderFactory(List<InvocableHandlerMethod> binderMethods)
		throws Exception {

		return new PortletRequestDataBinderFactory(binderMethods, getWebBindingInitializer());
	}

	/**
	 * Create a {@link PortletInvocableHandlerMethod} from the given {@link HandlerMethod} definition.
	 *
	 * @param   handlerMethod  the {@link HandlerMethod} definition
	 *
	 * @return  the corresponding {@link PortletInvocableHandlerMethod} (or custom subclass thereof)
	 *
	 * @since   5.1
	 */
	protected PortletInvocableHandlerMethod createInvocableHandlerMethod(HandlerMethod handlerMethod) {
		return new PortletInvocableHandlerMethod(handlerMethod);
	}

	protected ModelAndView doHandle(PortletRequest request, PortletResponse response, Object handler) throws Exception {

		if (handler instanceof HandlerMethod) {
			return doHandle(request, response, (HandlerMethod) handler);
		}

		PortletHandlerMethodResolver methodResolver = getMethodResolver(handler);
		Method method = methodResolver.resolveHandlerMethod(request);

		return doHandle(request, response, new HandlerMethod(handler, method));
	}

	protected ModelAndView doHandle(PortletRequest request, PortletResponse response, HandlerMethod handlerMethod)
		throws Exception {
		ModelMap implicitModel = null;

		if (response instanceof MimeResponse) {

			MimeResponse mimeResponse = (MimeResponse) response;

			// Detect implicit model from associated action phase.
			if (response instanceof RenderResponse) {
				PortletSession session = request.getPortletSession(false);

				if (session != null) {

					if (request.getParameter(IMPLICIT_MODEL_RENDER_PARAMETER) != null) {
						implicitModel = (ModelMap) session.getAttribute(IMPLICIT_MODEL_SESSION_ATTRIBUTE);
					}
					else {
						session.removeAttribute(IMPLICIT_MODEL_SESSION_ATTRIBUTE);
					}
				}
			}

			if (handlerMethod.getBean().getClass().getAnnotation(SessionAttributes.class) != null) {

				// Always prevent caching in case of session attribute management.
				checkAndPrepare(request, mimeResponse, this.cacheSecondsForSessionAttributeHandlers);
			}
			else {

				// Uses configured default cacheSeconds setting.
				checkAndPrepare(request, mimeResponse);
			}
		}

		// Execute invokeHandlerMethod in synchronized block if required.
		if (this.synchronizeOnSession) {
			PortletSession session = request.getPortletSession(false);

			if (session != null) {
				Object mutex = PortletUtils.getSessionMutex(session);

				synchronized (mutex) {
					return invokeHandlerMethod(request, response, handlerMethod, implicitModel);
				}
			}
		}

		return invokeHandlerMethod(request, response, handlerMethod, implicitModel);
	}

	/**
	 * Return the owning factory of this bean instance, or {@code null} if none.
	 */
	@Nullable
	protected ConfigurableBeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	@Override
	protected void handleActionInternal(ActionRequest request, ActionResponse response, Object handler)
		throws Exception {

		Object returnValue = doHandle(request, response, handler);

		if (returnValue != null) {
			throw new IllegalStateException("Invalid action method return value: " + returnValue);
		}
	}

	@Override
	protected void handleEventInternal(EventRequest request, EventResponse response, Object handler) throws Exception {

		Object returnValue = doHandle(request, response, handler);

		if (returnValue != null) {
			throw new IllegalStateException("Invalid event method return value: " + returnValue);
		}
	}

	@Override
	protected ModelAndView handleRenderInternal(RenderRequest request, RenderResponse response, Object handler)
		throws Exception {
		return doHandle(request, response, handler);
	}

	@Override
	protected ModelAndView handleResourceInternal(ResourceRequest request, ResourceResponse response, Object handler)
		throws Exception {
		return doHandle(request, response, handler);
	}

	/**
	 * Always return {@code true} since any method argument and return value type will be processed in some way. A
	 * method argument not recognized by any HandlerMethodArgumentResolver is interpreted as a request parameter if it
	 * is a simple type, or as a model attribute otherwise. A return value not recognized by any
	 * HandlerMethodReturnValueHandler will be interpreted as a model attribute.
	 */
	@Override
	protected boolean supportsInternal(Object handler) {

		if (handler instanceof HandlerMethod) {
			return true;
		}

		return getMethodResolver(handler).hasHandlerMethods();
	}

	private InvocableHandlerMethod createInitBinderMethod(Object bean, Method method) {
		InvocableHandlerMethod binderMethod = new InvocableHandlerMethod(bean, method);

		if (this.initBinderArgumentResolvers != null) {
			binderMethod.setHandlerMethodArgumentResolvers(this.initBinderArgumentResolvers);
		}

		binderMethod.setDataBinderFactory(new DefaultDataBinderFactory(this.webBindingInitializer));
		binderMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);

		return binderMethod;
	}

	private InvocableHandlerMethod createModelAttributeMethod(WebDataBinderFactory factory, Object bean,
		Method method) {
		InvocableHandlerMethod attrMethod = new InvocableHandlerMethod(bean, method);

		if (this.argumentResolvers != null) {
			attrMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
		}

		attrMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);
		attrMethod.setDataBinderFactory(factory);

		return attrMethod;
	}

	// Count all advice, including explicit registrations..

	private int getBodyAdviceCount(Class<?> adviceType) {
		List<Object> advice = this.requestResponseBodyAdvice;

		return RequestBodyAdvice.class.isAssignableFrom(adviceType)
			? RequestResponseBodyAdviceChain.getAdviceByType(advice, RequestBodyAdvice.class).size()
			: RequestResponseBodyAdviceChain.getAdviceByType(advice, ResponseBodyAdvice.class).size();
	}

	private WebDataBinderFactory getDataBinderFactory(HandlerMethod handlerMethod) throws Exception {
		Class<?> handlerType = handlerMethod.getBeanType();
		Set<Method> methods = this.initBinderCache.get(handlerType);

		if (methods == null) {
			methods = MethodIntrospector.selectMethods(handlerType, INIT_BINDER_METHODS);
			this.initBinderCache.put(handlerType, methods);
		}

		List<InvocableHandlerMethod> initBinderMethods = new ArrayList<>();

		// Global methods first
		this.initBinderAdviceCache.forEach((clazz, methodSet) -> {

			if (clazz.isApplicableToBeanType(handlerType)) {
				Object bean = clazz.resolveBean();

				for (Method method : methodSet) {
					initBinderMethods.add(createInitBinderMethod(bean, method));
				}
			}
		});

		for (Method method : methods) {
			Object bean = handlerMethod.getBean();
			initBinderMethods.add(createInitBinderMethod(bean, method));
		}

		return createDataBinderFactory(initBinderMethods);
	}

	/**
	 * Return the list of argument resolvers to use including built-in resolvers and custom resolvers provided via
	 * {@link #setCustomArgumentResolvers}.
	 */
	private List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

		// Annotation-based argument resolution
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), false));
		resolvers.add(new RequestParamMapMethodArgumentResolver());
		resolvers.add(new PathVariableMethodArgumentResolver());
		resolvers.add(new PathVariableMapMethodArgumentResolver());
		resolvers.add(new MatrixVariableMethodArgumentResolver());
		resolvers.add(new MatrixVariableMapMethodArgumentResolver());
		resolvers.add(new PortletModelAttributeMethodProcessor(false));
		resolvers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(), this.requestResponseBodyAdvice));
		resolvers.add(new RequestPartMethodArgumentResolver(getMessageConverters(), this.requestResponseBodyAdvice));
		resolvers.add(new RequestHeaderMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new RequestHeaderMapMethodArgumentResolver());
		resolvers.add(new PortletCookieValueMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new ExpressionValueMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new SessionAttributeMethodArgumentResolver());
		resolvers.add(new RequestAttributeMethodArgumentResolver());

		// Type-based argument resolution
		resolvers.add(new PortletRequestMethodArgumentResolver());
		resolvers.add(new PortletResponseMethodArgumentResolver());
		resolvers.add(new HttpEntityMethodProcessor(getMessageConverters(), this.requestResponseBodyAdvice));
		resolvers.add(new RedirectAttributesMethodArgumentResolver());
		resolvers.add(new ModelMethodProcessor());
		resolvers.add(new MapMethodProcessor());
		resolvers.add(new ErrorsMethodArgumentResolver());
		resolvers.add(new SessionStatusMethodArgumentResolver());
		resolvers.add(new UriComponentsBuilderMethodArgumentResolver());

		// Custom arguments
		if (getCustomArgumentResolvers() != null) {
			resolvers.addAll(getCustomArgumentResolvers());
		}

		// Catch-all
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), true));
		resolvers.add(new PortletModelAttributeMethodProcessor(true));

		return resolvers;
	}

	/**
	 * Return the list of argument resolvers to use for {@code @InitBinder} methods including built-in and custom
	 * resolvers.
	 */
	private List<HandlerMethodArgumentResolver> getDefaultInitBinderArgumentResolvers() {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

		// Annotation-based argument resolution
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), false));
		resolvers.add(new RequestParamMapMethodArgumentResolver());
		resolvers.add(new PathVariableMethodArgumentResolver());
		resolvers.add(new PathVariableMapMethodArgumentResolver());
		resolvers.add(new MatrixVariableMethodArgumentResolver());
		resolvers.add(new MatrixVariableMapMethodArgumentResolver());
		resolvers.add(new ExpressionValueMethodArgumentResolver(getBeanFactory()));
		resolvers.add(new SessionAttributeMethodArgumentResolver());
		resolvers.add(new RequestAttributeMethodArgumentResolver());

		// Type-based argument resolution
		resolvers.add(new PortletRequestMethodArgumentResolver());
		resolvers.add(new PortletResponseMethodArgumentResolver());

		// Custom arguments
		if (getCustomArgumentResolvers() != null) {
			resolvers.addAll(getCustomArgumentResolvers());
		}

		// Catch-all
		resolvers.add(new RequestParamMethodArgumentResolver(getBeanFactory(), true));

		return resolvers;
	}

	/**
	 * Return the list of return value handlers to use including built-in and custom handlers provided via {@link
	 * #setReturnValueHandlers}.
	 */
	private List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
		List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>();

		// Single-purpose return value types
		handlers.add(new PortletModelAndViewMethodReturnValueHandler());
		handlers.add(new ModelMethodProcessor());
		handlers.add(new ViewMethodReturnValueHandler());
		handlers.add(new ResponseBodyEmitterReturnValueHandler(getMessageConverters(),
				ReactiveAdapterRegistry.getSharedInstance(), this.taskExecutor, this.contentNegotiationManager));
		handlers.add(new StreamingResponseBodyReturnValueHandler());
		handlers.add(new HttpEntityMethodProcessor(getMessageConverters(), this.contentNegotiationManager,
				this.requestResponseBodyAdvice));
		handlers.add(new HttpHeadersReturnValueHandler());
		handlers.add(new CallableMethodReturnValueHandler());
		handlers.add(new DeferredResultMethodReturnValueHandler());
		handlers.add(new AsyncTaskMethodReturnValueHandler(this.beanFactory));

		// Annotation-based return value types
		handlers.add(new ModelAttributeMethodProcessor(false));
		handlers.add(new RequestResponseBodyMethodProcessor(getMessageConverters(), this.contentNegotiationManager,
				this.requestResponseBodyAdvice));

		// Multi-purpose return value types
		handlers.add(new ViewNameMethodReturnValueHandler());
		handlers.add(new MapMethodProcessor());

		// Custom return value types
		if (getCustomReturnValueHandlers() != null) {
			handlers.addAll(getCustomReturnValueHandlers());
		}

		// Catch-all
		if (!CollectionUtils.isEmpty(getModelAndViewResolvers())) {
			handlers.add(new PortletModelAndViewResolverMethodReturnValueHandler(getModelAndViewResolvers()));
		}
		else {
			handlers.add(new ModelAttributeMethodProcessor(true));
		}

		return handlers;
	}

	/**
	 * Build a HandlerMethodResolver for the given handler type.
	 */
	private PortletHandlerMethodResolver getMethodResolver(Object handler) {
		Class<?> handlerClass = ClassUtils.getUserClass(handler);
		PortletHandlerMethodResolver resolver = this.methodResolverCache.get(handlerClass);

		if (resolver == null) {

			synchronized (this.methodResolverCache) {
				resolver = this.methodResolverCache.get(handlerClass);

				if (resolver == null) {
					resolver = new PortletHandlerMethodResolver(handlerClass);
					this.methodResolverCache.put(handlerClass, resolver);
				}
			}
		}

		return resolver;
	}

	@Nullable
	private ModelAndView getModelAndView(ModelAndViewContainer mavContainer, ModelFactory modelFactory,
		NativeWebRequest webRequest) throws Exception {

		modelFactory.updateModel(webRequest, mavContainer);

		if (mavContainer.isRequestHandled()) {
			return null;
		}

		ModelMap model = mavContainer.getModel();
		ModelAndView mav = new ModelAndView(mavContainer.getViewName(), model);

		if (!mavContainer.isViewReference()) {
			mav.setView((View) mavContainer.getView());
		}

		if (model instanceof RedirectAttributes) {
			Map<String, ?> flashAttributes = ((RedirectAttributes) model).getFlashAttributes();
			HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

			if (request != null) {
				RequestContextUtils.getOutputFlashMap(request).putAll(flashAttributes);
			}
		}

		return mav;
	}

	private ModelFactory getModelFactory(HandlerMethod handlerMethod, WebDataBinderFactory binderFactory) {
		SessionAttributesHandler sessionAttrHandler = getSessionAttributesHandler(handlerMethod);
		Class<?> handlerType = handlerMethod.getBeanType();
		Set<Method> methods = this.modelAttributeCache.get(handlerType);

		if (methods == null) {
			methods = MethodIntrospector.selectMethods(handlerType, MODEL_ATTRIBUTE_METHODS);
			this.modelAttributeCache.put(handlerType, methods);
		}

		List<InvocableHandlerMethod> attrMethods = new ArrayList<>();

		// Global methods first
		this.modelAttributeAdviceCache.forEach((clazz, methodSet) -> {

			if (clazz.isApplicableToBeanType(handlerType)) {
				Object bean = clazz.resolveBean();

				for (Method method : methodSet) {
					attrMethods.add(createModelAttributeMethod(binderFactory, bean, method));
				}
			}
		});

		for (Method method : methods) {
			Object bean = handlerMethod.getBean();
			attrMethods.add(createModelAttributeMethod(binderFactory, bean, method));
		}

		return new ModelFactory(attrMethods, binderFactory, sessionAttrHandler);
	}

	/**
	 * Return the {@link SessionAttributesHandler} instance for the given handler type (never {@code null}).
	 */
	private SessionAttributesHandler getSessionAttributesHandler(HandlerMethod handlerMethod) {
		Class<?> handlerType = handlerMethod.getBeanType();
		SessionAttributesHandler sessionAttrHandler = this.sessionAttributesHandlerCache.get(handlerType);

		if (sessionAttrHandler == null) {

			synchronized (this.sessionAttributesHandlerCache) {
				sessionAttrHandler = this.sessionAttributesHandlerCache.get(handlerType);

				if (sessionAttrHandler == null) {
					sessionAttrHandler = new SessionAttributesHandler(handlerType, this.sessionAttributeStore);
					this.sessionAttributesHandlerCache.put(handlerType, sessionAttrHandler);
				}
			}
		}

		return sessionAttrHandler;
	}

	private void initControllerAdviceCache() {

		if (getApplicationContext() == null) {
			return;
		}

		List<ControllerAdviceBean> adviceBeans = ControllerAdviceBean.findAnnotatedBeans(getApplicationContext());
		AnnotationAwareOrderComparator.sort(adviceBeans);

		List<Object> requestResponseBodyAdviceBeans = new ArrayList<>();

		for (ControllerAdviceBean adviceBean : adviceBeans) {
			Class<?> beanType = adviceBean.getBeanType();

			if (beanType == null) {
				throw new IllegalStateException("Unresolvable type for ControllerAdviceBean: " + adviceBean);
			}

			Set<Method> attrMethods = MethodIntrospector.selectMethods(beanType, MODEL_ATTRIBUTE_METHODS);

			if (!attrMethods.isEmpty()) {
				this.modelAttributeAdviceCache.put(adviceBean, attrMethods);
			}

			Set<Method> binderMethods = MethodIntrospector.selectMethods(beanType, INIT_BINDER_METHODS);

			if (!binderMethods.isEmpty()) {
				this.initBinderAdviceCache.put(adviceBean, binderMethods);
			}

			if (RequestBodyAdvice.class.isAssignableFrom(beanType)) {
				requestResponseBodyAdviceBeans.add(adviceBean);
			}

			if (ResponseBodyAdvice.class.isAssignableFrom(beanType)) {
				requestResponseBodyAdviceBeans.add(adviceBean);
			}
		}

		if (!requestResponseBodyAdviceBeans.isEmpty()) {
			this.requestResponseBodyAdvice.addAll(0, requestResponseBodyAdviceBeans);
		}

		if (logger.isDebugEnabled()) {
			int modelSize = this.modelAttributeAdviceCache.size();
			int binderSize = this.initBinderAdviceCache.size();
			int reqCount = getBodyAdviceCount(RequestBodyAdvice.class);
			int resCount = getBodyAdviceCount(ResponseBodyAdvice.class);

			if ((modelSize == 0) && (binderSize == 0) && (reqCount == 0) && (resCount == 0)) {
				logger.debug("ControllerAdvice beans: none");
			}
			else {
				logger.debug("ControllerAdvice beans: " + modelSize + " @ModelAttribute, " + binderSize +
					" @InitBinder, " + reqCount + " RequestBodyAdvice, " + resCount + " ResponseBodyAdvice");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private ModelAndView invokeHandlerMethod(PortletRequest request, PortletResponse response,
		HandlerMethod handlerMethod, ModelMap implicitModel) throws Exception {

		PortletWebRequest webRequest = new PortletWebRequest(request, response);

		try {
			WebDataBinderFactory binderFactory = getDataBinderFactory(handlerMethod);
			ModelFactory modelFactory = getModelFactory(handlerMethod, binderFactory);

			PortletInvocableHandlerMethod invocableMethod = createInvocableHandlerMethod(handlerMethod);

			if (this.argumentResolvers != null) {
				invocableMethod.setHandlerMethodArgumentResolvers(this.argumentResolvers);
			}

			if (this.returnValueHandlers != null) {
				invocableMethod.setHandlerMethodReturnValueHandlers(this.returnValueHandlers);
			}

			invocableMethod.setDataBinderFactory(binderFactory);
			invocableMethod.setParameterNameDiscoverer(this.parameterNameDiscoverer);

			ModelAndViewContainer mavContainer = new ModelAndViewContainer();
			ModelMap defaultModelMap = mavContainer.getModel();

			if (implicitModel != null) {
				defaultModelMap.putAll(implicitModel);
			}

			RenderParameters renderParameters = request.getRenderParameters();
			String sessionComplete = renderParameters.getValue(SESSION_COMPLETE_RENDER_PARAMETER);

			if (Boolean.TRUE.toString().equals(sessionComplete)) {
				mavContainer.getSessionStatus().setComplete();
			}

			modelFactory.initModel(webRequest, mavContainer, invocableMethod);
			mavContainer.setIgnoreDefaultModelOnRedirect(this.ignoreDefaultModelOnRedirect);

			invocableMethod.invokeAndHandle(webRequest, mavContainer);

			SessionStatus sessionStatus = mavContainer.getSessionStatus();

			if (sessionStatus.isComplete() && (response instanceof StateAwareResponse)) {
				StateAwareResponse stateAwareResponse = (StateAwareResponse) response;
				MutableRenderParameters mutableRenderParameters = stateAwareResponse.getRenderParameters();
				mutableRenderParameters.setValue(SESSION_COMPLETE_RENDER_PARAMETER, Boolean.TRUE.toString());
			}

			// Expose implicit model for subsequent render phase.
			if ((response instanceof StateAwareResponse) && !defaultModelMap.isEmpty()) {
				StateAwareResponse stateResponse = (StateAwareResponse) response;
				Map<?, ?> modelToStore = defaultModelMap;

				try {
					stateResponse.setRenderParameter(IMPLICIT_MODEL_RENDER_PARAMETER, Boolean.TRUE.toString());

					if (response instanceof EventResponse) {

						// Update the existing model, if any, when responding to an event -
						// whereas we're replacing the model in case of an action response.
						Map<String, Object> existingModel = (Map<String, Object>) request.getPortletSession()
							.getAttribute(IMPLICIT_MODEL_SESSION_ATTRIBUTE);

						if (existingModel != null) {
							existingModel.putAll(defaultModelMap);
							modelToStore = existingModel;
						}
					}

					request.getPortletSession().setAttribute(IMPLICIT_MODEL_SESSION_ATTRIBUTE, modelToStore);
				}
				catch (IllegalStateException ex) {
					// Probably sendRedirect called... no need to expose model to render phase.
				}
			}

			return getModelAndView(mavContainer, modelFactory, webRequest);
		}
		finally {
			webRequest.requestCompleted();
		}
	}

	/**
	 * Portlet-specific subclass of {@code HandlerMethodResolver}.
	 */
	private static class PortletHandlerMethodResolver extends HandlerMethodResolver {

		private final Map<Method, RequestMappingInfo> mappings = new HashMap<>();

		public PortletHandlerMethodResolver(Class<?> handlerType) {
			init(handlerType);
		}

		public Method resolveHandlerMethod(PortletRequest request) throws PortletException {
			Map<RequestMappingInfo, Method> targetHandlerMethods = new LinkedHashMap<RequestMappingInfo, Method>();

			for (Method handlerMethod : getHandlerMethods()) {
				RequestMappingInfo mappingInfo = this.mappings.get(handlerMethod);

				if (mappingInfo.match(request)) {
					Method oldMappedMethod = targetHandlerMethods.put(mappingInfo, handlerMethod);

					if ((oldMappedMethod != null) && (oldMappedMethod != handlerMethod)) {
						throw new IllegalStateException("Ambiguous handler methods mapped for portlet mode '" +
							request.getPortletMode() + "': {" + oldMappedMethod + ", " + handlerMethod +
							"}. If you intend to handle the same mode in multiple methods, then factor " +
							"them out into a dedicated handler class with that mode mapped at the type level!");
					}
				}
			}

			if (!targetHandlerMethods.isEmpty()) {

				if (targetHandlerMethods.size() == 1) {
					return targetHandlerMethods.values().iterator().next();
				}
				else {
					RequestMappingInfo bestMappingMatch = null;

					for (RequestMappingInfo mapping : targetHandlerMethods.keySet()) {

						if (bestMappingMatch == null) {
							bestMappingMatch = mapping;
						}
						else {

							if (mapping.isBetterMatchThan(bestMappingMatch)) {
								bestMappingMatch = mapping;
							}
						}
					}

					return targetHandlerMethods.get(bestMappingMatch);
				}
			}
			else {
				throw new NoHandlerFoundException("No matching handler method found for portlet request", request);
			}
		}

		@Override
		protected boolean isHandlerMethod(Method method) {

			if (this.mappings.containsKey(method)) {
				return true;
			}

			RequestMappingInfo mappingInfo = new RequestMappingInfo();
			ActionMapping actionMapping = AnnotationUtils.findAnnotation(method, ActionMapping.class);
			RenderMapping renderMapping = AnnotationUtils.findAnnotation(method, RenderMapping.class);
			ResourceMapping resourceMapping = AnnotationUtils.findAnnotation(method, ResourceMapping.class);
			EventMapping eventMapping = AnnotationUtils.findAnnotation(method, EventMapping.class);
			RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);

			if (actionMapping != null) {
				mappingInfo.initPhaseMapping(PortletRequest.ACTION_PHASE, actionMapping.name(), actionMapping.params());
			}

			if (renderMapping != null) {
				mappingInfo.initPhaseMapping(PortletRequest.RENDER_PHASE, renderMapping.windowState(),
					renderMapping.params());
			}

			if (resourceMapping != null) {
				mappingInfo.initPhaseMapping(PortletRequest.RESOURCE_PHASE, resourceMapping.value(), new String[0]);
			}

			if (eventMapping != null) {
				mappingInfo.initPhaseMapping(PortletRequest.EVENT_PHASE, eventMapping.value(), new String[0]);
			}

			if (requestMapping != null) {
				mappingInfo.initStandardMapping(requestMapping.value(), requestMapping.method(),
					requestMapping.params(), requestMapping.headers());

				if (mappingInfo.phase == null) {
					mappingInfo.phase = determineDefaultPhase(method);
				}
			}

			if (mappingInfo.phase != null) {
				this.mappings.put(method, mappingInfo);

				return true;
			}

			return false;
		}

		private String determineDefaultPhase(Method handlerMethod) {

			if (void.class != handlerMethod.getReturnType()) {
				return PortletRequest.RENDER_PHASE;
			}

			for (Class<?> argType : handlerMethod.getParameterTypes()) {

				if (ActionRequest.class.isAssignableFrom(argType) || ActionResponse.class.isAssignableFrom(argType) ||
						InputStream.class.isAssignableFrom(argType) || Reader.class.isAssignableFrom(argType)) {
					return PortletRequest.ACTION_PHASE;
				}
				else if (RenderRequest.class.isAssignableFrom(argType) ||
						RenderResponse.class.isAssignableFrom(argType) ||
						OutputStream.class.isAssignableFrom(argType) || Writer.class.isAssignableFrom(argType)) {
					return PortletRequest.RENDER_PHASE;
				}
				else if (ResourceRequest.class.isAssignableFrom(argType) ||
						ResourceResponse.class.isAssignableFrom(argType)) {
					return PortletRequest.RESOURCE_PHASE;
				}
				else if (EventRequest.class.isAssignableFrom(argType) ||
						EventResponse.class.isAssignableFrom(argType)) {
					return PortletRequest.EVENT_PHASE;
				}
			}

			return "";
		}
	}

	/**
	 * Holder for request mapping metadata. Allows for finding a best matching candidate.
	 */
	private static class RequestMappingInfo {

		public final Set<PortletMode> modes = new HashSet<PortletMode>();

		public String phase;

		public String value;

		public final Set<String> methods = new HashSet<String>();

		public String[] params = new String[0];

		public String[] headers = new String[0];

		@Override
		public boolean equals(Object obj) {
			RequestMappingInfo other = (RequestMappingInfo) obj;

			return (this.modes.equals(other.modes) && ObjectUtils.nullSafeEquals(this.phase, other.phase) &&
					ObjectUtils.nullSafeEquals(this.value, other.value) && this.methods.equals(other.methods) &&
					Arrays.equals(this.params, other.params) && Arrays.equals(this.headers, other.headers));
		}

		@Override
		public int hashCode() {
			return ((ObjectUtils.nullSafeHashCode(this.modes) * 29) + this.phase.hashCode());
		}

		public void initPhaseMapping(String phase, String value, String[] params) {

			if (this.phase != null) {
				throw new IllegalStateException("Invalid mapping - more than one phase specified: '" + this.phase +
					"', '" + phase + "'");
			}

			this.phase = phase;
			this.value = value;
			this.params = StringUtils.mergeStringArrays(this.params, params);
		}

		public void initStandardMapping(String[] modes, RequestMethod[] methods, String[] params, String[] headers) {

			for (String mode : modes) {
				this.modes.add(new PortletMode(mode));
			}

			for (RequestMethod method : methods) {
				this.methods.add(method.name());
			}

			this.params = StringUtils.mergeStringArrays(this.params, params);
			this.headers = StringUtils.mergeStringArrays(this.headers, headers);
		}

		public boolean isBetterMatchThan(RequestMappingInfo other) {
			return ((!this.modes.isEmpty() && other.modes.isEmpty()) ||
					(StringUtils.hasLength(this.phase) && !StringUtils.hasLength(other.phase)) ||
					(StringUtils.hasLength(this.value) && !StringUtils.hasLength(other.value)) ||
					(!this.methods.isEmpty() && other.methods.isEmpty()) || (this.params.length > other.params.length));
		}

		public boolean match(PortletRequest request) {

			if (!this.modes.isEmpty() && !this.modes.contains(request.getPortletMode())) {
				return false;
			}

			if (StringUtils.hasLength(this.phase) &&
					!this.phase.equals(request.getAttribute(PortletRequest.LIFECYCLE_PHASE))) {
				return false;
			}

			if (StringUtils.hasLength(this.value)) {

				if (this.phase.equals(PortletRequest.ACTION_PHASE) &&
						!this.value.equals(request.getParameter(ActionRequest.ACTION_NAME))) {
					return false;
				}
				else if (this.phase.equals(PortletRequest.RENDER_PHASE) &&
						!(new WindowState(this.value)).equals(request.getWindowState())) {
					return false;
				}
				else if (this.phase.equals(PortletRequest.RESOURCE_PHASE) &&
						!this.value.equals(((ResourceRequest) request).getResourceID())) {
					return false;
				}
				else if (this.phase.equals(PortletRequest.EVENT_PHASE)) {
					Event event = ((EventRequest) request).getEvent();

					if (!this.value.equals(event.getName()) && !this.value.equals(event.getQName().toString())) {
						return false;
					}
				}
			}

			return (PortletAnnotationMappingUtils.checkRequestMethod(this.methods, request) &&
					PortletAnnotationMappingUtils.checkParameters(this.params, request) &&
					PortletAnnotationMappingUtils.checkHeaders(this.headers, request));
		}
	}

}
