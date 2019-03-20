# PortletMVC4Spring

The PortletMVC4Spring project began as Spring Portlet MVC and was part of the
[Spring Framework](https://spring.io/projects/spring-framework). When the project was pruned from version 5.0.x of the
Spring Framework under [SPR-14129](https://github.com/spring-projects/spring-framework/issues/18701), it became
necessary to fork and rename the project. This made it possible to improve and maintain the project for compatibility
with the latest versions of the Spring Framework and the Portlet API.

[Liferay, Inc.](http://www.liferay.com) adopted Spring Portlet MVC in March of 2019 and the project was renamed to
**PortletMVC4Spring**.

## Documentation

* [Developer Guide](framework/src/main/asciidoc/portletmvc4spring.adoc)
* [Javadoc Reference](https://liferay.github.io/portletmvc4spring/apidocs/index.html)

## Library Modules

| Module | Description |
| ------ | ----------- |
| [com.liferay.portletmvc4spring.framework](framework) | Provides the Model/View/Controller (MVC) portlet framework. |
| [com.liferay.portletmvc4spring.security](security) | Provides convenience and utility classes that help support [Cross-Site Request Forgery (CSRF)](https://www.owasp.org/index.php/Cross-Site_Request_Forgery_(CSRF)) protection provided by [Spring Security](https://spring.io/projects/spring-security). |

## Requirements

PortletMVC4Spring requires JDK 8+ and has been upgraded from version 2.0 of the Portlet API to version 3.0. In addition,
it has been refactored and tested for use with version 5.1.x of the Spring Framework.

## Supported Portals

PortletMVC4Spring is supported in the following Portlet 3.0 compliant portals:
- [Liferay Portal 7.1+](https://community.liferay.com/-/portal)
- [Apache Pluto 3.0+](https://portals.apache.org/pluto/)

## Version Scheme

PortletMVC4Spring follows a `major.minor.patch` version scheme. The `major` and `minor` version numbers correspond to
Spring Framework versions for which PortletMVC4Spring is compatible. For example, version 5.1.x of PortletMVC4Spring is
intended for use with version 5.1.x of the Spring Framework. The `patch` number indicates an incremental version
containing patches for software defects.

## Dependency Coordinates

**Maven:**

	<dependencies>
		<dependency>
			<groupId>com.liferay.portletmvc4spring</groupId>
			<artifactId>com.liferay.portletmvc4spring.framework</artifactId>
			<version>5.1.0</version>	
		</dependency>
		<dependency>
			<groupId>com.liferay.portletmvc4spring</groupId>
			<artifactId>com.liferay.portletmvc4spring.security</artifactId>
			<version>5.1.0</version>	
		</dependency>
	<dependencies>
	
**Gradle:**

	dependencies {
		compile group: 'com.liferay.portletmvc4spring', name: 'com.liferay.portletmvc4spring.framework', version: '5.1.0'
		compile group: 'com.liferay.portletmvc4spring', name: 'com.liferay.portletmvc4spring.security', version: '5.1.0'
	}

## Archetypes

	# JSP Form
	mvn archetype:generate \
		-DarchetypeGroupId=com.liferay.portletmvc4spring.archetype \
		-DarchetypeArtifactId=com.liferay.portletmvc4spring.archetype.form.jsp.portlet \
		-DarchetypeVersion=5.1.0 \
		-DgroupId=com.mycompany \
		-DartifactId=com.mycompany.my.form.jsp.portlet \

	# Thymeleaf Form
	mvn archetype:generate \
		-DarchetypeGroupId=com.liferay.portletmvc4spring.archetype \
		-DarchetypeArtifactId=com.liferay.portletmvc4spring.archetype.form.thymeleaf.portlet \
		-DarchetypeVersion=5.1.0 \
		-DgroupId=com.mycompany \
		-DartifactId=com.mycompany.my.form.thymeleaf.portlet \

## Demos

There are two demos available that have been tested in both Liferay Portal and Apache Pluto:

| Source Code   | Maven Central |
| ------------- | ------------- |
| [applicant-jsp-portlet](demo/applicant-jsp-portlet)  |  [com.liferay.portletmvc4spring.demo.applicant.jsp.portlet.war](https://search.maven.org/search?q=a:com.liferay.portletmvc4spring.demo.applicant.jsp.portlet) |
| [applicant-thymeleaf-portlet](demo/applicant-thymeleaf-portlet)  |  [com.liferay.portletmvc4spring.demo.applicant.thymeleaf.portlet.war](https://search.maven.org/search?q=a:com.liferay.portletmvc4spring.demo.applicant.thymeleaf.portlet) |

The demos exercise **many** of the features of PortletMVC4Spring that developers typically need for form-based portlet
applications. The demos are **identical** _except_ that one uses JSPX views and the other uses
[Thymeleaf](https://www.thymeleaf.org) views.

## Migration

If you are migrating a portlet project from Spring Portlet MVC to PortletMVC4Spring, then follow these steps:

1. Upgrade your pom.xml or build.gradle descriptor to version 5.1.x of the Spring Framework.

2. Replace the `spring-webmvc-portlet` dependency in your pom.xml or build.gradle descriptor with the
`com.liferay.portletmvc4spring.framework` dependency. For more information about adding dependencies, see
[Dependency Coordinates](#dependency-coordinates).

3. Replace `org.springframework.web.portlet.DispatcherPortlet` with `com.liferay.portletmvc4spring.DispatcherPortlet`
in your WEB-INF/portlet.xml descriptor.

4. The Spring Portlet MVC
[AnnotationMethodHandlerAdapter](https://docs.spring.io/spring-framework/docs/4.3.4.RELEASE/javadoc-api/org/springframework/web/portlet/mvc/annotation/AnnotationMethodHandlerAdapter.html)
class was replaced with the new PortletMVC4Spring
[PortletRequestMappingHandlerAdapter](https://liferay.github.io/portletmvc4spring/apidocs/com/liferay/portletmvc4spring/mvc/method/annotation/PortletRequestMappingHandlerAdapter.html)
class in order to utilize the modern-day HandlerMethod infrastructure that
[Spring Web MVC](https://docs.spring.io/spring/docs/5.1.x/spring-framework-reference/web.html#spring-web) is based on.

   If you specified `AnnotationMethodHandlerAdapter` as a `<bean>` in a Spring configuration descriptor, then you will
need to replace the `org.springframework.web.portlet.mvc.annotation.AnnotationMethodHandlerAdapter` fully-qualified
class name (FQCN) with `com.liferay.portletmvc4spring.mvc.method.annotation.PortletRequestMappingHandlerAdapter`. In
addition, the following properties have changed:
 
- [customModelAndViewResolver](https://docs.spring.io/spring-framework/docs/4.3.4.RELEASE/javadoc-api/org/springframework/web/portlet/mvc/annotation/AnnotationMethodHandlerAdapter.html#setCustomModelAndViewResolver-org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver-) (no longer available)

- [customArgumentResolver](https://docs.spring.io/spring-framework/docs/4.3.4.RELEASE/javadoc-api/org/springframework/web/portlet/mvc/annotation/AnnotationMethodHandlerAdapter.html#setCustomArgumentResolver-org.springframework.web.bind.support.WebArgumentResolver-) (no longer available)

- [customArgumentResolvers](https://liferay.github.io/portletmvc4spring/apidocs/com/liferay/portletmvc4spring/mvc/method/annotation/PortletRequestMappingHandlerAdapter.html#setCustomArgumentResolvers-java.util.List-) (specify a list of [HandlerMethodArgumentResolver](https://docs.spring.io/spring/docs/5.1.4.RELEASE/javadoc-api/org/springframework/web/method/support/HandlerMethodArgumentResolver.html) instead of a list of [WebArgumentResolver](https://docs.spring.io/spring-framework/docs/4.3.4.RELEASE/javadoc-api/org/springframework/web/bind/support/WebArgumentResolver.html))
 
## Enabling CSRF Protection

In order to enable CSRF protection, follow these steps:

1. Add the `com.liferay.portletmvc.security` library as a dependency to your portlet project. For more information,
see [Dependency Coordinates](#dependency-coordinates).

2. Add the following to your WEB-INF/spring-context/portlet-application-context.xml descriptor:

````
	<bean id="springSecurityPortletConfigurer" class="com.liferay.portletmvc4spring.security.SpringSecurityPortletConfigurer" />
	<bean id="delegatingFilterProxy" class="org.springframework.web.filter.DelegatingFilterProxy">
		<property name="targetBeanName" value="springSecurityFilterChain" />
	</bean>
````

3. Add the following to your WEB-INF/portlet.xml descriptor:

````
	<filter>
		<filter-name>SpringSecurityPortletFilter</filter-name>
		<filter-class>com.liferay.portletmvc4spring.security.SpringSecurityPortletFilter</filter-class>
		<lifecycle>ACTION_PHASE</lifecycle>
		<lifecycle>RENDER_PHASE</lifecycle>
		<lifecycle>RESOURCE_PHASE</lifecycle>
	<filter>
	<filter-mapping>
		<filter-name>SpringSecurityPortletFilter</filter-name>
		<portlet-name>portlet1</portlet-name>
	</filter-mapping>
````

4. Add the following to your WEB-INF/web.xml descriptor:
 
````
	<filter>
		<filter-name>delegatingFilterProxy</filter-name>
		<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>delegatingFilterProxy</filter-name>
		<url-pattern>/WEB-INF/servlet/view</url-pattern>
		<dispatcher>INCLUDE</dispatcher>
	</filter-mapping>
````
 
## Issues

Defects and feature requests can be posted in the [PortletMVC4Spring Issue Tracker](http://issues.liferay.com/browse/MVCS).

## License

PortletMVC4Spring is released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Building From Source

Using [Maven](https://maven.apache.org/) 3.x:

	mvn clean install

## Community Participation

For code contributions, see [CONTRIBUTING](CONTRIBUTING.md).

Post questions in the [Liferay Development Forum](https://community.liferay.com/forums/-/message_boards/category/239390).
