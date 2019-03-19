# PortletMVC4Spring

The PortletMVC4Spring project began as Spring Portlet MVC which was originally part of the
[Spring Framework](https://spring.io/projects/spring-framework). When the project was pruned from version 5.0.x of the
Spring Framework under [SPR-14129](https://github.com/spring-projects/spring-framework/issues/18701), it became
necessary to fork and rename the project in order to improve and maintain it for compatibility with future versions of
the Spring Framework.

[Liferay, Inc.](http://www.liferay.com) adopted Spring Portlet MVC in March of 2019 and the project was renamed **PortletMVC4Spring**.

## Version Scheme

PortletMVC4Spring follows a major.minor.patch version scheme. The major and minor version numbers correspond to
compatibility with the Spring Framework. For example, version 5.1.x of PortletMVC4Spring is intended for use with
version 5.1.x of the Spring Framework. The patch number indicates an incremental version containing patches for
software defects.

## Dependency Coordinates

**Maven:**

	<dependencies>
		<dependency>
			<groupId>com.liferay.portletmvc4spring</groupId>
			<artifactId>com.liferay.portletmvc4spring.framework</artifactId>
			<version>5.1.0</version>	
		</dependency>
	<dependencies>
	
**Gradle:**

	dependencies {
		compile group: 'com.liferay', name: 'com.liferay.portletmvc4spring', version: '5.1.0'
	}

## Archetypes

	# JSP Form
	mvn archetype:generate \
		-DarchetypeGroupId=com.liferay.portletmvc4spring.archetype \
		-DarchetypeArtifactId=com.liferay.portletmvc4spring.archetype.form.jsp.portlet \
		-DarchetypeVersion=5.1.0-SNAPSHOT \
		-DgroupId=com.mycompany \
		-DartifactId=com.mycompany.my.form.jsp.portlet \

	# Thymeleaf Form
	mvn archetype:generate \
		-DarchetypeGroupId=com.liferay.portletmvc4spring.archetype \
		-DarchetypeArtifactId=com.liferay.portletmvc4spring.archetype.form.thymeleaf.portlet \
		-DarchetypeVersion=5.1.0-SNAPSHOT \
		-DgroupId=com.mycompany \
		-DartifactId=com.mycompany.my.form.thymeleaf.portlet \

## Dependency Upgrades

PortletMVC4Spring requires JDK 8+ and has been upgraded from version 2.0 of the Portlet API to version 3.0. In
addition, it has been refactored and tested for use with version 5.1.x of the Spring Framework.

## Issues

Defects and feature requests can be posted in the [PortletMVC4Spring Issue Tracker](http://issues.liferay.com/browse/MVCS).

## License

PortletMVC4Spring is released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Building From Source

Using [Maven](https://maven.apache.org/) 3.x:

	mvn clean install

## Documentation

* [Official Documentation](src/main/asciidoc/portletmvc4spring.adoc)

## Community Participation

For code contributions, see [CONTRIBUTING](CONTRIBUTING.md).

Post questions in the [Liferay Development Forum](https://community.liferay.com/forums/-/message_boards/category/239390).
