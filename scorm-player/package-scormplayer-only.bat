@ECHO OFF
SET JAVA_OPTS='-server -Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=512m -XX:NewSize=192m -XX:MaxNewSize=384m -Djava.awt.headless=true -Dhttp.agent=Sakai -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false -Dsun.lang.ClassLoader.allowArraySyntax=true'

call mvn clean install -Djava.net.preferIPv4Stack=true -Dmaven.test.skip=true

echo Prepare distribution for sakai 11.x
mkdir .\dist\components .\dist\webapps .\dist\lib

copy .\scorm-api\target\scorm-api-11-SNAPSHOT.jar .\dist\lib\scorm-api.jar
xcopy .\scorm-impl\pack\target\scorm-pack-11-SNAPSHOT .\dist\components\scorm-pack\ /S
copy .\scorm-tool\target\scorm-tool-11-SNAPSHOT.war .\dist\webapps\scorm-tool.war

REM copy .\scorm-report-api\target\scorm-report-api-11-SNAPSHOT.jar .\dist\lib\scorm-report-api.jar
REM copy .\scorm-report-tool\target\scorm-report-tool-1.0-SNAPSHOT.war .\dist\webapps\scorm-report-tool.war

