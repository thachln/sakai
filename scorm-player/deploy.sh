export JAVA_OPTS='-server -Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=512m -XX:NewSize=192m -XX:MaxNewSize=384m -Djava.awt.headless=true -Dhttp.agent=Sakai -Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false -Dsun.lang.ClassLoader.allowArraySyntax=true'
export TOMCAT_SAKAI=/home/lengocthach/jPackagesA/sakai11.3-tomcat-8.5.12
sh mvn clean install sakai:deploy -Dmaven.tomcat.home=$TOMCAT_SAKAI -Dsakai.home=$TOMCAT_SAKAI/sakai -Djava.net.preferIPv4Stack=true -Dmaven.test.skip=true

echo Copy the api into $TOMCAT_SAKAI/lib
cp ./scorm-api/target/scorm-api-11-SNAPSHOT.jar $TOMCAT_SAKAI/lib
cp /home/lengocthach/.m2/repository/org/projectlombok/lombok/1.16.10/lombok-1.16.10.jar $TOMCAT_SAKAI/lib

echo Prepare distribution for sakai 11.x
mkdir -p ./dist ./dist/lib ./dist/components ./dist/webapps ./dist/lib
cp /home/lengocthach/.m2/repository/org/projectlombok/lombok/1.16.10/lombok-1.16.10.jar ./dist/lib
cp ./scorm-api/target/scorm-api-11-SNAPSHOT.jar ./dist/lib
cp -R ./scorm-impl/pack/target/scorm-pack-11-SNAPSHOT ./dist/components
cp ./scorm-tool/target/scorm-tool-11-SNAPSHOT.war ./dist/webapps
