#!/usr/bin/perl -w

################################################################################
#
# Copyright (c) 2000-2019 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
################################################################################

################################################################################
#
# This Perl script is a convenience utility that parses the project version
# information from the parent-most pom.xml file and fixes version numbers in
# various type of files.
#
################################################################################
#
# Author: Vernon Singleton
#
################################################################################

use strict;
use File::Find;
use POSIX qw(strftime);

#
# Primitives
#
my($major1,$major2,$minor);
my($portalVersion,$portalVersionDTD,$portalVersions,$portalDtdDisplay,$portalDtdUrl);
my($portletApi,$portletApiURL,$portletApiMajor,$portletApiMajorDotMinor,$servletApi,$servletApiURL,$servletApiMajor1DotMajor2);
my $year= strftime "%Y", localtime;

#
# Parse the project version information from the parent-most POM file.
#
open POM, "pom.xml" or die "cannot open pom.xml: $!\n";
while(<POM>) {

	if (/artifactId>com.liferay.portletmvc4spring.parent</) {

		$_ = <POM>;
		/<version>(.*)</;
	}

	if (/^\t\t<liferay.version>/) {

		/version>(.*)</;
		$portalVersion = $1;
		print "portalVersion = $portalVersion\n";

		$_ = $portalVersion;

		($major1,$major2,$minor) = split /\./;
		$portalVersions = "${major1}.${major2}.*";
		print "portalVersions = $portalVersions\n";
	}

	if (/^\t\t<liferay.version.dtd>/) {

		/version.dtd>(.*)</;
		$portalVersionDTD = $1;
		print "portalVersionDTD = $portalVersionDTD\n";

		$_ = $portalVersionDTD;

		($major1,$major2,$minor) = split /\./;

		$portalDtdDisplay = "${major1}.${major2}.0";
		print "portalDtdDisplay = $portalDtdDisplay\n";

		$portalDtdUrl = "${major1}_${major2}_0";
		print "portalDtdUrl = $portalDtdUrl\n";

	}

	if(/portlet-api<\/artifactId>/) {

			$_ = <POM>;
			if (/version>(.*)</) {
			$portletApi = $1;
			print "portletApi = $portletApi\n";

			$_ = $portletApi;
			($major1,$minor) = split /\./;
			$_ = $minor;
			($minor) = split /\-/;

			$portletApiURL = "${major1}_${minor}";
			print "portletApiURL = $portletApiURL\n";

			$portletApiMajor = "${major1}";

			$portletApiMajorDotMinor = "${major1}.${minor}";
			print "portletApiMajorDotMinor = $portletApiMajorDotMinor\n";
		}
	}

	if(/servlet-api<\/artifactId>/) {

			$_ = <POM>;
			if (/version>(.*)</) {
			$servletApi = $1;
			print "servletApi = $servletApi\n";

			$_ = $servletApi;
			($major1,$major2,$minor) = split /\./;

			$servletApiURL = "${major1}_${major2}";
			print "servletApiURL = $servletApiURL\n";

			$servletApiMajor1DotMajor2 = "${major1}.${major2}";
			print "servletApiMajor1DotMajor2 = $servletApiMajor1DotMajor2\n";
		}
	}

}
close POM;

#
# For each file in the hierarchy:
#
find(\&do_inplace_edits, ".");

sub do_inplace_edits {

	my $file = $_;

	#
	# If the current file is named "web.xml", then potentially fix the
	# version number specified in the schemaLocation url.
	#
	if ((($file eq "web.xml") or ($file =~ m/web[-].*\.xml/)) and ($File::Find::name =~ /\/src/)) {
		print "$File::Find::name\n";
		$_ = $File::Find::name;

			open OUT, ">web.xml.tmp" or die "cannot open >web.xml.tmp: $!\n";
			open IN, $file or die "cannot open $file: $!\n";
			while(<IN>) {
				if (/web-app_/) {
					s/web-app_\d+_\d+.xsd/web-app_${servletApiURL}.xsd/;
					if (/version="/) {
						s/version="\d+.\d+"/version="${servletApiMajor1DotMajor2}"/;
					} else {
						print OUT;
						$_ = <IN>;
						s/version="\d+.\d+"/version="${servletApiMajor1DotMajor2}"/;
					}
				}
				print OUT;
			}
			close IN;
			close OUT;
			rename("web.xml.tmp", $file);
	}

	#
	# Otherwise, if the current file is named "portlet.xml" then potentially fix the version
	# numbers specified in DOCTYPE line for the DTD.
	#
	elsif ($file eq "portlet.xml" and ($File::Find::name =~ /\/src/)) {
		print "$File::Find::name\n";
		if ($portletApiMajor == 3) {
			`perl -pi -e 's/portlet.portlet-app_2_0.xsd\" version=\"2.0\"/portlet\" version=\"$portletApiMajorDotMinor\"/' $file`;
			`perl -pi -e 's/.portlet-app_2_0.xsd http/ http/' $file`;
			`perl -pi -e 's/portlet-app_2_0/portlet-app_$portletApiURL/' $file`;
			`perl -pi -e 's/java.sun.com/xmlns.jcp.org/g' $file`;
		}
	}

	#
	# Otherwise, if the current file is named "liferay-portlet.xml" then potentially fix the version
	# numbers specified in DOCTYPE line for the DTD.
	#
	elsif ($file eq "liferay-portlet.xml" and ($File::Find::name =~ /\/src/)) {
		print "$File::Find::name\n";
		`perl -pi -e 's/DTD Portlet Application ..*\\/\\/EN/DTD Portlet Application $portalDtdDisplay\\/\\/EN/' $file`;
		`perl -pi -e 's/-portlet-app_..*\\.dtd/-portlet-app_$portalDtdUrl\\.dtd/' $file`;
	}

	#
	# Otherwise, if the current file is named "liferay-display.xml" then potentially fix the version
	# numbers specified in DOCTYPE line for the DTD.
	#
	elsif ($file eq "liferay-display.xml" and ($File::Find::name =~ /\/src/)) {
		print "$File::Find::name\n";
		`perl -pi -e 's/DTD Display ..*\\/\\/EN/DTD Display $portalDtdDisplay\\/\\/EN/' $file`;
		`perl -pi -e 's/-display_..*\\.dtd/-display_$portalDtdUrl\\.dtd/' $file`;
	}

	#
	# Otherwise, if the current file is named "liferay-plugin-package.properties" then potentially fix
	# the version wildcard that indicates compatible versions of Liferay Portal as well as the Bundle-Version.
	#
	elsif ($file eq "liferay-plugin-package.properties" and ($File::Find::name =~ /\/src/) and ($File::Find::name !~ /\/archetype/)) {
		print "$File::Find::name\n";
		`perl -pi -e 's/liferay-versions=..*/liferay-versions=$portalVersions/' $file`;
	}

	#
	# Otherwise, if the current file is named NOTICE, then potentially fix the copyright year.
	#
	elsif (($file =~ m/NOTICE/) and ($File::Find::name =~ /\/src/)) {
		print "$File::Find::name\n";
		`perl -pi -e 's/2000-..* the original author or authors./2000-${year} the original author or authors./' $file`;
	}

	#
	# Otherwise, if the current file is named with .java extension, then potentially fix the copyright year.
	#
	elsif (($file =~ m/.*\.java/) and ($File::Find::name =~ /\/src/)) {
		print "$File::Find::name\n";
		`perl -pi -e 's/2000-..* the original author or authors./2000-${year} the original author or authors./' $file`;
	}
}
