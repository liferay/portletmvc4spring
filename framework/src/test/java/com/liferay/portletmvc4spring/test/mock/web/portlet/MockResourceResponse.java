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

import jakarta.portlet.ResourceResponse;


/**
 * Mock implementation of the {@link jakarta.portlet.ResourceResponse} interface.
 *
 * @author  Juergen Hoeller
 * @since   3.0
 */
public class MockResourceResponse extends MockMimeResponse implements ResourceResponse {

	private long contentLength;

	private int status;

	public int getContentLength() {
		return (int) this.contentLength;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void setContentLength(int len) {
		this.contentLength = len;
	}

	@Override
	public void setContentLengthLong(long contentLengthLong) {
		this.contentLength = contentLengthLong;
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}

}
