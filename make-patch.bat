@ECHO OFF
REM Delete the folder "dist"
SET RELEASE_FOLDER=D:\MyProjects\github.com\sakai\fork-sakai12.0\release
SET RELEASE_PATCH=D:\MyProjects\github.com\sakai\fork-sakai12.0\release-patch
rd /S  /Q %RELEASE_PATCH%

REM Call package.bat to make binary package
REM call package.bat

echo Prepare distribution for sakai 11.x
mkdir %RELEASE_PATCH%\webapps %RELEASE_PATCH%\components

copy %RELEASE_FOLDER%\webapps\admin-tools.war %RELEASE_PATCH%\webapps\
xcopy %RELEASE_FOLDER%\components\localization-bundles %RELEASE_PATCH%\components\localization-bundles\ /S
copy %RELEASE_FOLDER%\webapps\sakai-login-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\portal.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-presence-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-reset-pass.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-site-manage-link-helper.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-site-manage-participant-helper.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-site-manage-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-tool-tool-su.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-signup-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\lessonbuilder-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\samigo-app.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\messageforums-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-site-*.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-syllabus-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-user-tool-admin-prefs.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-user-tool-prefs.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-web-portlet.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-web-portlet-basiclti.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-web-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\profile2-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-postem-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-message-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-message-bundle-manager-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-mailarchive-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\gradebookng-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\feedback-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-chat-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-announcement-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-content-tool.war %RELEASE_PATCH%\webapps\
copy %RELEASE_FOLDER%\webapps\sakai-calendar-*.war %RELEASE_PATCH%\webapps\