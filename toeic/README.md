How to run the project
==============================
For the first time, run package.cmd to build the project.
If you change some things within this project (Not change in Samigo module), run quick-package.cmd.

While the script package.cmd or quick-package.cmd is running, it requests you input the folder to contain the binary package.
After finished, copy all folders "lib", "webapps" and "components" into your Tomcat Sakai.

For debugging, copy file ./configuration/log4j.properties into the foler $TOMCAT_SAKAT/sakai/

---
ThachLN@gmail.com