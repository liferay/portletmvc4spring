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
package com.liferay.portletmvc4spring.test.mock.web.portlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;

import jakarta.portlet.ActionURL;
import jakarta.portlet.CacheControl;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderURL;
import jakarta.portlet.ResourceURL;

import org.springframework.util.CollectionUtils;

import org.springframework.web.util.WebUtils;


/**
 * Mock implementation of the {@link jakarta.portlet.MimeResponse} interface.
 *
 * @author  Juergen Hoeller
 * @since   3.0
 */
public class MockMimeResponse extends MockPortletResponse implements MimeResponse {

	private PortletRequest request;

	private String contentType;

	private String characterEncoding = WebUtils.DEFAULT_CHARACTER_ENCODING;

	private PrintWriter writer;

	private Locale locale = Locale.getDefault();

	private int bufferSize = 4096;

	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	private final CacheControl cacheControl = new MockCacheControl();

	private boolean committed;

	private String includedUrl;

	private String forwardedUrl;

	/**
	 * Create a new MockMimeResponse with a default {@link MockPortalContext}.
	 *
	 * @see  com.liferay.portletmvc4spring.test.mock.web.portlet.MockPortalContext
	 */
	public MockMimeResponse() {
		super();
	}

	/**
	 * Create a new MockMimeResponse.
	 *
	 * @param  portalContext  the PortalContext defining the supported PortletModes and WindowStates
	 */
	public MockMimeResponse(PortalContext portalContext) {
		super(portalContext);
	}

	/**
	 * Create a new MockMimeResponse.
	 *
	 * @param  portalContext  the PortalContext defining the supported PortletModes and WindowStates
	 * @param  request        the corresponding render/resource request that this response is being generated for
	 */
	public MockMimeResponse(PortalContext portalContext, PortletRequest request) {
		super(portalContext);
		this.request = request;
	}

	@Override
	public <T extends PortletURL & ActionURL> T createActionURL() {
		return (T) new MockPortletURL(getPortalContext(), MockPortletURL.URL_TYPE_ACTION);
	}

	@Override
	public ActionURL createActionURL(Copy copy) {
		return new MockActionURL(getPortalContext(), copy);
	}

	@Override
	public <T extends PortletURL & RenderURL> T createRenderURL() {
		return (T) new MockPortletURL(getPortalContext(), MockPortletURL.URL_TYPE_RENDER);
	}

	@Override
	public RenderURL createRenderURL(Copy copy) {
		return new MockRenderURL(getPortalContext(), copy);
	}

	@Override
	public ResourceURL createResourceURL() {
		return new MockResourceURL();
	}

	@Override
	public void flushBuffer() {

		if (this.writer != null) {
			this.writer.flush();
		}

		if (this.outputStream != null) {

			try {
				this.outputStream.flush();
			}
			catch (IOException ex) {
				throw new IllegalStateException("Could not flush OutputStream: " + ex.getMessage());
			}
		}

		this.committed = true;
	}

	@Override
	public int getBufferSize() {
		return this.bufferSize;
	}

	@Override
	public CacheControl getCacheControl() {
		return this.cacheControl;
	}

	@Override
	public String getCharacterEncoding() {
		return this.characterEncoding;
	}

	public byte[] getContentAsByteArray() {
		flushBuffer();

		return this.outputStream.toByteArray();
	}

	public String getContentAsString() throws UnsupportedEncodingException {
		flushBuffer();

		return (this.characterEncoding != null) ? this.outputStream.toString(this.characterEncoding)
												: this.outputStream.toString();
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	public String getForwardedUrl() {
		return this.forwardedUrl;
	}

	public String getIncludedUrl() {
		return this.includedUrl;
	}

	@Override
	public Locale getLocale() {
		return this.locale;
	}

	@Override
	public OutputStream getPortletOutputStream() throws IOException {
		return this.outputStream;
	}

	@Override
	public PrintWriter getWriter() throws UnsupportedEncodingException {

		if (this.writer == null) {
			Writer targetWriter = ((this.characterEncoding != null)
					? new OutputStreamWriter(this.outputStream, this.characterEncoding)
					: new OutputStreamWriter(this.outputStream));
			this.writer = new PrintWriter(targetWriter);
		}

		return this.writer;
	}

	@Override
	public boolean isCommitted() {
		return this.committed;
	}

	@Override
	public void reset() {
		resetBuffer();
		this.characterEncoding = null;
		this.contentType = null;
		this.locale = null;
	}

	@Override
	public void resetBuffer() {

		if (this.committed) {
			throw new IllegalStateException("Cannot reset buffer - response is already committed");
		}

		this.outputStream.reset();
	}

	@Override
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public void setCommitted(boolean committed) {
		this.committed = committed;
	}

	// ---------------------------------------------------------------------
	// RenderResponse methods
	// ---------------------------------------------------------------------

	@Override
	public void setContentType(String contentType) {

		if (this.request != null) {
			Enumeration<String> supportedTypes = this.request.getResponseContentTypes();

			if (!CollectionUtils.contains(supportedTypes, contentType)) {
				throw new IllegalArgumentException("Content type [" + contentType + "] not in supported list: " +
					Collections.list(supportedTypes));
			}
		}

		this.contentType = contentType;
	}

	public void setForwardedUrl(String forwardedUrl) {
		this.forwardedUrl = forwardedUrl;
	}

	// ---------------------------------------------------------------------
	// Methods for MockPortletRequestDispatcher
	// ---------------------------------------------------------------------

	public void setIncludedUrl(String includedUrl) {
		this.includedUrl = includedUrl;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
