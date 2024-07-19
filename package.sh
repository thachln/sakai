#!/bin/bash
export JAVA_OPTS='-server -Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=512m -XX:NewSize=192m -XX:MaxNewSize=384m -Djava.awt.headless=true -Dhttp.agent=Sakai -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false -Dsun.lang.ClassLoader.allowArraySyntax=true' 

mkdir /tmp/sakai-release
export TOMCAT_HOME=/tmp/sakai-release

mvn clean package sakai:deploy -e -Dmaven.tomcat.home=$TOMCAT_HOME -Dmaven.test.skip=true

echo Prepare default configuration
mkdir $TOMCAT_HOME\sakai

echo Copy sample default configuration for sakai.properties.
cp .\config\configuration\bundles\src\bundle\org\sakaiproject\config\bundle\default.sakai.properties $TOMCAT_HOME\sakai\default.sakai.properties

echo Copy default logging.
cp .\kernel\kernel-common\src\main\config\log4j2.properties $TOMCAT_HOME\sakai\
