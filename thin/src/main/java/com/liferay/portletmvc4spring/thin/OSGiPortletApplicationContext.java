/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.portletmvc4spring.thin;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import org.springframework.web.portlet.context.XmlPortletApplicationContext;


/**
 * @author Raymond Augé
 */
public class OSGiPortletApplicationContext extends XmlPortletApplicationContext {

	@Override
	protected ResourcePatternResolver getResourcePatternResolver() {
		return new OSGiPathMatchingResourcePatternResolver(this);
	}

	private class OSGiPathMatchingResourcePatternResolver extends PathMatchingResourcePatternResolver {

		public OSGiPathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {

			super(resourceLoader);
		}

		@Override
		protected Resource resolveRootDirResource(Resource original) throws IOException {

			URL url = original.getURL();
			String protocol = url.getProtocol();

			if (protocol.startsWith("bundle")) {
				return new UrlResource(FileLocator.resolve(url));
			}

			return original;
		}
	}

}
