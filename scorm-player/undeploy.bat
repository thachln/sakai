@ECHO OFF
SET TOMCAT_SAKAI=D:\jPackages\sakai11.3-tomcat-8.0.47

ECHO Removing ./webapps/scorm-tool-11-SNAPSHOT

del /S /Q %TOMCAT_SAKAI%\lib\scorm-*.jar
REM del /S /Q %TOMCAT_SAKAI%\lib\gradebook-*.jar

del /S /Q %TOMCAT_SAKAI%\webapps\scorm-*
REM del /S /Q %TOMCAT_SAKAI%\webapps\sakai-gradebook-tool.war
rd /S  /Q %TOMCAT_SAKAI%\webapps\scorm-tool-11-SNAPSHOT
rd /S  /Q %TOMCAT_SAKAI%\webapps\scorm-tool

REM rd /S  /Q %TOMCAT_SAKAI%\webapps\sakai-gradebook-tool

rd /S  /Q %TOMCAT_SAKAI%\components\scorm-pack
REM rd /S  /Q %TOMCAT_SAKAI%\components\gradebook-service-pack

