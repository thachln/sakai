@ECHO OFF
SET JAVA_OPTS='-server -Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=512m -XX:NewSize=192m -XX:MaxNewSize=384m -Djava.awt.headless=true -Dhttp.agent=Sakai -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false -Dsun.lang.ClassLoader.allowArraySyntax=true' 

REM Should use the absolute path for folder release
SET OUTPUT_FOLDER=%CD%\release

ECHO Ouput folder: %OUTPUT_FOLDER%

IF "%OUTPUT_FOLDER%"=="" ( 
	set /p OUTPUT_FOLDER="Input the path of the TOMCAT: "
) 

REM ECHO Run script ".\samigo\install-make-lib.cmd" before run following command:
REM cd ..\samigo\
REM CALL package.cmd
REM CALL install-make-lib.cmd

REM cd ..\toeic
REM CALL mvn clean package sakai:deploy -Dmaven.tomcat.home=%OUTPUT_FOLDER% -Dmaven.test.skip=true
CALL mvn package sakai:deploy -Dmaven.tomcat.home=%OUTPUT_FOLDER% -Dmaven.test.skip=true

@PAUSE