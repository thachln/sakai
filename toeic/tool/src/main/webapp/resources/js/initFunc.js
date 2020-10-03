$(function () {
	var url_string = window.location.href;
	var url = new URL(url_string);
	idAssessment = url.searchParams.get("idA");

	var localData = localStorage.getItem('infoMyTest' + idAssessment);
	var localItems = localStorage.getItem('items' + idAssessment);
	if (localData) {
		localData = JSON.parse(localData);

		if (localItems) {
			localData['items'] = JSON.parse(localItems);
		}

		saveLocalData(localData);
	}

	getFaceRecognitionFlag();
	initPage();
});

async function startInit() {
	mainData = parseMainData(assessmentData.assessment);
	arrayRealQuestion = getRealQuestion(mainData);
	if (assessmentData.savedAnswer) {
		savedAnswers = assessmentData.savedAnswer;
		// start_test.innerHTML = 'CONTINUE';
	}

	getMetaData();

	await initData();
	// renderInfoToeic();

	successAlert();

	// Check password security
	// if (assessmentData.password) {
	// $('#confirm_password').modal('show');
	// }
	console.log("mainData", mainData);
}

function initPage() {
	waitingAlert("Preparing your assessment, please wait...");
	$('#rankBoard').scrollbox({
		linear: true,
		step: 1,
		delay: 0,
		speed: 100
	});
	$('#switchBootstrap20').bootstrapSwitch();
	$('#templateT').bootstrapSwitch();

	settingHelpToolbar();
	settingRNotesInToolbar();

	if (idAssessment) {
		console.log("Success " + idAssessment);

		// get decrypt password.
		getPassword();

		// Get data Assessment
		$.ajax({
			type: "POST",
			url: "getDataTest/" + idAssessment,
			dataType: 'json',
			contentType: "application/json",
			success: function (data) {
				console.log("SUCCESS data Assessment: ", data);
				assessmentData = data;
				startInit();
			},
			error: function (er) {
				console.log("ERROR: ", er);
				errorAlert("unexpected error occurred while preparing your assessment, please refresh this page...");
			}
		});
	}

	initDatabaseAndDbFunction();
	settingComfirnModalDialog();
	autoSaveAssessment();
	settingImageDialog();
	addScrollTojQuery();
	settingRankDialog();

	// remove button blue outline
	$('head').append(`<style>.button_color:focus, input[type="submit"]:focus, input[type="button"]:focus, input[type="reset"]:focus, button:focus, button.btn-primary:focus {
		outline: none !important;
	}
	.btn-toolbar {
		margin-left: 0;
	}
	</style>`);
}