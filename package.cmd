@ECHO OFF
SET JAVA_OPTS='-server -Xms512m' 

REM Should use the absolute path for folder release
REM SET TOMCAT_HOME=D:\jPackages\sakai20.0
IF "%TOMCAT_HOME%"=="" ( 
	set /p TOMCAT_HOME="Input the path of the TOMCAT: "
) 

CALL mvn clean package sakai:deploy -Dmaven.tomcat.home=%TOMCAT_HOME% -Dmaven.test.skip=true

@PAUSE