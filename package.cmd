@ECHO OFF
SET JAVA_OPTS='-server -Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=512m -XX:NewSize=192m -XX:MaxNewSize=384m -Djava.awt.headless=true -Dhttp.agent=Sakai -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false -Dsun.lang.ClassLoader.allowArraySyntax=true' 

REM Should use the absolute path for folder release
REM SET TOMCAT_HOME=D:\jPackages\sakai12.3.5-bin

IF "%TOMCAT_HOME%"=="" ( 
	set /p TOMCAT_HOME="Input the path of the TOMCAT: "
) 

ECHO Run script ".\samigo\install-make-lib.cmd" before run following command:
cd .\samigo\
CALL install-make-lib.cmd

REM cd ..\
CALL mvn clean package sakai:deploy -Dmaven.tomcat.home=%TOMCAT_HOME% -Dmaven.test.skip=true

ECHO Prepare default configuration
mkdir %TOMCAT_HOME%\xlms

ECHO Copy sample default configuration for xlms.properties.
copy .\config\configuration\bundles\src\bundle\org\sakaiproject\config\bundle\default.sakai.properties %TOMCAT_HOME%\xlms\default.xlms.properties

ECHO Copy default logging.
copy .\kernel\kernel-common\src\main\config\log4j.properties %TOMCAT_HOME%\xlms\

@PAUSE