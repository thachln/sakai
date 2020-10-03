@ECHO OFF
SET JAVA_OPTS='-server -Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=512m -XX:NewSize=192m -XX:MaxNewSize=384m -Djava.awt.headless=true -Dhttp.agent=Sakai -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false -Dsun.lang.ClassLoader.allowArraySyntax=true' 

REM Should use the absolute path for folder release
SET TOMCAT_HOME=D:\Projects\gitlab-cl.fsoft.com.vn\sakai\Release\library

IF "%TOMCAT_HOME%"=="" ( 
	set /p TOMCAT_HOME="Input the path of the TOMCAT: "
) 

rem CALL mvn clean package -pl :library -am sakai:deploy -Dmaven.tomcat.home=%TOMCAT_HOME% -Dmaven.test.skip=true
REM CALL mvn install -pl :sakai-fck-connector sakai:deploy -nsu -Dmaven.tomcat.home=%TOMCAT_HOME% -X

CALL mvn clean package -pl :library sakai:deploy -Dmaven.tomcat.home=%TOMCAT_HOME%

@PAUSE