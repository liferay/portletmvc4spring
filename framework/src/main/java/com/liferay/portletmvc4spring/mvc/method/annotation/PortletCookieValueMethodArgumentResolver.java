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
package com.liferay.portletmvc4spring.mvc.method.annotation;

import java.net.URLDecoder;
import java.nio.charset.UnsupportedCharsetException;

import jakarta.portlet.ClientDataRequest;
import jakarta.portlet.PortletRequest;
import jakarta.servlet.http.Cookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import org.springframework.core.MethodParameter;

import org.springframework.lang.Nullable;

import org.springframework.util.Assert;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractCookieValueMethodArgumentResolver;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.liferay.portletmvc4spring.util.PortletUtils;


/**
 * An {@link AbstractCookieValueMethodArgumentResolver} that resolves cookie values from a {@link PortletRequest}.
 *
 * @author  Arjen Poutsma
 * @author  Rossen Stoyanchev
 * @author  Neil Griffin
 * @since   5.1
 */
public class PortletCookieValueMethodArgumentResolver extends AbstractCookieValueMethodArgumentResolver {

	private static final Log logger = LogFactory.getLog(PortletCookieValueMethodArgumentResolver.class);

	public PortletCookieValueMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
		super(beanFactory);
	}

	@Override
	@Nullable
	protected Object resolveName(String cookieName, MethodParameter parameter, NativeWebRequest webRequest)
		throws Exception {

		PortletRequest portletRequest = webRequest.getNativeRequest(PortletRequest.class);
		Assert.state(portletRequest != null, "No PortletRequest");

		Cookie cookieValue = PortletUtils.getCookie(portletRequest, cookieName);

		if (Cookie.class.isAssignableFrom(parameter.getNestedParameterType())) {
			return cookieValue;
		}
		else if (cookieValue != null) {
			return decodeCookieValue(portletRequest, cookieValue.getValue());
		}
		else {
			return null;
		}
	}

	private Object decodeCookieValue(PortletRequest request, String source) {
		String enc = determineEncoding(request);

		try {
			return UriUtils.decode(source, enc);
		}
		catch (UnsupportedCharsetException ex) {

			if (logger.isWarnEnabled()) {
				logger.warn("Could not decode request string [" + source + "] with encoding '" + enc +
					"': falling back to platform default encoding; exception message: " + ex.getMessage());
			}

			return URLDecoder.decode(source);
		}
	}

	private String determineEncoding(PortletRequest request) {
		String enc = null;

		if (request instanceof ClientDataRequest) {
			enc = ((ClientDataRequest) request).getCharacterEncoding();
		}

		if (enc == null) {
			enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
		}

		return enc;
	}
}
