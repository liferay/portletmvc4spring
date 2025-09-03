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
package com.liferay.portletmvc4spring.handler;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import com.liferay.portletmvc4spring.context.PortletApplicationObjectSupport;


/**
 * Convenient superclass for any kind of web content generator, like {@link
 * com.liferay.portletmvc4spring.mvc.AbstractController}. Can also be used for custom handlers that have their own
 * {@link com.liferay.portletmvc4spring.HandlerAdapter}.
 *
 * <p>Supports portlet cache control options.
 *
 * @author  Juergen Hoeller
 * @author  John A. Lewis
 * @since   2.0
 * @see     #setCacheSeconds
 * @see     #setRequireSession
 */
public abstract class PortletContentGenerator extends PortletApplicationObjectSupport {

	private boolean requireSession = false;

	private int cacheSeconds = -1;

	/**
	 * Return the number of seconds that content is cached.
	 */
	public final int getCacheSeconds() {
		return this.cacheSeconds;
	}

	/**
	 * Return whether a session is required to handle requests.
	 */
	public final boolean isRequireSession() {
		return this.requireSession;
	}

	/**
	 * Cache content for the given number of seconds. Default is -1, indicating no override of portlet content caching.
	 *
	 * <p>Only if this is set to 0 (no cache) or a positive value (cache for this many seconds) will this class override
	 * the portlet settings.
	 *
	 * <p>The cache setting can be overwritten by subclasses, before content is generated.
	 */
	public final void setCacheSeconds(int seconds) {
		this.cacheSeconds = seconds;
	}

	/**
	 * Set whether a session should be required to handle requests.
	 */
	public final void setRequireSession(boolean requireSession) {
		this.requireSession = requireSession;
	}

	/**
	 * Apply the given cache seconds to the render response
	 *
	 * @param  response  current portlet render response
	 * @param  seconds   positive number of seconds into the future that the response should be cacheable for, 0 to
	 *                   prevent caching
	 */
	protected final void applyCacheSeconds(MimeResponse response, int seconds) {

		if (seconds > 0) {
			cacheForSeconds(response, seconds);
		}
		else if (seconds == 0) {
			preventCaching(response);
		}
		// Leave caching to the portlet configuration otherwise.
	}

	/**
	 * Set portlet response to allow caching for the given number of seconds.
	 *
	 * @param  response  current portlet render response
	 * @param  seconds   number of seconds into the future that the response should be cacheable for
	 */
	protected final void cacheForSeconds(MimeResponse response, int seconds) {
		response.setProperty(MimeResponse.EXPIRATION_CACHE, Integer.toString(seconds));
	}

	/**
	 * Check and prepare the given request and response according to the settings of this generator. Checks for a
	 * required session, and applies the number of cache seconds configured for this generator (if it is a render
	 * request/response).
	 *
	 * @param   request   current portlet request
	 * @param   response  current portlet response
	 *
	 * @throws  PortletException  if the request cannot be handled because a check failed
	 */
	protected final void check(PortletRequest request, PortletResponse response) throws PortletException {

		if (this.requireSession) {

			if (request.getPortletSession(false) == null) {
				throw new PortletSessionRequiredException("Pre-existing session required but none found");
			}
		}
	}

	/**
	 * Check and prepare the given request and response according to the settings of this generator. Checks for a
	 * required session, and applies the number of cache seconds configured for this generator (if it is a render
	 * request/response).
	 *
	 * @param   request   current portlet request
	 * @param   response  current portlet response
	 *
	 * @throws  PortletException  if the request cannot be handled because a check failed
	 */
	protected final void checkAndPrepare(PortletRequest request, MimeResponse response) throws PortletException {

		checkAndPrepare(request, response, this.cacheSeconds);
	}

	/**
	 * Check and prepare the given request and response according to the settings of this generator. Checks for a
	 * required session, and applies the given number of cache seconds (if it is a render request/response).
	 *
	 * @param   request       current portlet request
	 * @param   response      current portlet response
	 * @param   cacheSeconds  positive number of seconds into the future that the response should be cacheable for, 0 to
	 *                        prevent caching
	 *
	 * @throws  PortletException  if the request cannot be handled because a check failed
	 */
	protected final void checkAndPrepare(PortletRequest request, MimeResponse response, int cacheSeconds)
		throws PortletException {

		check(request, response);
		applyCacheSeconds(response, cacheSeconds);
	}

	/**
	 * Prevent the render response from being cached.
	 */
	protected final void preventCaching(MimeResponse response) {
		cacheForSeconds(response, 0);
	}

}
