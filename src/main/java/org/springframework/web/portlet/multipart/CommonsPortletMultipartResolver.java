/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.web.portlet.multipart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.portlet.ActionParameters;
import javax.portlet.ActionRequest;
import javax.portlet.PortalContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderParameters;
import javax.portlet.ResourceRequest;
import javax.portlet.WindowState;
import javax.servlet.http.Cookie;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.portlet.PortletFileUpload;

import org.springframework.util.Assert;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.commons.CommonsFileUploadSupport;
import org.springframework.web.portlet.context.PortletContextAware;
import org.springframework.web.portlet.util.PortletUtils;

/**
 * {@link PortletMultipartResolver} implementation for
 * <a href="http://commons.apache.org/proper/commons-fileupload">Apache Commons FileUpload</a>
 * 1.2 or above.
 *
 * <p>Provides "maxUploadSize", "maxInMemorySize" and "defaultEncoding" settings as
 * bean properties (inherited from {@link CommonsFileUploadSupport}). See corresponding
 * PortletFileUpload / DiskFileItemFactory properties ("sizeMax", "sizeThreshold",
 * "headerEncoding") for details in terms of defaults and accepted values.
 *
 * <p>Saves temporary files to the portlet container's temporary directory.
 * Needs to be initialized <i>either</i> by an application context <i>or</i>
 * via the constructor that takes a PortletContext (for standalone usage).
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see #CommonsPortletMultipartResolver(javax.portlet.PortletContext)
 * @see #setResolveLazily
 * @see org.springframework.web.multipart.commons.CommonsMultipartResolver
 * @see org.apache.commons.fileupload.portlet.PortletFileUpload
 * @see org.apache.commons.fileupload.disk.DiskFileItemFactory
 */
public class CommonsPortletMultipartResolver extends CommonsFileUploadSupport
		implements PortletMultipartResolver, PortletContextAware {

	private boolean resolveLazily = false;


	/**
	 * Constructor for use as bean. Determines the portlet container's
	 * temporary directory via the PortletContext passed in as through the
	 * PortletContextAware interface (typically by an ApplicationContext).
	 * @see #setPortletContext
	 * @see org.springframework.web.portlet.context.PortletContextAware
	 */
	public CommonsPortletMultipartResolver() {
		super();
	}

	/**
	 * Constructor for standalone usage. Determines the portlet container's
	 * temporary directory via the given PortletContext.
	 * @param portletContext the PortletContext to use
	 */
	public CommonsPortletMultipartResolver(PortletContext portletContext) {
		this();
		setPortletContext(portletContext);
	}


	/**
	 * Set whether to resolve the multipart request lazily at the time of
	 * file or parameter access.
	 * <p>Default is "false", resolving the multipart elements immediately, throwing
	 * corresponding exceptions at the time of the {@link #resolveMultipart} call.
	 * Switch this to "true" for lazy multipart parsing, throwing parse exceptions
	 * once the application attempts to obtain multipart files or parameters.
	 */
	public void setResolveLazily(boolean resolveLazily) {
		this.resolveLazily = resolveLazily;
	}

	/**
	 * Initialize the underlying {@code org.apache.commons.fileupload.portlet.PortletFileUpload}
	 * instance. Can be overridden to use a custom subclass, e.g. for testing purposes.
	 * @return the new PortletFileUpload instance
	 */
	@Override
	protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
		return new PortletFileUpload(fileItemFactory);
	}

	@Override
	public void setPortletContext(PortletContext portletContext) {
		if (!isUploadTempDirSpecified()) {
			getFileItemFactory().setRepository(PortletUtils.getTempDir(portletContext));
		}
	}


	@Override
	public boolean isMultipart(ActionRequest request) {
		return (request != null && PortletFileUpload.isMultipartContent(request));
	}

	/**
	 * @since 5.1
	 */
	@Override
	public boolean isMultipart(ResourceRequest request) {
		return (request != null && PortletFileUpload.isMultipartContent(new ActionRequestAdapter(request)));
	}

	@Override
	public MultipartActionRequest resolveMultipart(final ActionRequest request) throws MultipartException {
		Assert.notNull(request, "Request must not be null");
		if (this.resolveLazily) {
			return new DefaultMultipartActionRequest(request) {
				@Override
				protected void initializeMultipart() {
					MultipartParsingResult parsingResult = parseRequest(request);
					setMultipartFiles(parsingResult.getMultipartFiles());
					setMultipartParameters(parsingResult.getMultipartParameters());
					setMultipartParameterContentTypes(parsingResult.getMultipartParameterContentTypes());
				}
			};
		}
		else {
			MultipartParsingResult parsingResult = parseRequest(request);
			return new DefaultMultipartActionRequest(request, parsingResult.getMultipartFiles(),
					parsingResult.getMultipartParameters(), parsingResult.getMultipartParameterContentTypes());
		}
	}

	/**
	 * @since 5.1
	 */
	@Override
	public MultipartResourceRequest resolveMultipart(final ResourceRequest request) throws MultipartException {
		Assert.notNull(request, "Request must not be null");
		if (this.resolveLazily) {
			return new DefaultMultipartResourceRequest(request) {
				@Override
				protected void initializeMultipart() {
					MultipartParsingResult parsingResult = parseRequest(request);
					setMultipartFiles(parsingResult.getMultipartFiles());
					setMultipartParameters(parsingResult.getMultipartParameters());
					setMultipartParameterContentTypes(parsingResult.getMultipartParameterContentTypes());
				}
			};
		}
		else {
			MultipartParsingResult parsingResult = parseRequest(request);
			return new DefaultMultipartResourceRequest(request, parsingResult.getMultipartFiles(),
					parsingResult.getMultipartParameters(), parsingResult.getMultipartParameterContentTypes());
		}
	}

	/**
	 * Parse the given portlet request, resolving its multipart elements.
	 * @param request the request to parse
	 * @return the parsing result
	 * @throws MultipartException if multipart resolution failed.
	 */
	protected MultipartParsingResult parseRequest(ActionRequest request) throws MultipartException {
		String encoding = determineEncoding(request);
		FileUpload fileUpload = prepareFileUpload(encoding);
		try {
			List<FileItem> fileItems = ((PortletFileUpload) fileUpload).parseRequest(request);
			return parseFileItems(fileItems, encoding);
		}
		catch (FileUploadBase.SizeLimitExceededException ex) {
			throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
		}
		catch (FileUploadException ex) {
			throw new MultipartException("Could not parse multipart portlet request", ex);
		}
	}

	/**
	 * Parse the given portlet request, resolving its multipart elements.
	 * @param request the request to parse
	 * @return the parsing result
	 * @throws MultipartException if multipart resolution failed.
	 * @since 5.1
	 */
	protected MultipartParsingResult parseRequest(ResourceRequest request) throws MultipartException {
		String encoding = determineEncoding(request);
		FileUpload fileUpload = prepareFileUpload(encoding);
		try {
			List<FileItem> fileItems = ((PortletFileUpload) fileUpload).parseRequest(new ActionRequestAdapter(request));
			return parseFileItems(fileItems, encoding);
		}
		catch (FileUploadBase.SizeLimitExceededException ex) {
			throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
		}
		catch (FileUploadException ex) {
			throw new MultipartException("Could not parse multipart portlet request", ex);
		}
	}

	/**
	 * Determine the encoding for the given request.
	 * Can be overridden in subclasses.
	 * <p>The default implementation checks the request encoding,
	 * falling back to the default encoding specified for this resolver.
	 * @param request current portlet request
	 * @return the encoding for the request (never {@code null})
	 * @see javax.portlet.ActionRequest#getCharacterEncoding
	 * @see #setDefaultEncoding
	 */
	protected String determineEncoding(ActionRequest request) {
		String encoding = request.getCharacterEncoding();
		if (encoding == null) {
			encoding = getDefaultEncoding();
		}
		return encoding;
	}

	/**
	 * Determine the encoding for the given request.
	 * Can be overridden in subclasses.
	 * <p>The default implementation checks the request encoding,
	 * falling back to the default encoding specified for this resolver.
	 * @param request current portlet request
	 * @return the encoding for the request (never {@code null})
	 * @see javax.portlet.ResourceRequest#getCharacterEncoding
	 * @see #setDefaultEncoding
	 * @since 5.1
	 */
	protected String determineEncoding(ResourceRequest request) {
		String encoding = request.getCharacterEncoding();
		if (encoding == null) {
			encoding = getDefaultEncoding();
		}
		return encoding;
	}

	@Override
	public void cleanupMultipart(MultipartActionRequest request) {
		if (request != null) {
			try {
				cleanupFileItems(request.getMultiFileMap());
			}
			catch (Throwable ex) {
				logger.warn("Failed to perform multipart cleanup for portlet request", ex);
			}
		}
	}

	/**
	 * @param request the request to cleanup resources for
	 * @since 5.1
	 */
	@Override
	public void cleanupMultipart(MultipartResourceRequest request) {
		if (request != null) {
			try {
				cleanupFileItems(request.getMultiFileMap());
			}
			catch (Throwable ex) {
				logger.warn("Failed to perform multipart cleanup for portlet request", ex);
			}
		}
	}

	/**
	 * Since {@link PortletFileUpload#parseRequest(ActionRequest)} only works with {@link ActionRequest}, this adapter
	 * class is necessary to force commons-fileupload to work with ResourceRequest (Ajax file upload).
	 *
	 * @author  Neil Griffin
	 * @since 5.1
	 */
	private static class ActionRequestAdapter implements ActionRequest {

		private ResourceRequest resourceRequest;

		public ActionRequestAdapter(ResourceRequest resourceRequest) {
			this.resourceRequest = resourceRequest;
		}

		@Override
		public Object getAttribute(String name) {
			return resourceRequest.getAttribute(name);
		}

		@Override
		public Enumeration<String> getAttributeNames() {
			return resourceRequest.getAttributeNames();
		}

		@Override
		public String getAuthType() {
			return resourceRequest.getAuthType();
		}

		@Override
		public String getCharacterEncoding() {
			return resourceRequest.getCharacterEncoding();
		}

		@Override
		public int getContentLength() {
			return resourceRequest.getContentLength();
		}

		@Override
		public long getContentLengthLong() {
			return resourceRequest.getContentLengthLong();
		}

		@Override
		public String getContentType() {
			return resourceRequest.getContentType();
		}

		@Override
		public String getContextPath() {
			return resourceRequest.getContextPath();
		}

		@Override
		public Cookie[] getCookies() {
			return resourceRequest.getCookies();
		}

		@Override
		public Locale getLocale() {
			return resourceRequest.getLocale();
		}

		@Override
		public Enumeration<Locale> getLocales() {
			return resourceRequest.getLocales();
		}

		@Override
		public String getMethod() {
			return resourceRequest.getMethod();
		}

		@Override
		public Part getPart(String name) throws IOException, PortletException {
			return resourceRequest.getPart(name);
		}

		@Override
		public Collection<Part> getParts()
			throws IOException, PortletException {
			return resourceRequest.getParts();
		}

		@Override
		public String getParameter(String name) {
			return resourceRequest.getParameter(name);
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return resourceRequest.getParameterMap();
		}

		@Override
		public Enumeration<String> getParameterNames() {
			return resourceRequest.getParameterNames();
		}

		@Override
		public String[] getParameterValues(String name) {
			return resourceRequest.getParameterValues(name);
		}

		@Override
		public PortalContext getPortalContext() {
			return resourceRequest.getPortalContext();
		}

		@Override
		public PortletContext getPortletContext() {
			return resourceRequest.getPortletContext();
		}

		@Override
		public InputStream getPortletInputStream() throws IOException {
			return resourceRequest.getPortletInputStream();
		}

		@Override
		public RenderParameters getRenderParameters() {
			return resourceRequest.getRenderParameters();
		}

		@Override
		public PortletMode getPortletMode() {
			return resourceRequest.getPortletMode();
		}

		@Override
		public PortletSession getPortletSession() {
			return resourceRequest.getPortletSession();
		}

		@Override
		public PortletSession getPortletSession(boolean create) {
			return resourceRequest.getPortletSession();
		}

		@Override
		public PortletPreferences getPreferences() {
			return resourceRequest.getPreferences();
		}

		@Override
		public Map<String, String[]> getPrivateParameterMap() {
			return resourceRequest.getPrivateParameterMap();
		}

		@Override
		public Enumeration<String> getProperties(String name) {
			return resourceRequest.getProperties(name);
		}

		@Override
		public String getProperty(String name) {
			return resourceRequest.getProperty(name);
		}

		@Override
		public Enumeration<String> getPropertyNames() {
			return resourceRequest.getPropertyNames();
		}

		@Override
		public Map<String, String[]> getPublicParameterMap() {
			return resourceRequest.getPublicParameterMap();
		}

		@Override
		public String getUserAgent() {
			return resourceRequest.getUserAgent();
		}

		@Override
		public BufferedReader getReader() throws IOException {
			return resourceRequest.getReader();
		}

		@Override
		public String getRemoteUser() {
			return resourceRequest.getRemoteUser();
		}

		@Override
		public String getRequestedSessionId() {
			return resourceRequest.getRequestedSessionId();
		}

		@Override
		public String getResponseContentType() {
			return resourceRequest.getResponseContentType();
		}

		@Override
		public Enumeration<String> getResponseContentTypes() {
			return resourceRequest.getResponseContentTypes();
		}

		@Override
		public String getScheme() {
			return resourceRequest.getScheme();
		}

		@Override
		public String getServerName() {
			return resourceRequest.getServerName();
		}

		@Override
		public int getServerPort() {
			return resourceRequest.getServerPort();
		}

		@Override
		public Principal getUserPrincipal() {
			return resourceRequest.getUserPrincipal();
		}

		@Override
		public String getWindowID() {
			return resourceRequest.getWindowID();
		}

		@Override
		public WindowState getWindowState() {
			return resourceRequest.getWindowState();
		}

		@Override
		public boolean isPortletModeAllowed(PortletMode mode) {
			return resourceRequest.isPortletModeAllowed(mode);
		}

		@Override
		public boolean isRequestedSessionIdValid() {
			return resourceRequest.isRequestedSessionIdValid();
		}

		@Override
		public boolean isSecure() {
			return resourceRequest.isSecure();
		}

		@Override
		public boolean isUserInRole(String role) {
			return resourceRequest.isUserInRole(role);
		}

		@Override
		public boolean isWindowStateAllowed(WindowState state) {
			return resourceRequest.isWindowStateAllowed(state);
		}

		@Override
		public void removeAttribute(String name) {
			resourceRequest.removeAttribute(name);
		}

		@Override
		public void setAttribute(String name, Object value) {
			resourceRequest.setAttribute(name, value);
		}

		@Override
		public void setCharacterEncoding(String enc) throws
			UnsupportedEncodingException {
			resourceRequest.setCharacterEncoding(enc);
		}

		@Override
		public ActionParameters getActionParameters() {
			return null;
		}
	}
}
