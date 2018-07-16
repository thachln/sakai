SET JAVA_OPTS='-server -Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=512m -XX:NewSize=192m -XX:MaxNewSize=384m -Djava.awt.headless=true -Dhttp.agent=Sakai -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false -Dsun.lang.ClassLoader.allowArraySyntax=true'
SET TOMCAT_SAKAI=D:/Campuslink/scorm-player/svn/trunk/source/sakai-11.3/scorm-player/package
call mvn clean install sakai:deploy -Dmaven.tomcat.home=%TOMCAT_SAKAI% -Dsakai.home=%TOMCAT_SAKAI%/sakai -Djava.net.preferIPv4Stack=true -Dmaven.test.skip=true

echo Copy the api into %TOMCAT_SAKAI%/lib

copy .\scorm-api\target\scorm-api-11-SNAPSHOT.jar %TOMCAT_SAKAI%\lib
copy %USERPROFILE%\.m2\repository\org\projectlombok\lombok\1.16.14\lombok-1.16.14.jar %TOMCAT_SAKAI%\lib

echo Prepare distribution for sakai 11.x
mkdir .\dist\components .\dist\webapps .\dist\lib
copy %USERPROFILE%\.m2\repository\org\projectlombok\lombok\1.16.14\lombok-1.16.14.jar .\dist\lib
copy .\scorm-api\target\scorm-api-11-SNAPSHOT.jar .\dist\lib\scorm-api.jar
xcopy .\scorm-impl\pack\target\scorm-pack-11-SNAPSHOT .\dist\components\scorm-pack\ /S
copy .\scorm-tool\target\scorm-tool-11-SNAPSHOT.war .\dist\webapps\scorm-tool.war
