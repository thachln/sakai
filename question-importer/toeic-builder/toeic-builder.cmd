@ECHO OFF
IF "%INPUT_FOLDER%"=="" ( 
	set /p INPUT_FOLDER="Input the path of the folder contains sub folder 'media': "
)

IF "%URL_ROOT%"=="" ( 
	set /p URL_ROOT="Input URL contains sub folder 'media': "
)

IF "%OUTPUT_FOLDER%"=="" ( 
	set /p OUTPUT_FOLDER="Input the path of the output: "
)

java -jar toeic-builder-12.3-0.2.jar %INPUT_FOLDER% %URL_ROOT% %OUTPUT_FOLDER%

PAUSE