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
package com.liferay.portletmvc4spring.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ClientDataRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.servlet.http.Part;

import org.apache.commons.logging.LogFactory;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;

import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;


/**
 * Standard implementation of the {@link MultipartResolver} interface, based on the Servlet 3.0 {@link
 * jakarta.servlet.http.Part} API. To be added as "portletMultipartResolver" bean to a Spring DispatcherServlet context,
 * without any extra configuration at the bean level (see below).
 *
 * <p><b>Note:</b> In order to use Portlet 3.0 based multipart parsing, you need to mark the affected servlet with a
 * "multipart-config" section in {@code portlet.xml}, or (in case of a custom portlet class) possibly with a {@link
 * jakarta.portlet.annotations.Multipart} annotation on your portlet class. Configuration settings such as maximum sizes
 * or storage locations need to be applied at that portlet registration level.
 *
 * @author  Juergen Hoeller
 * @author  Neil Griffin
 * @since   5.1.0
 * @see     #setResolveLazily
 * @see     jakarta.portlet.ClientDataRequest#getParts()
 * @see     org.springframework.web.multipart.commons.CommonsMultipartResolver
 */
public class StandardPortletMultipartResolver implements PortletMultipartResolver {

	private boolean resolveLazily = false;

	@Override
	public void cleanupMultipart(MultipartActionRequest multipartActionRequest) {
		_cleanupMultipart(multipartActionRequest);
	}

	@Override
	public void cleanupMultipart(MultipartResourceRequest multipartResourceRequest) {
		_cleanupMultipart(multipartResourceRequest);
	}

	@Override
	public boolean isMultipart(ActionRequest actionRequest) {
		return _isMultipart(actionRequest);
	}

	@Override
	public boolean isMultipart(ResourceRequest resourceRequest) {
		return _isMultipart(resourceRequest);
	}

	@Override
	public MultipartActionRequest resolveMultipart(ActionRequest actionRequest) throws MultipartException {
		Assert.notNull(actionRequest, "ActionRequest must not be null");

		if (this.resolveLazily) {
			final ActionRequest request = actionRequest;

			return new DefaultMultipartActionRequest(actionRequest) {
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
			MultipartParsingResult parsingResult = parseRequest(actionRequest);

			return new DefaultMultipartActionRequest(actionRequest, parsingResult.getMultipartFiles(),
					parsingResult.getMultipartParameters(), parsingResult.getMultipartParameterContentTypes());
		}
	}

	@Override
	public MultipartResourceRequest resolveMultipart(ResourceRequest resourceRequest) throws MultipartException {
		Assert.notNull(resourceRequest, "ResourceRequest must not be null");

		if (this.resolveLazily) {
			final ResourceRequest request = resourceRequest;

			return new DefaultMultipartResourceRequest(resourceRequest) {
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
			MultipartParsingResult parsingResult = parseRequest(resourceRequest);

			return new DefaultMultipartResourceRequest(resourceRequest, parsingResult.getMultipartFiles(),
					parsingResult.getMultipartParameters(), parsingResult.getMultipartParameterContentTypes());
		}
	}

	/**
	 * Set whether to resolve the multipart request lazily at the time of file or parameter access.
	 *
	 * <p>Default is "false", resolving the multipart elements immediately, throwing corresponding exceptions at the
	 * time of the {@link #resolveMultipart} call. Switch this to "true" for lazy multipart parsing, throwing parse
	 * exceptions once the application attempts to obtain multipart files or parameters.
	 */
	public void setResolveLazily(boolean resolveLazily) {
		this.resolveLazily = resolveLazily;
	}

	protected String filterFilename(String fileName) {

		// http://technet.microsoft.com/en-us/library/cc956689.aspx
		String filteredFilename = fileName;

		if (fileName != null) {

			int pos = fileName.lastIndexOf(".");
			filteredFilename = fileName.replaceAll("[\\\\/\\[\\]:|<>+;=.?\"]", "-");

			if (pos > 0) {
				filteredFilename = filteredFilename.substring(0, pos) + "." + filteredFilename.substring(pos + 1);
			}
		}

		return filteredFilename;
	}

	/**
	 * Parse the given portlet actionRequest, resolving its multipart elements.
	 *
	 * @param   clientDataRequest  the request to parse
	 *
	 * @return  the parsing result
	 *
	 * @throws  MultipartException  if multipart resolution failed.
	 */
	protected MultipartParsingResult parseRequest(ClientDataRequest clientDataRequest) throws MultipartException {

		MultiValueMap<String, MultipartFile> multipartFiles = new LinkedMultiValueMap<>();
		Map<String, String[]> multipartParameters = new HashMap<>();
		Map<String, String> multipartParameterContentTypes = new HashMap<>();

		Collection<Part> parts = null;

		try {
			parts = clientDataRequest.getParts();

			for (Part part : parts) {
				String headerValue = part.getHeader(HttpHeaders.CONTENT_DISPOSITION);
				ContentDisposition disposition = ContentDisposition.parse(headerValue);
				String filename = disposition.getFilename();

				if (filename != null) {

					if (filename.startsWith("=?") && filename.endsWith("?=")) {
						filename = filterFilename(filename);
					}

					multipartFiles.add(part.getName(), new StandardPortletMultipartFile(part, filename));
				}
			}
		}
		catch (Exception e) {
			throw new MultipartException(e.getMessage(), e);
		}

		return new MultipartParsingResult(multipartFiles, multipartParameters, multipartParameterContentTypes);
	}

	private void _cleanupMultipart(ClientDataRequest clientDataRequest) {

		// To be on the safe side: explicitly delete the parts,
		// but only actual file parts (for Resin compatibility)
		try {
			MultipartRequest multipartRequest = (MultipartRequest) clientDataRequest;

			for (Part part : clientDataRequest.getParts()) {

				if (multipartRequest.getFile(part.getName()) != null) {
					part.delete();
				}
			}
		}
		catch (Throwable ex) {
			LogFactory.getLog(getClass()).warn("Failed to perform cleanup of multipart items", ex);
		}
	}

	private boolean _isMultipart(ClientDataRequest clientDataRequest) {

		// Same check as in Commons FileUpload...
		if (!"post".equalsIgnoreCase(clientDataRequest.getMethod())) {
			return false;
		}

		String contentType = clientDataRequest.getContentType();

		return StringUtils.startsWithIgnoreCase(contentType, "multipart/");
	}

	/**
	 * Holder for a Map of Spring MultipartFiles and a Map of multipart parameters.
	 */
	protected static class MultipartParsingResult {

		private final MultiValueMap<String, MultipartFile> multipartFiles;

		private final Map<String, String[]> multipartParameters;

		private final Map<String, String> multipartParameterContentTypes;

		public MultipartParsingResult(MultiValueMap<String, MultipartFile> multipartFiles,
			Map<String, String[]> multipartParameters, Map<String, String> multipartParameterContentTypes) {

			this.multipartFiles = multipartFiles;
			this.multipartParameters = multipartParameters;
			this.multipartParameterContentTypes = multipartParameterContentTypes;
		}

		public MultiValueMap<String, MultipartFile> getMultipartFiles() {
			return this.multipartFiles;
		}

		public Map<String, String> getMultipartParameterContentTypes() {
			return this.multipartParameterContentTypes;
		}

		public Map<String, String[]> getMultipartParameters() {
			return this.multipartParameters;
		}
	}

	private static class StandardPortletMultipartFile implements MultipartFile, Serializable {

		private final Part part;

		private final String filename;

		public StandardPortletMultipartFile(Part part, String filename) {
			this.part = part;
			this.filename = filename;
		}

		@Override
		public byte[] getBytes() throws IOException {
			return FileCopyUtils.copyToByteArray(this.part.getInputStream());
		}

		@Override
		public String getContentType() {
			return this.part.getContentType();
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return this.part.getInputStream();
		}

		@Override
		public String getName() {
			return this.part.getName();
		}

		@Override
		public String getOriginalFilename() {
			return this.filename;
		}

		@Override
		public long getSize() {
			return this.part.getSize();
		}

		@Override
		public boolean isEmpty() {
			return (this.part.getSize() == 0);
		}

		@Override
		public void transferTo(File dest) throws IOException, IllegalStateException {
			this.part.write(dest.getPath());

			if (dest.isAbsolute() && !dest.exists()) {

				// Servlet 3.0 Part.write is not guaranteed to support absolute file paths:
				// may translate the given path to a relative location within a temp dir
				// (e.g. on Jetty whereas Tomcat and Undertow detect absolute paths).
				// At least we offloaded the file from memory storage; it'll get deleted
				// from the temp dir eventually in any case. And for our user's purposes,
				// we can manually copy it to the requested location as a fallback.
				FileCopyUtils.copy(this.part.getInputStream(), Files.newOutputStream(dest.toPath()));
			}
		}
	}
}
