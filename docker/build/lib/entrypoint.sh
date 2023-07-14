#!/bin/bash
# Append any secret properties from /run/secrets/security.properties
# cat /run/secrets/security.properties >> /opt/tomcat/sakai/security.properties

# Start tomcat
/opt/tomcat/bin/catalina.sh run
