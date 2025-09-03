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

import jakarta.portlet.ActionRequest;

import org.springframework.web.multipart.MultipartRequest;


/**
 * Interface which provides additional methods for dealing with multipart content within a portlet request, allowing to
 * access uploaded files. Implementations also need to override the standard ActionRequest methods for parameter access,
 * making multipart parameters available.
 *
 * <p>A concrete implementation is {@link DefaultMultipartActionRequest}.
 *
 * @author  Juergen Hoeller
 * @since   2.0
 * @see     PortletMultipartResolver
 * @see     org.springframework.web.multipart.MultipartFile
 * @see     jakarta.portlet.ActionRequest#getParameter
 * @see     jakarta.portlet.ActionRequest#getParameterNames
 * @see     jakarta.portlet.ActionRequest#getParameterMap
 */
public interface MultipartActionRequest extends ActionRequest, MultipartRequest {

}
