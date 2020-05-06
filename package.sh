
export JAVA_OPTS='-server -Xms512m'

if ["$TOMCAT_HOME" = ""]
then
    read -p 'Input the path of output or TOMCAT HOME dir: ' TOMCAT_HOME
fi

mvn clean package sakai:deploy -Dmaven.tomcat.home=$TOMCAT_HOME -Dmaven.test.skip=true
