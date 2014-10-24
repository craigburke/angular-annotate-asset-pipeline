#!/bin/bash
set -e

function install_java {
	sudo apt-get purge openjdk*
	sudo apt-get purge oracle-java*
	
	unset JAVA_HOME
	
	JAVA_VERSION=$1
	
	sudo apt-get -q -y install python-software-properties
	sudo add-apt-repository -y ppa:webupd8team/java > /dev/null
	sudo apt-get -q -y update
	
	echo "oracle-java$JAVA_VERSION-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections
	echo "oracle-java$JAVA_VERSION-installer shared/accepted-oracle-license-v1-1 seen true" | sudo debconf-set-selections

	sudo apt-get -q -y install oracle-java$JAVA_VERSION-set-default
}

install_java 7
./grailsw test-app

install_java 8
./grailsw test-app