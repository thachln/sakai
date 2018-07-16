echo Remove all files, folders of scorm-player from the Tomcat

export TOMCAT_SAKAI=/opt/tomcat

rm -f $TOMCAT_SAKAI/lib/scorm-*.jar
rm -f $TOMCAT_SAKAI/lib/gradebook-*.jar

rm -f $TOMCAT_SAKAI/webapps/scorm-*.war

rm -f $TOMCAT_SAKAI/webapps/sakai-gradebook-tool.war


rm -f -R $TOMCAT_SAKAI/webapps/scorm-tool-11-SNAPSHOT

rm -f -R $TOMCAT_SAKAI/webapps/scorm-tool

rm -f -R $TOMCAT_SAKAI/webapps/sakai-gradebook-tool


rm -f -R $TOMCAT_SAKAI/components/gradebook-service-pack

rm -f -R $TOMCAT_SAKAI/components/scorm-pack

rm -f -R $TOMCAT_SAKAI/components/scorm-pack-11-SNAPSHOT


