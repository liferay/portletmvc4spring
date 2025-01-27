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
package com.liferay.portletmvc4spring.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;


/**
 * In order to enable CSRF protection, it is necessary to specify this class in a component-scan or register it in the
 * WEB-INF/spring-context/portlet-application-context.xml descriptor. For example:
 *
 * <pre>
 {@code
 <bean id="springSecurityPortletConfigurer" class="com.liferay.portletmvc4spring.security.SpringSecurityPortletConfigurer" />
 <bean id="delegatingFilterProxy" class="org.springframework.web.filter.DelegatingFilterProxy">
    <property name="targetBeanName" value="springSecurityFilterChain" />
 </bean>
 }
 * </pre>
 *
 * It is also necessary to register the {@link SpringSecurityPortletFilter} in the WEB-INF/portlet.xml descriptor. For
 * example:
 *
 * <pre>
 {@code
 <portlet>
     <filter>
         <filter-name>SpringSecurityPortletFilter</filter-name>
         <filter-class>com.liferay.portletmvc4spring.security.SpringSecurityPortletFilter</filter-class>
         <lifecycle>ACTION_PHASE</lifecycle>
         <lifecycle>RENDER_PHASE</lifecycle>
         <lifecycle>RESOURCE_PHASE</lifecycle>
     </filter>
     <filter-mapping>
         <filter-name>SpringSecurityPortletFilter</filter-name>
         <portlet-name>portlet1</portlet-name>
     </filter-mapping>
 </portlet>
 }
 * </pre>
 *
 * Finally, it is necessary to specify the following in the WEB-INF/web.xml descriptor:
 *
 * <pre>
{@code
<filter>
    <filter-name>delegatingFilterProxy</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
</filter>
<filter-mapping>
    <filter-name>delegatingFilterProxy</filter-name>
    <url-pattern>/WEB-INF/servlet/view</url-pattern>
    <dispatcher>FORWARD</dispatcher>
    <dispatcher>INCLUDE</dispatcher>
</filter-mapping>
}
 * </pre>
 *
 * @author  Neil Griffin
 */
@EnableWebSecurity
@Configuration
public class SpringSecurityPortletConfigurer {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable()) // Disable CSRF or customize as necessary for portlets
			.exceptionHandling(exceptionHandling ->
				exceptionHandling.accessDeniedHandler(new PortletAccessDeniedHandler())
			);

		return http.build();
	}

	private static class PortletAccessDeniedHandler implements AccessDeniedHandler {

		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response,
						   AccessDeniedException accessDeniedException) throws IOException, ServletException {
			throw accessDeniedException;
		}
	}
}