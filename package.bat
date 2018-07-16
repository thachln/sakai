@ECHO OFF
SET JAVA_OPTS='-server -Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=512m -XX:NewSize=192m -XX:MaxNewSize=384m -Djava.awt.headless=true -Dhttp.agent=Sakai -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false -Dsun.lang.ClassLoader.allowArraySyntax=true' 

REM Should use the absolute path for folder release
SET FOLDER_RELEASE=D:\MyProjects\github.com\sakai\fork-sakai12.2\release

CALL mvn package sakai:deploy -Dmaven.tomcat.home=%FOLDER_RELEASE% -Dmaven.test.skip=true