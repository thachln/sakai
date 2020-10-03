REM call mvn clean package

REM Install samigo-api
cd .\samigo-api
call mvn install
cd ..\

REM Install samigo-services
cd .\samigo-services
call mvn install -Dmaven.test.skip=true
ECHO Installed 'samigo-services'.

REM @PAUSE
cd ..\samigo-app
call mvn package -Dmaven.test.skip=true
cd .\target\samigo-app-19.3\WEB-INF\classes

jar cvf samigo-app-lib-19.3.jar *
call mvn install:install-file -Dfile=samigo-app-lib-19.3.jar -DgroupId=org.sakaiproject.samigo -DartifactId=samigo-app-lib -Dversion=19.3 -Dpackaging=jar
ECHO Installed 'samigo-app-lib'.
cd ..\..\..\..\..\

REM @PAUSE