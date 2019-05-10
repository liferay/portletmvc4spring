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

public class OSGiPortletApplicationContext
	extends XmlPortletApplicationContext {

	@Override
	protected ResourcePatternResolver getResourcePatternResolver() {
		return new OSGiPathMatchingResourcePatternResolver(this);
	}

	private class OSGiPathMatchingResourcePatternResolver
		extends PathMatchingResourcePatternResolver {

		public OSGiPathMatchingResourcePatternResolver(
			ResourceLoader resourceLoader) {

			super(resourceLoader);
		}

		@Override
		protected Resource resolveRootDirResource(Resource original)
			throws IOException {

			URL url = original.getURL();
			String protocol = url.getProtocol();

			if (protocol.startsWith("bundle")) {
				return new UrlResource(FileLocator.resolve(url));
			}

			return original;
		}
	}

}
