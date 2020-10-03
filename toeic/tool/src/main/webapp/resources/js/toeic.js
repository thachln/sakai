// DOM
var start_page = _('start_page');
var test_page = _('test_page');
var result_page = _('result_page');
var info_toeic = _('info_toeic');
var start_test = _('start_test');
var answer_sheet = _('answer_sheet');
var timeToeicId = _('time_remain');
var nextBtn = _("nextBtn");
var backBtn = _("backBtn");
var submitBtn = _("submitBtn");
var saveBtn = _("saveBtn");
var question_contain = _("question_contain");
var multi_question_area = _("multi_question");
var toeicForm = document.getElementsByClassName("toeic-form");
var submitted = false;
var currentQuestionNum = 1;
var savedItems = true;
var firstPartNum = 1;

// Init
var listAssessment;
var idAssessment;
var assessmentData;
var mainData;
var arrayRealQuestion;
var savedAnswers;
var introUrl;
var partsMetaData = {};
var audioNumber = new Audio();
var audioLook = new Audio();
var feedback;
var currentPart;
var isReady = false;
var playLook = false;
var played = false;
var playedIntro = false;
var playingDirection = false;
var playingIntro = false;
var playedPartDirections = [false, false, false, false, false, false, false];
var timeLimit_minute;
var indexPart = 1;
var indexQuestion = 0;
var isStart = false;
var TIME_REMAINING_SECOND = 0;
var TIME_REMAINING_QUESTION_SECOND;
var counterTimeToeic;
var arrSelectedAnswers = [];
var choseAssessment;
var loadFilePermission = true;
var itemToSaveQuery = [];
var saveQueryInterval;
var savingItem;
var errorAudioRetry = false;
var renderedSavedAnswers = false;
var faceRec;
var images = [];
var countSaveImage = 0;
var start = false;
var end = false;
var template = true; // true = question by question; false = part by part

// variables for IndexedDB
var indexedDB, dbVersion, request, db, createObjectStore, getFile, getFile2, addToDb, getFromDb, getAllFromDb, deleteFromDb, searchFromDb, deleteFromDbWithRange;
window.indexedDB = window.indexedDB || window.webkitIndexedDB || window.mozIndexedDB || window.OIndexedDB || window.msIndexedDB,
	IDBTransaction = window.IDBTransaction || window.webkitIDBTransaction || window.OIDBTransaction || window.msIDBTransaction;
var loadRetry = [];
var localAudioUrl;
var localImageUrl;
var localNumberUrl;
var localLookUrl;
var localDirectImg;
var localDirectAudio;

// password to decrypt.
var cryptoPassword;

var lengthPartAll = {
	part1: 0,
	part2: 0,
	part3: 0,
	part4: 0,
	part5: 0,
	part6: 0,
	part7: 0,
};

// This flag is used in begin.html of the TOEIC data.
var isDisplayedBegin = false;

// Event btn
start_test.addEventListener("click", function () {
	if (faceRec.startRecognition) {
		startCamera();
		$("#start_page").addClass("hidden");
	} else {
		handleFlag(true);
	}
});
// Event for next button
nextBtn.addEventListener("click", function () {
	nextQuestion();
});
// Event for back button
backBtn.addEventListener("click", function () {
	prevQuestion();
});

// Event for submit button
submitBtn.addEventListener("click", function () {
	submitForGrading(true);
});

// Event for save button
saveBtn.addEventListener("click", function () {
	submitForGrading(false);
});

// Event for audio, set time remain question
audioNumber.addEventListener("ended", () => {
	if (playLook) {
		audioLook.play();
	} else {
		_("audioQuestion").play();
	}

});

audioLook.addEventListener("ended", () => {
	_("audioQuestion").play();
});

audioNumber.onerror = function (e) {
	if ($(e.path[0]).attr("src")) {
		errorAlert(error_number);
	}
};

audioLook.onerror = function (e) {
	if ($(e.path[0]).attr("src")) {
		errorAlert(error_look);
	}
};

// START
// if (mainData) {
// renderInfoToeic(mainData);
// }


// =====================Function=================== //

function _(x) {
	return document.getElementById(x);
}

async function initData() {
	console.log('arrayRealQuestion', arrayRealQuestion);

	checkTimeLimit().then(async (pastDue) => {
		if (pastDue) {
			submitForGrading(true, true, true);
		} else {
			renderInfoToeic();

			for (var prop in mainData) {
				if (mainData.hasOwnProperty(prop)) {
					indexPart = parseInt(prop.slice(-1));
					firstPartNum = parseInt(prop.slice(-1));
					break;
				}
			}

			if (!introUrl) {
				playedIntro = true;
			}
			currentPart = "part" + indexPart;

			if (savedAnswers) {
				// if (savedAnswers.timeElapsed) {
				// 	TIME_REMAINING_SECOND = TIME_REMAINING_SECOND - savedAnswers.timeElapsed;
				// }
				if (savedAnswers.lastVisitedPart) {
					playedIntro = true;
					indexPart = savedAnswers.lastVisitedPart;
					currentPart = "part" + indexPart;

					for (var i = 0; i < savedAnswers.lastVisitedPart; i++) {
						playedPartDirections[i] = true;
					}
				}
				if (savedAnswers.lastVisitedQuestion) {
					playedIntro = true;
					indexQuestion = savedAnswers.lastVisitedQuestion;
				}
				if (mainData["part" + indexPart][indexQuestion].length > 0) {
					currentQuestionNum = getRealIndex(arrayRealQuestion, mainData["part" + indexPart][indexQuestion][1]);
				} else {
					currentQuestionNum = getRealIndex(arrayRealQuestion, mainData["part" + indexPart][indexQuestion]);
				}
			}

			getLengthEachPart();
			shortcutKeyEvent();
			start_test.classList.remove("disabled");
		}
	});
}

async function startTest() {
	if (isStart) {
		waitingAlert(wait);

		// disable checkbox loadFilePermission
		$('#switchBootstrap20').bootstrapSwitch('disabled', true);
		$('#templateT').bootstrapSwitch('disabled', true);
		start_test.classList.add("disabled");

		// checkTimeLimit().then(async (pastDue) => {
		// 	if (pastDue) {
		// 		submitForGrading(true, true, true);
		// 	} else {
		loadFilePermission = $('#switchBootstrap20').is(":checked");
		template = $("#templateT").prop("checked");

		if (!assessmentData.assessment.practice) {
			disableTextSelectAndCopyPaste();
		}

		if (window.indexedDB && loadFilePermission) {

			// wait finish load all audio of part 1.
			let ready = await getAndStoreAudioFile(assessmentData.savedAnswer.lastVisitedPart);

			if (ready) {
				start_page.classList.add("hidden");
				test_page.classList.remove("hidden");

				// Counter time toeic
				timer(TIME_REMAINING_SECOND, timeToeicId);
				// Render answer sheet
				renderAnswerSheet(arrayRealQuestion);
				// Render toeic area
			if (template || indexPart < 5) {
				renderToeic(mainData);
			} else if (!template && indexPart >= 5) {
				nextBtn.classList.add("hidden");
				backBtn.classList.add("hidden");
				renderPBP(indexPart);
			}
				// param = 2 => start load all audio of all part in assessment start from 2 to 7.
				getAndStoreAudioFile(assessmentData.savedAnswer.lastVisitedPart + 1);
			}
		} else {
			start_page.classList.add("hidden");
			test_page.classList.remove("hidden");

			// Counter time toeic
			timer(TIME_REMAINING_SECOND, timeToeicId);
			// Render answer sheet
			renderAnswerSheet(arrayRealQuestion);
			// Render toeic area
			if (template || indexPart < 5) {
				renderToeic(mainData);
			} else if (!template && indexPart >= 5) {
				nextBtn.classList.add("hidden");
				backBtn.classList.add("hidden");
				renderPBP(indexPart);
			}
		}
		// }
		successAlert();
		// });
	} else {
		start_page.classList.remove("hidden");
		test_page.classList.add("hidden");
	}
}

function getElementsFromUrl(url) {
	$.get(url, function (data) {
		appendDirection(data);
	}).fail(function () {
		nextQuestion(true);
	});
}

async function appendDirection(data, part) {
	if (data instanceof Blob) {
		data = await readFileAsText(data);
	}

	if (localDirectAudio) {
		window.URL.revokeObjectURL(localDirectAudio);
	}

	if (localDirectImg) {
		window.URL.revokeObjectURL(localDirectImg);
	}

	if (part) {
		let dImgUrl = $(data).find('img').eq(0).attr('src');
		let dAudioUrl = $(data).find('source').eq(0).attr('src');

		if (dImgUrl) {
			let result = await getFromDb(dImgUrl + userEid + idAssessment + part, "url").catch((e) => {
				console.log("error :", e, dImgUrl);
			});
			if (result) {
				localDirectImg = await window.URL.createObjectURL(result.blob);
				data = data.replace(dImgUrl, localDirectImg);
			}
		}

		if (dAudioUrl) {
			let result = await getFromDb(dAudioUrl + userEid + idAssessment + part, "url").catch((e) => {
				console.log("error :", e, dAudioUrl);
			});
			if (result) {
				localDirectAudio = await window.URL.createObjectURL(result.blob);
				data = data.replace(dAudioUrl, localDirectAudio);
			}
		}
	}

	$('#question_contain').html(data);

	// find inline scripts then execute them
	$(data).find("script").each(function () {
		var scriptContent = $(this).html(); // Grab the content of this tag
		eval(scriptContent); // Execute the content
	});

	if (_("direction")) {
		_("direction").play();
	}
}

async function renderToeic(data) {

	stopAudio();
	$('#question_contain').empty();

	if (!playedIntro && indexPart == firstPartNum && indexQuestion == 0 && introUrl) {
		getElementsFromUrl(introUrl);
		playedIntro = true;
		playingIntro = true;
		nextBtn.classList.add("hidden");
	} else if (!playedPartDirections[indexPart - 1] && indexQuestion == 0 && partsMetaData["part" + indexPart] && partsMetaData["part" + indexPart].directions) {
		let directionUrl = partsMetaData["part" + indexPart].directions;

		if (window.indexedDB && loadFilePermission) {
			let result = await getFromDb(directionUrl + userEid + idAssessment + indexPart, "url").catch((e) => {
				console.log("error :", e, question.audio);
			});
			console.log("load direction from indexedDb: ", result);
			if (result) {
				appendDirection(result.blob, indexPart);
			} else {
				getElementsFromUrl(directionUrl);
			}
		} else {
			getElementsFromUrl(directionUrl);
		}

		nextBtn.classList.add("hidden");
		backBtn.classList.add("hidden");
		playedPartDirections[indexPart - 1] = true;
		playingIntro = false;
		playingDirection = true;
	} else {
		playingIntro = false;
		playingDirection = false;
		if (indexPart < 5 || template) {
			nextBtn.classList.remove("hidden");
		}

		// Data part question array
		var dataQuestion = data["part" + indexPart][indexQuestion];

		// Render part question
		if ((indexPart == 1 || indexPart == 2 || indexPart == 5) && (template || indexPart < 5)) {
			renderForSingleQuestion(dataQuestion);
		} else if ((indexPart == 3 || indexPart == 4 || indexPart == 6 || indexPart == 7) && (template || indexPart < 5)) {
			// console.log("Call renderForMultiQuestion...");
			renderForMultiQuestion(dataQuestion);
		} else {
			console.log("Warning: indexPart=" + indexPart + "is not processed.");
		}

		if (indexPart > 4 && indexQuestion > 0 && template) {
			backBtn.classList.remove("hidden");
		}

		if (indexPart < 4 || (indexPart == 5 && indexQuestion == 0)) {
			backBtn.classList.add("hidden");
		}

		// Show directions part
		// if (indexQuestion == 0) {
		// $("#infoPart").collapse('show');
		// }

		if ((indexQuestion == mainData["part" + indexPart].length - 1) && indexPart == 7) {
			nextBtn.classList.add("hidden");
			if (template) {
				backBtn.classList.remove("hidden");
			}
			submitBtn.classList.remove("hidden");
		}
	}
}

// Read data part
function readDataPart(dataAll, partNum) {
	const data = dataAll.data["part" + partNum];
	return data;
}

// Render infomation toeic test
function renderInfoToeic() {
	if (assessmentData.savedAnswer.timeElapsed > 0) {
		start_test.innerHTML = 'RESUME';
	} else {
		start_test.innerHTML = 'START';
	}
	let desc = assessmentData.assessment.description;
	
	if (desc != null) {
		desc = desc.replace("start_face_recognition: true", "");
		desc = desc.replace("end_face_recognition: true", "");
		let match = desc.match("part_face_recognition:\\s\\[\\s*[0-9\\,\\s*].*\\]");
		if (match) {
			desc = desc.replace(match[0], "");
		}
	} else {
		desc = "";
	}

	$("#description").html(desc);

	if (assessmentData.assessment) {
		$("#assessmentTitle").html(assessmentData.assessment.assessmentTitle);
		if (assessmentData.assessment.timeLimit > 0) {
			timeLimit_minute = Math.floor(assessmentData.assessment.timeLimit / 60);
		} else if (assessmentData.assessment.timeLimit == 0) {
			timeLimit_minute = 120;
		}

		info_toeic.innerHTML = `
			${(assessmentData.assessment.dueDateString) ? `<p><b>Due Day:</b> ${assessmentData.assessment.dueDateString}</p>` : ''}
		`;

		let mm = (parseInt(TIME_REMAINING_SECOND / 60) < 10) ? ("0" + parseInt(TIME_REMAINING_SECOND / 60)) : parseInt(TIME_REMAINING_SECOND / 60);
		let ss = (parseInt(TIME_REMAINING_SECOND % 60) < 10) ? ("0" + parseInt(TIME_REMAINING_SECOND % 60)) : parseInt(TIME_REMAINING_SECOND % 60);

		$("#time_remain").html(mm + ":" + ss);
		if (assessmentData.assessment.practice) {
			settingAudioTextDialog();
		}
	}
}

// For part 1, 2, 5
async function renderForSingleQuestion(data) {

	if (assessmentData.assessment.practice) {
		addFeedbackTextForDialog(data);
	}

	data = parseData(data);

	var indexQ = getRealIndex(arrayRealQuestion, data);

	// if user granted permission to load question'data to local, replace question's audio, image with local data.
	if (window.indexedDB && loadFilePermission) {
		data = await replaceWithLocalBlob(data, indexPart, indexQ);
	}

	// if file has been encrypted get file blob and decrypt the file and create url for the file and set to localAudioUrl.
	if (cryptoPassword) {
		if (data.audioUrl) {
			let ready = await getFileAndDecrypt(data.audioUrl);
			if (ready) {
				data.audioUrl = localAudioUrl;
			} else {
				data.audioUrl = "";
			}
		}
	}

	// mark color
	markColorAnswerSheet(indexQ);
	// Render question
	question_contain.innerHTML = `
		<div class="title-part" >
			<h3 id="title_part">${setTitlePart(data.poolName)}</h3>
			${(assessmentData.assessment.practice && indexPart < 5) ? `<button class="btn btn-info" style="margin: 5px" onclick="showAudioTextDialog()">Show Audio Text</button>` : ''}
		</div>
		<div class="test-box">
			<div class="toeic-index">
				<h4 id="question_number">Question ${getRealIndex(arrayRealQuestion, data)}</h4>
			</div>
			<div class="toiec-question">
				<div id="pic_question">
					${data.imageUrl ? `<img style="cursor: pointer;" src="${data.imageUrl}" onclick="showImage(this)" alt="${error_network}">` : ''}
				</div>
				<p class="text-question" id="text_question">
					${data.justText}
				</p>
			</div>
			${data.audioUrl ? `
				<div class="for-listening text-center" id="audio_question">
					<div class="black">
					</div>
					${data.audioUrl ? `<audio class="mt-md-3s" preload="auto" src="${data.audioUrl}" id="audioQuestion" controls controlsList="nodownload">` : ''}
				</div>`
			: ''}
			<div class="toeic-select">
				<form class="toiec-form">
					<label class="radio-inline answerTextOption" onclick="selectAnswer(${data.answerAId})">
						<strong>A. </strong>
						<span id="answerA">${data.answerA}</span>
					</label>
					<label class="radio-inline answerTextOption" onclick="selectAnswer(${data.answerBId})">
						<strong>B. </strong>
						<span id="answerB">${data.answerB}</span>
					</label>
					<label class="radio-inline answerTextOption" onclick="selectAnswer(${data.answerCId})">
						<strong>C. </strong>
						<span id="answerC">${data.answerC}</span>
					</label>
					${(data.answerDId && indexPart != 2) ? `
						<label class="radio-inline answerTextOption" onclick="selectAnswer(${data.answerDId})">
							<strong>D. </strong><span> ${data.answerD}</span>
						</label>
					` : ''}
				</form>
			</div>
		</div>
	`;

	if (indexPart < 5) {
		playAudio(indexQ);
	}
}

// For part 3, 4, 6 ,7
async function renderForMultiQuestion(data) {
	// console.log("renderForMultiQuestion");

	var childQuestion = [];
	var fatherQuestion;

	// info question
	fatherQuestion = data.filter((question) => {
		return isValidPatternX_Y(question.objective);
	});

	// child question
	childQuestion = data.filter((question) => {
		return !isValidPatternX_Y(question.objective);
	});

	// sorting child question
	childQuestion.sort(function (a, b) {

		return parseInt(a.objective.trim().slice(-1)) - parseInt(b.objective.trim().slice(-1));
	});

	var indexQ = getRealIndex(arrayRealQuestion, childQuestion[0]);
	var lastIndexQ = getRealIndex(arrayRealQuestion, childQuestion[childQuestion.length - 1]);

	if (assessmentData.assessment.practice) {
		addFeedbackTextForDialog(data);
	}

	// mark color
	markColorAnswerSheet(indexQ, lastIndexQ);
	// Render question father
	let parseFatherData = parseData(fatherQuestion[0]);
	let infoQuestion = showQuestionMulti(parseFatherData, indexQ, lastIndexQ);

	// if user granted permission to load question'data to local, replace question's audio, image with local data.
	if (window.indexedDB && loadFilePermission) {
		infoQuestion = await replaceWithLocalBlob(infoQuestion, indexPart, indexQ);
	}

	// if file has been encrypted get file blob and decrypt the file and create url for the file and set to localAudioUrl.
	if (cryptoPassword) {
		if (infoQuestion.audioUrl) {
			let ready = await getFileAndDecrypt(infoQuestion.audioUrl);
			if (ready) {
				infoQuestion.audioUrl = localAudioUrl;
			} else {
				infoQuestion.audioUrl = "";
			}
		}
	}

	question_contain.innerHTML = `
		<div class="title-part" >
			<h3 id="title_part_multi">${setTitlePart(infoQuestion.poolName)}</h3>
			${(assessmentData.assessment.practice && indexPart < 5) ? `<button class="btn btn-info" style="margin: 5px" onclick="showAudioTextDialog()">Show Audio Text</button>` : ''}

		</div>
		<div class="test-box">
			<div class="toeic-index">
				<h4 id="question_number_multi">Question ${getRealIndex(arrayRealQuestion, childQuestion[0])} - ${getRealIndex(arrayRealQuestion, childQuestion[childQuestion.length - 1])} </h4>
			</div>
			<div class="toiec-question">
				<div id="pic_question_multi">
					${infoQuestion.imageUrl ? `<img style="cursor: pointer;" src="${infoQuestion.imageUrl}" onclick="showImage(this)" alt="${error_network}">` : ''}
				</div>
				<p class="text-question" id="text_question_multi">
					${infoQuestion.justText}
				</p>
			</div>
			${infoQuestion.audioUrl ? `
				<div class="for-listening text-center" id="audio_question_multi">
					<div class="black">
					</div>
					${infoQuestion.audioUrl ? `<audio preload="auto" src="${infoQuestion.audioUrl}" id="audioQuestion" controls controlsList="nodownload">` : ''}
				</div>
			` : ''}

			<div class="multi-question" id="multi_question">
				${	/*Render quesion child*/
		childQuestion.map((item, index) => {
			let question = parseData(item);
			return `<div class="toiec-question">
			
												
			
											<p class="text-question" id="text_question">
												${getRealIndex(arrayRealQuestion, item)}. ${question.justText}
											</p>
										</div>
										<form class="toiec-form id-${parseFatherData.id}" id="${index}">
											<label class="radio-inline answerTextOption" onclick="selectAnswer(${question.answerAId})">
												<strong>A. </strong>
												<span>${question.answerA}</span>
											</label>
											<label class="radio-inline answerTextOption" onclick="selectAnswer(${question.answerBId})">
												<strong>B. </strong>
												<span>${question.answerB}</span>
											</label>
											<label class="radio-inline answerTextOption" onclick="selectAnswer(${question.answerCId})">
												<strong>C. </strong>
												<span>${question.answerC}</span>
											</label>
											${ question.answerDId ? `
												<label class="radio-inline answerTextOption" onclick="selectAnswer(${question.answerDId})">
													<strong>D. </strong>
													<span>${question.answerD}</span>
												</label>` : ''}
										</form>`;
		}).join("")
		}
			</div>
		</div>
	`;

	if (indexPart < 5) {
		playAudio(indexQ);
	} else if ($('button.numberInQuestion' + parseFatherData.id).length > 0) {
		addEventForNumberInQuestion(parseFatherData.id);
	}
}

async function playAudio(indexQ) {
	stopAudio();
	var audioQuestion = _("audioQuestion");

	audioQuestion.addEventListener("loadedmetadata", () => {
		TIME_REMAINING_QUESTION_SECOND = parseInt(audioQuestion.duration) + 3;
		let time_question = _("time_question");

		if (time_question) {
			time_question.innerHTML = TIME_REMAINING_QUESTION_SECOND;
		}
	});

	audioQuestion.onerror = function (e) {
		errorAlert(error_retry);
		clearInterval(counterTimeToeic);

		_("retry").onclick = function () {
			//$('.offline-ui').removeClass().addClass('offline-ui')
			errorAudioRetry = true;
			toastr.remove();
			renderToeic(mainData);
			timer(TIME_REMAINING_SECOND, timeToeicId);
		};
	};

	audioQuestion.addEventListener("play", () => {
		isReady = true;
	});

	if (partsMetaData.hasOwnProperty(currentPart)) {
		var metaNumber = partsMetaData[currentPart]["number"];
		var metaLook = partsMetaData[currentPart]["look-at-number"];
		if (metaNumber) {
			if (metaNumber[indexQ]) {
				if (window.indexedDB && loadFilePermission) {
					let result = await getFromDb(metaNumber[indexQ] + userEid + idAssessment + indexPart + indexQ, "url").catch((e) => {
						console.log("error :", e, question.audio);
					});

					if (localNumberUrl) {
						window.URL.revokeObjectURL(localNumberUrl);
					}
					if (result) {
						localNumberUrl = window.URL.createObjectURL(result.blob);
						audioNumber.src = localNumberUrl;
					} else {
						audioNumber.src = metaNumber[indexQ];
					}
				} else {
					audioNumber.src = metaNumber[indexQ];
				}

				audioNumber.play();
				if (metaLook) {
					if (metaLook[indexQ]) {
						if (window.indexedDB && loadFilePermission) {
							let result = await getFromDb(metaLook[indexQ] + userEid + idAssessment + indexPart + indexQ, "url").catch((e) => {
								console.log("error :", e, question.audio);
							});

							if (localLookUrl) {
								window.URL.revokeObjectURL(localLookUrl);
							}
							if (result) {
								localLookUrl = window.URL.createObjectURL(result.blob);
								audioLook.src = localLookUrl;
							} else {
								audioLook.src = metaLook[indexQ];
							}
						} else {
							audioLook.src = metaLook[indexQ];
						}

						playLook = true;
					} else {
						audioLook.src = "";
						playLook = false;
					}
				} else {
					audioLook.src = "";
					playLook = false;
				}
			} else {
				audioNumber.src = "";
				audioQuestion.play();
			}
		} else {
			audioNumber.src = "";
			audioQuestion.play();
		}
	} else {
		audioNumber.src = "";
		audioLook.src = "";
		audioQuestion.play();
	}
}

function renderAnswerSheet(arrayRealQuestion) {

	for (var i = 0; i < arrayRealQuestion.length; i++) {
		let numTitle;
		if (i < 9) {
			numTitle = "0" + (i + 1).toString();
		} else {
			numTitle = (i + 1).toString();
		}
		answer_sheet.innerHTML += `
		<div class="toeic-form" id="answer_form_${i + 1}" ${(i < 99) ? `style="margin-left: 9px;"` : ''}>
			<label class="ques-no">${numTitle}.</label>
			<label class="radio-inline">
				<input type="radio" tabindex="-1" attrId="${arrayRealQuestion[i].id}" poolName="${arrayRealQuestion[i].poolName}" answerId="${arrayRealQuestion[i].answers.answerAId}" num="${i + 1}" name="optradio[${arrayRealQuestion[i].id}]" value="A">
				<strong>A</strong>
			</label>
			<label class="radio-inline">
				<input type="radio" tabindex="-1" attrId="${arrayRealQuestion[i].id}" poolName="${arrayRealQuestion[i].poolName}" answerId="${arrayRealQuestion[i].answers.answerBId}" num="${i + 1}" name="optradio[${arrayRealQuestion[i].id}]" value="B">
				<strong>B</strong>
			</label>
			<label class="radio-inline">
				<input type="radio" tabindex="-1" attrId="${arrayRealQuestion[i].id}" poolName="${arrayRealQuestion[i].poolName}" answerId="${arrayRealQuestion[i].answers.answerCId}" num="${i + 1}" name="optradio[${arrayRealQuestion[i].id}]" value="C">
				<strong>C</strong>
			</label>
			<label class="radio-inline">
				${arrayRealQuestion[i].poolName.slice(-1) != "2" ? `<input type="radio" tabindex="-1" attrId="${arrayRealQuestion[i].id}" poolName="${arrayRealQuestion[i].poolName}" answerId="${arrayRealQuestion[i].answers.answerDId}" num="${i + 1}" name="optradio[${arrayRealQuestion[i].id}]" value="D"><strong>D</strong>` : ''}
			</label>
		</div>
	`;
	}

	$('#answer_form_' + currentQuestionNum).addClass('selected-border');

	// Event for radio checked
	$("input[type='radio']").on('change', function (e) {
		$(this).blur();
		getCheckedRadio($(this).attr("attrId"), $(this).attr("answerId"), $(this).attr("poolName"), $(this).attr("num"), $(this).val());
		checkAndMarkBorderMultipleQuestion($(this).attr("attrId"));
		if (autoSave10Second) {
			// saveItemToQuery($(this).attr("attrId"), $(this).attr("answerId"), $(this).attr("poolName"), $(this).attr("num"), $(this).val());
			saveItemToQuery();
		}

		savedItems = false;
	});

	for (var j = 0; j < arrayRealQuestion.length; j++) {
		// Set checked
		let temId = arrayRealQuestion[j].id;
		$('input[attrId= ' + temId + ']').each(function (index) {
			if (savedAnswers) {
				if (savedAnswers.items) {
					savedAnswers.items.forEach(answer => {
						if (answer.answerId == $(this).attr("answerId")) {
							$(this).click();
						}
					});
				}
			}
		});
	}

	renderedSavedAnswers = true;
	settingWindowResize();
}

function getRealQuestion(data) {
	var arrayRealQuestion = [];

	// Logic
	if (data.part1) loopQuestionSingle(data.part1, arrayRealQuestion);
	if (data.part2) loopQuestionSingle(data.part2, arrayRealQuestion);
	if (data.part3) loopQuestionMulti(data.part3, arrayRealQuestion);
	if (data.part4) loopQuestionMulti(data.part4, arrayRealQuestion);
	if (data.part5) loopQuestionSingle(data.part5, arrayRealQuestion);
	if (data.part6) loopQuestionMulti(data.part6, arrayRealQuestion);
	if (data.part7) loopQuestionMulti(data.part7, arrayRealQuestion);

	function loopQuestionSingle(part, array) {
		part.forEach(item => {
			array.push(item);
		});
	}

	function loopQuestionMulti(part, array) {
		part.forEach(item => {
			item.sort(function (a, b) {

				return parseInt(a.objective.trim().slice(-1)) - parseInt(b.objective.trim().slice(-1));
			});

			item.forEach(question => {
				if (!isValidPatternX_Y(question.objective)) {
					array.push(question);
				}
			});
		});
	}

	return arrayRealQuestion;
}

function getRealIndex(data, value) {
	let index = 0;

	if (data && value) {
		index = data.findIndex(item => item.id === value.id) + 1;
	}

	return index;
}

function setTitlePart(poolName) {
	var titlePart;
	let partName = poolName.slice(-1);
	switch (partName) {
		case "1":
			titlePart = "Part 1: Picture description";
			// TIME_REMAINING_QUESTION_SECOND = 26;
			break;
		case "2":
			titlePart = "Part 2: Question and Response";
			// TIME_REMAINING_QUESTION_SECOND = 26;
			break;
		case "3":
			titlePart = "Part 3: Short conversation";
			// TIME_REMAINING_QUESTION_SECOND = 83;
			break;
		case "4":
			titlePart = "Part 4: Short talk";
			// TIME_REMAINING_QUESTION_SECOND = 91;
			break;
		case "5":
			titlePart = "Part 5: Incomplete sentences";
			break;
		case "6":
			titlePart = "Part 6: Text completion";
			break;
		case "7":
			titlePart = "Part 7: Passages";
			break;
		default:
			titlePart = "Part ??: ??";
	}

	return titlePart;
}

// For go to next question
function nextQuestion(skipIntro) {
	let dataPart = mainData["part" + indexPart];
	let lengthPart = dataPart.length;

	if (indexPart <= 7 && indexQuestion < lengthPart && !(indexPart == 7 && indexQuestion == (lengthPart - 1))) {
		if (errorAudioRetry) {
			toastr.remove();
			timer(TIME_REMAINING_SECOND, timeToeicId);
			errorAudioRetry = false;
		}

		if ($('div[role="tooltip"]').is(":visible")) {
			$('div[role="tooltip"]').hide();
		}

		if (indexPart > 4 && $('#dlgAudioText:visible').length) {
			$("#dlgAudioText").dialog("close");
		}

		if ($('#dlgImg:visible').length) {
			$("#dlgImg").dialog("close");
		}

		let ended = false;
		isReady = false;

		if (!skipIntro) {
			indexQuestion++;
		}

		if (indexQuestion > lengthPart - 1) {
			if (indexPart != 7) {
				indexPart++;
				indexQuestion = 0;
			}
			if (!mainData["part" + indexPart]) {

				// reset to last question
				--indexPart;
				ended = true;
				indexQuestion = (mainData["part" + indexPart].length - 1);

				// check if there is any part forward.
				for (var j = (indexPart + 1); j <= 7; j++) {
					if (mainData["part" + j]) {
						indexPart = j;
						ended = false;
						indexQuestion = 0;
						break;
					}
				}
			}
		}

		if ((indexQuestion == lengthPart - 1) && indexPart == 7) {
			nextBtn.classList.add("hidden");
			// submitBtn.classList.remove("hidden");
		}

		currentPart = "part" + indexPart;

		if (indexPart > 4) {
			if ($('#dlgImg:visible').length) {
				$("#dlgImg").dialog("close");
			}
		}

		if (!ended) {
			if (template || indexPart < 5) {
				renderToeic(mainData);
			} else if (!template && indexPart >= 5) {
				nextBtn.classList.add("hidden");
				renderPBP(indexPart);
			}

			saveAssessmentData();
		}
	}
}

// For go to previous question
function prevQuestion() {
	if (indexPart > 4 && !(indexPart == 5 && indexQuestion == 0)) {
		if ($('div[role="tooltip"]').is(":visible")) {
			$('div[role="tooltip"]').hide();
		}

		if ($('#dlgAudioText:visible').length) {
			$("#dlgAudioText").dialog("close");
		}

		indexQuestion--;
		let isBacked = false;

		let ended = false;

		if (indexQuestion < 0) {
			(indexPart > 1) ? indexPart-- : indexPart;
			isBacked = true;

			if (!mainData["part" + indexPart]) {

				// reset back to last question.
				++indexPart;
				//let lengthPart = mainData["part" + indexPart].length;
				indexQuestion = 0;
				isBacked = false;
				ended = true;

				// check if there is other part behind current part.
				for (var j = (indexPart - 1); j > 0; j--) {
					if (mainData["part" + j]) {
						indexPart = j;
						indexQuestion = (mainData["part" + indexPart].length - 1);
						ended = false;
						isBacked = true;
						break;
					}
				}
			}
		}

		let dataPart = mainData["part" + indexPart];
		currentPart = "part" + indexPart;

		let lengthPart = dataPart.length;
		// nextBtn.classList.remove("hidden");

		(isBacked) ? indexQuestion = (lengthPart - 1) : indexQuestion;



		if (!ended) {
			renderToeic(mainData);
			saveAssessmentData();
		}
	}
}

function markColorAnswerSheet(firstParam, secondParam) {
	// console.log('firstParam ' + firstParam + ' secondParam ' + secondParam);

	// set hightlight border for selecting question.
	currentQuestionNum = firstParam;
	markBoder();

	var selectedQuestion;
	// reset
	for (var i = 0; i < toeicForm.length; i++) {
		toeicForm[i].classList.remove("mark-color");
	}

	if (firstParam && secondParam) {
		for (var i = firstParam; i <= secondParam; i++) {
			// console.log('answer_form_' + i);
			selectedQuestion = _('answer_form_' + i);
			selectedQuestion.classList.add("mark-color");
		}
	} else if (firstParam && !secondParam) {
		selectedQuestion = _('answer_form_' + firstParam);
		selectedQuestion.classList.add("mark-color");
	} else {
		return;
	}

	// Auto Scroll AnswerSheet
	answer_sheet.scrollTop = _('answer_form_' + firstParam).offsetTop - 60;
}

function timer(duration, blockId) {
	if (counterTimeToeic) {
		clearInterval(counterTimeToeic);
	}
	var timer = duration,
		minutes, seconds;
	counterTimeToeic = setInterval(function () {
		minutes = parseInt(timer / 60, 10);
		seconds = parseInt(timer % 60, 10);

		minutes = minutes < 10 ? "0" + minutes : minutes;
		seconds = seconds < 10 ? "0" + seconds : seconds;

		blockId.innerHTML = minutes + ":" + seconds;
		--TIME_REMAINING_SECOND;
		if (--timer < 0) {
			//timer = duration;
			clearInterval(counterTimeToeic);
			submitForGrading(true, true);
		}
		// auto next
		if (indexPart < 5) {
			autoNextQuestion();
		}

		saveAssessmentData();
	}, 1000);
}

function autoNextQuestion() {
	if (isReady) {
		TIME_REMAINING_QUESTION_SECOND--;
		if (TIME_REMAINING_QUESTION_SECOND < 0) {
			// TIME_REMAINING_QUESTION_SECOND = null;
			nextQuestion();
		}

		let time_question = _("time_question");

		if (time_question) {
			time_question.innerHTML = (TIME_REMAINING_QUESTION_SECOND < 0) ? 0 : TIME_REMAINING_QUESTION_SECOND;
		}
	}
}


function finishToeic(data) {
	renderResult(data);
	resetDataToeic();
}

function resetDataToeic() {

	indexPart = 1;
	indexQuestion = 0;
	arrSelectedAnswers = [];

	question_contain.innerHTML = '';
	answer_sheet.innerHTML = '';
	// result_page.innerHTML = '';

	clearInterval(counterTimeToeic);

	localStorage.removeItem('infoMyTest' + idAssessment);
	localStorage.removeItem('items' + idAssessment);
}

// External function to handle all radio selections
function getCheckedRadio(id, answerId, pool, number, label) {
	let index = arrSelectedAnswers.findIndex(item => item.itemId == id);

	if (index > -1) {
		arrSelectedAnswers.splice(index, 1);
	}

	// Push id value to array answer
	arrSelectedAnswers.push({
		itemId: id,
		poolName: pool,
		answerId: answerId,
		answerText: number + "-" + label,
		assessmentGradingId: assessmentData.assessment.assessmentGradingId
	});

	localStorage.setItem('items' + idAssessment, JSON.stringify(arrSelectedAnswers));
}

// function saveItemToQuery(id, answerId, pool, number, label) {
function saveItemToQuery() {
	if (renderedSavedAnswers) {
		if (savingItem) {
			console.log("call saveItemToQueryAndSaveDb() after 3s");
			setTimeout(() => {
				saveItemToQueryAndSaveDb();
			}, 3000);
		} else {
			saveItemToQueryAndSaveDb();
		}
	}
}

function saveItemToQueryAndSaveDb(id, answerId, pool, number, label) {
	// console.log("doing saveItemToQueryAndSaveDb()...");
	// let index = itemToSaveQuery.findIndex(item => item.itemId == id);

	// let item = {
	// 	itemId: id,
	// 	poolName: pool,
	// 	answerId: answerId,
	// 	answerText: number + "-" + label,
	// 	assessmentGradingId: assessmentData.assessment.assessmentGradingId
	// };

	// if (index > -1) {
	// 	itemToSaveQuery.splice(index, 1, item);
	// } else {
	// 	itemToSaveQuery.push(item);
	// }

	if (saveQueryInterval) {
		clearInterval(saveQueryInterval);
		console.log("call clearInterval(saveQueryInterval);");
	}

	saveQueryInterval = setInterval(() => {
		submit(false);
	}, 10000);
}

function saveItemQueryToDatabase() {
	if (itemToSaveQuery.length) {
		console.log("saving item query");
		$("#submitBtn").prop("disabled", true);
		$("#saveBtn").prop("disabled", true);
		savingItem = true;

		$.ajax({
			type: "POST",
			url: "save-itemGradingList",
			data: JSON.stringify(itemToSaveQuery),
			dataType: 'json',
			contentType: "application/json",
			success: function (data) {
				if (data) {
					console.log("save item query success");

					itemToSaveQuery = [];
					clearInterval(saveQueryInterval);
					savingItem = false;
					savedItems = true;

					$("#submitBtn").prop("disabled", false);
					$("#saveBtn").prop("disabled", false);
				}
			},
			error: function (e) {
				if (e.status == 200) {
					console.log("save item query success");
					itemToSaveQuery = [];
					clearInterval(saveQueryInterval);
					savingItem = false;
					savedItems = true;

					$("#submitBtn").prop("disabled", false);
					$("#saveBtn").prop("disabled", false);
				} else {
					console.log("ERROR: ", e);
					errorAlert(error);
					savingItem = false;
					$("#submitBtn").prop("disabled", false);
					$("#saveBtn").prop("disabled", false);
				}
			}
		});
	}
}

function getLengthEachPart() {
	arrayRealQuestion.forEach(item => {
		let partName = item.poolName.slice(-1);
		switch (partName) {
			case '1':
				lengthPartAll.part1 += 1;
				break;
			case '2':
				lengthPartAll.part2 += 1;
				break;
			case '3':
				lengthPartAll.part3 += 1;
				break;
			case '4':
				lengthPartAll.part4 += 1;
				break;
			case '5':
				lengthPartAll.part5 += 1;
				break;
			case '6':
				lengthPartAll.part6 += 1;
				break;
			case '7':
				lengthPartAll.part7 += 1;
				break;
		}
	});
}

function renderResult(data) {

	if ($(start_page).is(":visible")) {
		start_page.classList.add("hidden");
	}

	test_page.classList.add("hidden");
	result_page.classList.remove("hidden");
	let exportUrl = "export?assessmentGradingId=" + data.assessmentGradingId;
	result_page.innerHTML += `
		<div class="row">
			<div class="col-sm-6">
				 <canvas id="radar-chart" height="400"></canvas>
			</div>
			<div class="col-sm-6">
				<div class="info-result">
					<p><b>Time Limit:</b> ${timeLimit_minute} Minutes</p>
					<h3><b>Listening Score:</b> ${data.listeningScore} - <b>Reading Score:</b> ${data.readingScore}</h3>					
					<h2>TOTAL SCORE <span class="score-toeic">${data.finalScore}</span></h2>
					${data.toeicGeneralFeedback ? `
						<h3>CEFR LEVEL</h3>
						<p><strong>${data.toeicGeneralFeedback.cefrLevel}</strong></p>
						<p>${data.toeicGeneralFeedback.levelText}</p>
						<h3>General Description</h3>
						<ul id="gDes" style="text-align: left;">
						</ul>` : ''}
					<a class="btn btn-warning" href="home" style="margin-top: 7px;">Home</a>
					${(assessmentData.assessment.practice) ? `<a class="btn btn-success" href="result?idA=${idAssessment}&idG=${assessmentData.assessment.assessmentGradingId}" style="margin-top: 7px;">Result</a>` : ''}
					<button class="btn btn-info" id="exportExcel" value="${exportUrl}" onclick="downloadFile(this)">Export</button>		
				</div>
			</div>
		</div>
		
		<div class="col-sm-12" id="detailFeedback">
		</div>
			`;

	if (data.toeicGeneralFeedback) {
		var generalDescriptions = data.toeicGeneralFeedback.levelDescription.split("-");

		if (generalDescriptions.length > 0) {
			var gDes = _("gDes");
			for (let p = 0; p < generalDescriptions.length; p++) {
				let text = generalDescriptions[p].trim();
				gDes.innerHTML += "<li>" + text + "</li>";
			}

		}
	}

	var detail_feedback = _("detailFeedback");

	// LISTENING FEEDBACK
	if (data.toeicDetailFeedback) {
		if (data.toeicDetailFeedback[0]) {
			var listeningStrength = data.toeicDetailFeedback[0].strength.split("-");
			var listeningWeakness = data.toeicDetailFeedback[0].weakness.split("-");

			detail_feedback.innerHTML += `
				<h3>Listening</h3>
				<div class="row">
					<div class="col-sm-6">
						<h4>STRENGTH</h4>
						<p style="color: blue"><strong>Test takers who score around ${data.toeicDetailFeedback[0].levelPoint} typically have the following strengths:</strong></p>
						<ul id="lStrength" style="text-align: left;">
						</ul>
					</div>
			`;

			if (listeningStrength.length > 0) {

				for (let i = 0; i < listeningStrength.length; i++) {
					let text = listeningStrength[i].trim();

					_("lStrength").innerHTML += "<li>" + text + "</li>";
				}

			}

			if (listeningWeakness.length > 0) {
				detail_feedback.innerHTML += `
					<div class="col-sm-6">
						<h4>WEAKNESS</h4>
						<p style="color: blue"><strong>Test takers who score around ${data.toeicDetailFeedback[0].levelPoint} typically have the following weaknesses:</strong></p>
						<ul id="lWeakness">
						</ul>
					</div>
						`;

				for (let i = 0; i < listeningWeakness.length; i++) {
					let text = listeningWeakness[i].trim();

					_("lWeakness").innerHTML += "<li>" + text + "</li>";
				}
			}
		}

		// READING FEEDBACK
		if (data.toeicDetailFeedback[1]) {
			var readingStrength = data.toeicDetailFeedback[1].strength.split("-");
			var readingWeakness = data.toeicDetailFeedback[1].weakness.split("-");

			detail_feedback.innerHTML += `
				<h3>Reading</h3>
				<div class="row">
					<div class="col-sm-6">
						<h4>STRENGTH</h4>
						<p style="color: blue"><strong>Test takers who score around ${data.toeicDetailFeedback[1].levelPoint} typically have the following strengths:</strong></p>
						<ul id="rStrength">
						</ul>
					</div>
			`;

			if (readingStrength.length > 0) {
				for (let i = 0; i < readingStrength.length; i++) {
					let text = readingStrength[i];

					_("rStrength").innerHTML += "<li>" + text + "</li>";
				}
			}

			if (readingWeakness.length > 0) {
				detail_feedback.innerHTML += `
					<div class="col-sm-6">
						<h4>WEAKNESS</h4>
						<p style="color: blue"><strong>Test takers who score around ${data.toeicDetailFeedback[1].levelPoint} typically have the following weaknesses:</strong></p>
						<ul id="rWeakness">
						</ul>
					</div>`;

				for (let i = 0; i < readingWeakness.length; i++) {
					let text = readingWeakness[i];

					_("rWeakness").innerHTML += "<li>" + text + "</li>";
				}

			}

		}

	}

	drawScoreChart(data);
}

function submitForGrading(isSubmit, isAutoSubmit, pastDueDate) {
	if (isSubmit) {
		if (arrSelectedAnswers.length != arrayRealQuestion.length) {
			var arrNum = [];

			arrayRealQuestion.forEach((item, index) => {
				let i = arrSelectedAnswers.find(q => q.itemId == item.id);

				if (!i) {
					arrNum.push(index + 1);
				}
			});

			if (isAutoSubmit) {
				if (pastDueDate) {
					$('#confirmModalTitle').html("This assessment already past due date, the exam will be automatically submitted");
				} else {
					$('#confirmModalTitle').html("Run out of time, the exam will be automatically submitted");
				}
				$('#submitModalBtn').hide();
				$('#cancelModalBtn').html("Ok");
			} else {
				$('#confirmModalTitle').html("Are you sure you still want to submit?");
				$('#submitModalBtn').show();
				$('#cancelModalBtn').html("Cancel");
			}
			$("#confirmTitle").html("You have " + (arrayRealQuestion.length - arrSelectedAnswers.length) + " questions left unanswered");
			$("#questionNum").html(arrNum.join(", "));

			$("#dlgConfirm").modal("show");
		} else {
			if (isAutoSubmit) {
				$('#confirmModalTitle').html("Run out of time, the exam will be automatically submitted");
				$("#confirmTitle").html("You have " + (arrayRealQuestion.length - arrSelectedAnswers.length) + " questions left unanswered");
				$('#submitModalBtn').hide();
				$('#cancelModalBtn').html("Ok");
			} else {
				$('#confirmModalTitle').html("Are you sure you still want to submit?");
				$("#confirmTitle").html("Did you check your answer again?");
			}

			$("#questionNum").html("");
			$("#dlgConfirm").modal("show");
		}

		if (isAutoSubmit) {
			submit(isSubmit, isAutoSubmit);
		}
	} else {
		submit(isSubmit, isAutoSubmit);
	}
}

function submit(isSubmit, isAutoSubmit) {
	if (saveQueryInterval) {
		clearInterval(saveQueryInterval);
		console.log("call clearInterval(saveQueryInterval);");
	}

	if (isSubmit) {
		waitingAlert(wait_submit);
		$("input:radio").prop("disabled", true);
		stopAudio();
		deleteFromDbWithRange(1);
		if ($('#dlgImg:visible').length) {
			$("#dlgImg").dialog("close");
		}

		if ($('#dlgAudioText:visible').length) {
			$("#dlgAudioText").dialog("close");
		}

	} else {
		waitingAlert(wait_save);
	}
	savingItem = true;
	//$("#loading").fadeIn().css("display", "inline-block");
	$("#submitBtn").prop("disabled", true);
	$("#saveBtn").prop("disabled", true);

	var data = {};
	var time;

	if (isAutoSubmit) {
		url = "submit-test?isSubmit=" + isSubmit + "&isAutoSubmit=" + isAutoSubmit;

		time = timeLimit_minute * 60;
	} else {
		url = "submit-test?isSubmit=" + isSubmit + "&isAutoSubmit=" + false;

		time = timeToeicId.innerHTML.split(':');
		time = (parseInt(time[0]) * 60) + parseInt(time[1]);
		time = (timeLimit_minute * 60) - time;
	}


	data['assessmentId'] = idAssessment;
	if (!savedItems && !isSubmit) {
		data['items'] = arrSelectedAnswers;
	} else if (isSubmit) {
		data['items'] = arrSelectedAnswers;
	}
	data['timeElapsed'] = time;
	data['lastVisitedPart'] = indexPart;
	data['lastVisitedQuestion'] = indexQuestion;
	data['assessmentGradingId'] = assessmentData.assessment.assessmentGradingId;
	var url;

	$.ajax({
		type: "POST",
		url: url,
		data: JSON.stringify(data),
		dataType: 'json',
		contentType: "application/json",
		success: function (result) {
			itemToSaveQuery = [];
			savingItem = false;
			localStorage.removeItem('items' + idAssessment);
			localStorage.removeItem('infoMyTest' + idAssessment);
			if (result.status == "Success") {
				//$("#loading").css("display", "none")
				$('#result').fadeToggle('slow').css("display", "inline-block");
				$('#result').fadeOut(5000);
				$("#submitBtn").prop("disabled", false);
				$("#saveBtn").prop("disabled", false);

				savedItems = true;
				successAlert();
			} else {
				submitted = true;
				feedback = result;
				finishToeic(result);
				successAlert();
			}
		},
		error: function (e) {
			console.log("ERROR: ", e);
			savingItem = false;
			errorAlert(error);

			$("#submitBtn").prop("disabled", false);
			$("#saveBtn").prop("disabled", false);
			if (isSubmit && TIME_REMAINING_SECOND >= 0) {
				$("input:radio").prop("disabled", false);
			}
		}
	});
}

function autoSaveAssessment() {
	$(window).on('beforeunload', function () {
		if (isStart && !submitted) {
			submit(false);
		} else if (submitted) {
			localStorage.removeItem('items' + idAssessment);
			localStorage.removeItem('infoMyTest' + idAssessment);
		}
		if (savedItems) {
			localStorage.removeItem('items' + idAssessment);
		}
	});
}

function stopAudio() {

	if (!audioNumber.paused) {
		audioNumber.pause();
	}

	if (!audioLook.paused) {
		audioLook.pause();
	}

	if (_("direction")) {
		// (_("direction").paused == false) ? _("direction").currentTime = _("direction").duration : '';
		(_("direction").paused == false) ? _("direction").pause() : '';
		try {
			audio.pause();
		} catch (e) {

		}
	}

	if (_("audioQuestion")) {
		(_("audioQuestion").paused == false) ? _("audioQuestion").pause() : '';
	}
}

function selectAnswer(answerID) {
	$(`input:radio[answerid="${answerID}"]`).click();
	let dataQ;
	if (!template) {
		if (indexPart == 1 || indexPart == 2 || indexPart == 5) {
			let index = mainData["part" + indexPart].findIndex(i => i.answers.answerAId == answerID || i.answers.answerBId == answerID || i.answers.answerCId == answerID || i.answers.answerDId == answerID);
			if (index >= 0) {
				indexQuestion = index;
				dataQ = mainData["part" + indexPart][index];
			}
		} else if (indexPart == 3 || indexPart == 4 || indexPart == 6 || indexPart == 7) {
			for (var i = 0; i < mainData["part" + indexPart].length; i++) {
				let childQuestion = mainData["part" + indexPart][i].filter((question) => {
					return !isValidPatternX_Y(question.objective);
				});
				let item = childQuestion.find(i => i.answers.answerAId == answerID || i.answers.answerBId == answerID || i.answers.answerCId == answerID || i.answers.answerDId == answerID);
				if (item) {
					indexQuestion = i;
					dataQ = item;
					break;
				}
			}
		} else {
			console.log("Warning: indexPart=" + indexPart + "is not processed.");
		}

		currentQuestionNum = getRealIndex(arrayRealQuestion, dataQ);

		markBoder();
	}
}

function settingAudioTextDialog() {
	$(".default-dialog").dialog({
		autoOpen: false,
		width: 'auto',
		height: 'auto',
		maxHeight: 700,
		create: function (event, ui) {
			$(this).css("maxWidth", "400px");
		},
		// modal: true, // true = cannot be interacted with other item.
		position: {
			my: "center", // clicked item's position.
			at: "left" // modal's position.
		}
	});

	$('div[aria-describedby="dlgAudioText"]').css("zIndex", 9999, "important");
}

function settingRankDialog() {
	$("#rankDlg").dialog({
		autoOpen: false,
		width: 'auto',
		height: 'auto',
		maxHeight: 700,
		// modal: true, // true = cannot be interacted with other item.
		position: {
			my: "center", // clicked item's position.
			at: "center" // modal's position.
		}
	});

	$('a[href="ranks"]').on("click", function (e) {
		e.preventDefault();
		$("#rankDlg").dialog({
			width: 500,
			height: 700
		}).dialog("open");
	});

	$('div[aria-describedby="rankDlg"]').css("zIndex", 9999, "important");
}

function addFeedbackTextForDialog(data) {
	let feedback = "";

	if (data.length > 0) {
		for (var i = 0; i < data.length; i++) {
			let ans = data[i];
			feedback = feedback + `
									${(ans.answers.answerAFeedback) ? `<p>${ans.answers.answerAFeedback}</p>` : ''}
									${(ans.answers.answerBFeedback) ? `<p>${ans.answers.answerBFeedback}</p>` : ''}
									${(ans.answers.answerCFeedback) ? `<p>${ans.answers.answerCFeedback}</p>` : ''}
									${(ans.answers.answerDFeedback) ? `<p>${ans.answers.answerDFeedback}</p>` : ''}
								  `;
		}
	} else {
		feedback = `
						${(data.answers.answerAFeedback) ? `<p>${data.answers.answerAFeedback}</p>` : ''}
						${(data.answers.answerBFeedback) ? `<p>${data.answers.answerBFeedback}</p>` : ''}
						${(data.answers.answerCFeedback) ? `<p>${data.answers.answerCFeedback}</p>` : ''}
						${(data.answers.answerDFeedback) ? `<p>${data.answers.answerDFeedback}</p>` : ''}
					`;
	}

	$('#dlgAudioText').html(feedback);
}

function showImage(img, isToolbar) {
	_("imgQ").src = img.src;

	if (isToolbar) {
		$(".full-width-dialog").dialog({
			width: 500,
			height: 700
		}).dialog("open");
	} else {
		$(".full-width-dialog").dialog({
			width: "auto",
			height: "auto"
		}).dialog("open");
	}

	$("#imgQ").width($(".full-width-dialog.ui-dialog-content.ui-widget-content").width());

	if (isToolbar) {
		$(".full-width-dialog").dialog("widget").position({
			my: 'top',
			at: 'top',
			of: window
		});
	} else {
		$(".full-width-dialog").dialog("widget").position({
			my: 'center',
			at: 'center',
			of: window
		});
	}
}

function showAudioTextDialog() {
	$(".default-dialog").dialog("open");
}

function settingRNotesInToolbar() {
	$('a[href="rNotes"]').on("click", function (e) {
		e.preventDefault();
		$("#rNotes").modal("show");
	});
}

function settingImageDialog() {
	$(".full-width-dialog").dialog({
		autoOpen: false,
		width: 'auto',
		height: 'auto',
		maxHeight: 750,
		position: {
			my: "center", // clicked item's position.
			at: "center" // modal's position.
		},
		resize: function (event, ui) {
			$("#imgQ").width($(".full-width-dialog.ui-dialog-content.ui-widget-content").width());
		}
	});

	$('div[aria-describedby="dlgImg"]').css("zIndex", 9999, "important");

	$('#dlgImg').on('dialogclose', function (event) {
		_("imgQ").src = '';
	});
}

function settingHelpToolbar() {

	$('div[data-toolbar="set-04"]').toolbar({
		content: '#transport-options',
		position: 'top',
		style: 'primary',
		animation: 'flyin'
	});

	$('div[data-toolbar="set-04"]').on('toolbarShown',
		function (event) {
			$('.tool-container').css("zIndex", 9999, "important");
		}
	);

	$('a[href="score"]').on('click', function (e) {
		e.preventDefault();
		let imgSrc = window.location.pathname.split("/");
		imgSrc.pop();
		imgSrc = 'resources/images/score_converted.jpg';

		imgSrc = {
			src: imgSrc
		};

		showImage(imgSrc, true);
	});

	$('a[href="usage"]').on('click', function (e) {
		e.preventDefault();
		let imgSrc = window.location.pathname.split("/");
		imgSrc.pop();
		imgSrc = 'resources/images/usage.jpg';

		imgSrc = {
			src: imgSrc
		};

		showImage(imgSrc, true);
	});
}

function settingComfirnModalDialog() {
	$('#dlgConfirm').on('show.bs.modal	', function (e) {
		$("#submitBtn").prop("disabled", true);
		$("#saveBtn").prop("disabled", true);
	});

	$('#dlgConfirm').on('hidden.bs.modal', function (e) {
		$("#submitBtn").prop("disabled", false);
		$("#saveBtn").prop("disabled", false);
	});
}

function showQuestionMulti(infoQuestion, indexQ, lastIndexQ) {
	if (infoQuestion.justText) {
		const reg = /[\(][0-9]{2,3}[\)]/g;
		const regQuestionNumberXToY = /[0-9]{2,3}[-]{1}[0-9]{2,3}/g;

		let match = infoQuestion.justText.match(regQuestionNumberXToY);
		if (match) {
			infoQuestion.justText = infoQuestion.justText.replace(match[0], indexQ + "-" + lastIndexQ);
		}

		let matches = infoQuestion.justText.match(reg);
		if (matches) {
			for (var i = 0; i < matches.length; i++) {
				let replaceStr = `<button style="background: lightgray !important; margin-left: 0; margin-right: 0;" class="numberInQuestion${infoQuestion.id}" data-html="true" title="#" id="${i}"><span style="color: black;">${indexQ++}</span></button>`;
				infoQuestion.justText = infoQuestion.justText.replace(matches[i], replaceStr);
			}
		} else {
			return infoQuestion;
		}
	}

	return infoQuestion;
}

function addEventForNumberInQuestion(Qid) {
	let buttonNumberInQuestion = $('button.numberInQuestion' + Qid);

	buttonNumberInQuestion.click(function (e) {
		e.preventDefault();
	});

	for (var i = 0; i < buttonNumberInQuestion.length; i++) {
		let id = $(buttonNumberInQuestion[i]).attr('id');
		let titleContent = "";

		let form = $(".toiec-form.id-" + Qid).filter(function (index) {
			return $(this).attr('id') == id;
		});

		form.contents().each(function (index, obj) {
			if (this.nodeType == 1) {
				titleContent = titleContent + `<p> ${$(this).text()} <p>`;
			}
		});

		$(buttonNumberInQuestion[i]).prop('title', titleContent);
	}

	$('button.numberInQuestion' + Qid).tooltip({
		show: {
			effect: "slideDown",
			delay: 250
		},
		content: function () {
			return $(this).prop('title');
		}
	});
}

function addScrollTojQuery() {
	jQuery.fn.scrollTo = function (elem) {
		//$(this).scrollTop($(this).scrollTop() - $(this).offset().top + $(elem).offset().top - 60);
		$(this).stop();
		$(this).animate({
			scrollTop: $(this).scrollTop() - $(this).offset().top + $(elem).offset().top - 60
		}, 500);

		return this;
	};
}

function selectRadioWithKey(key) {
	$('.selected-border').find('input').each(function (index) {
		if ($(this).attr('value') == key) {
			$(this).click();
		}
	});
}

function checkAndMarkBorderMultipleQuestion(id) {
	let data = mainData["part" + indexPart][indexQuestion];

	if (data.length) {
		let childQuestion = data.filter((question) => {
			return !isValidPatternX_Y(question.objective);
		}).sort(function (a, b) {

			return parseInt(a.objective.trim().slice(-1)) - parseInt(b.objective.trim().slice(-1));
		});

		let index = childQuestion.findIndex(i => i.id == id);

		let lastChildIndex = getRealIndex(arrayRealQuestion, childQuestion[childQuestion.length - 1]);

		if (index > -1 && index < childQuestion.length - 1 && currentQuestionNum < lastChildIndex) {
			++currentQuestionNum;
			markBoder();
		}
	}
}

function markBoder() {
	$('.selected-border').removeClass('selected-border');

	let divId = '#answer_form_' + currentQuestionNum;
	$(divId).addClass('selected-border');
	$("#answer_sheet").scrollTo(divId);
	$("#question_contain > div.test-box").focus();
	//answer_sheet.scrollTop = _('answer_form_' + currentQuestionNum).offsetTop - 60;
}

function shortcutKeyEvent() {
	document.body.onkeyup = function (e) {
		let dataPart = mainData["part" + indexPart];
		let lengthPart = dataPart.length;

		if (playedIntro && !playingIntro) {
			if (e.key == "ArrowLeft" && (template || indexPart < 5)) {
				e.preventDefault();

				// if part > 4 AND not first question of part 5
				if (indexPart > 4 && !(indexPart == 5 && indexQuestion == 0)) {
					// if (playingDirection) {
					// 	prevQuestion();
					// } else {
					prevQuestion();
					// }
				}
			} else if (e.key == "ArrowRight" && (template || indexPart < 5)) {
				e.preventDefault();

				// if part <=7 and question is not > last question (meaning not out of question)
				// AND if not last question of part 7
				if (indexPart <= 7 && indexQuestion < lengthPart && !(indexPart == 7 && indexQuestion == (lengthPart - 1))) {
					if (playingDirection) {
						nextQuestion(true);
					} else {
						nextQuestion();
					}
				}
			} else if (e.key == "ArrowUp") {
				e.preventDefault();
				if (arrayRealQuestion) {

					// if index is not first question in answersheet AND index still in list length
					if (currentQuestionNum > 1 && currentQuestionNum <= arrayRealQuestion.length) {
						currentQuestionNum--;
						markBoder();
					}
				}
			} else if (e.key == "ArrowDown") {
				e.preventDefault();
				if (arrayRealQuestion) {

					// if index is not last question in answerSheet AND index still in list length
					if (currentQuestionNum >= 1 && currentQuestionNum < arrayRealQuestion.length) {
						currentQuestionNum++;
						markBoder();
					}
				}
			} else if (e.key == "1" || e.key.toLowerCase() == "a") {
				selectRadioWithKey("A");
			} else if (e.key == "2" || e.key.toLowerCase() == "b") {
				selectRadioWithKey("B");
			} else if (e.key == "3" || e.key.toLowerCase() == "c") {
				selectRadioWithKey("C");
			} else if (e.key == "4" || e.key.toLowerCase() == "d") {
				selectRadioWithKey("D");
			}
		}
	};

	var el = document.getElementsByClassName('question-area')[0];

	swipedetect(el, function (swipedir) {
		let dataPart = mainData["part" + indexPart];
		let lengthPart = dataPart.length;

		//swipedir contains either "none", "left", "right", "top", or "down"
		// swipe from right to left
		if (swipedir == 'right') {
			if (indexPart > 4 && !(indexPart == 5 && indexQuestion == 0)) {
				if (playingDirection) {
					prevQuestion(true);
				} else {
					prevQuestion();
				}
			}

			// swipe from left to right
		} else if (swipedir == 'left') {
			if (indexPart <= 7 && indexQuestion < lengthPart && !(indexPart == 7 && indexQuestion == (lengthPart - 1))) {
				if (playingDirection) {
					nextQuestion(true);
				} else {
					nextQuestion();
				}
			}
		}
	})
}

function saveAssessmentData() {
	var time = timeToeicId.innerHTML.split(':');
	time = (parseInt(time[0]) * 60) + parseInt(time[1]);
	time = (timeLimit_minute * 60) - time;

	// Put the object into storage
	var infoMyTest = {
		assessmentId: idAssessment,
		// items: arrSelectedAnswers,
		timeElapsed: time,
		lastVisitedPart: indexPart,
		lastVisitedQuestion: indexQuestion,
		assessmentGradingId: assessmentData.assessment.assessmentGradingId
	};

	localStorage.setItem('infoMyTest' + idAssessment, JSON.stringify(infoMyTest));
}

function saveLocalData(data) {

	let url = "submit-test?isSubmit=false&isAutoSubmit=false";

	$.ajax({
		type: "POST",
		url: url,
		data: JSON.stringify(data),
		dataType: 'json',
		contentType: "application/json",
		success: function (result) {
			localStorage.removeItem('infoMyTest' + idAssessment);
			localStorage.removeItem('items' + idAssessment);
		},
		error: function (e) {
			console.log("ERROR: ", e);
			errorAlert(error);
		}
	});
}

function downloadFile(url) {
	waitingAlert(wait_download);
	$.ajax({
		url: url.value,
		method: 'GET',
		xhrFields: {
			responseType: 'blob'
		},
		success: function (response, status, xhr) {
			if (xhr.status == 200 && response instanceof Blob) {
				var fileName = "";
				var disposition = xhr.getResponseHeader('Content-Disposition');
				if (disposition && disposition.indexOf('attachment') != -1) {
					var fileNameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
					var matches = fileNameRegex.exec(disposition);

					if (matches != null && matches[1]) {
						fileName = matches[1].replace(/['"]/g, '');
					}
				}

				var a = document.createElement('a');
				var url = window.URL.createObjectURL(response);
				a.href = url;
				a.download = fileName;
				document.body.appendChild(a);
				a.click();
				setTimeout(function () {
					document.body.removeChild(a);
					window.URL.revokeObjectURL(url);
				}, 3000);

				successAlert();

			} else {
				errorAlert(error_download);
			}
		},
		error: function (e) {
			console.log("ERROR: ", e);
			errorAlert(error_download);
		}
	});
}

//function detect swipe for mobile device
function swipedetect(el, callback) {

	var touchsurface = el,
		swipedir,
		startX,
		startY,
		distX,
		distY,
		threshold = 150, //required min distance traveled to be considered swipe
		restraint = 100, // maximum distance allowed at the same time in perpendicular direction
		allowedTime = 300, // maximum time allowed to travel that distance
		elapsedTime,
		startTime,
		handleswipe = callback || function (swipedir) { }

	touchsurface.addEventListener('touchstart', function (e) {
		var touchobj = e.changedTouches[0];
		swipedir = 'none';
		dist = 0;
		startX = touchobj.pageX;
		startY = touchobj.pageY;
		startTime = new Date().getTime(); // record time when finger first makes contact with surface
		//e.preventDefault();
	}, false);

	//    touchsurface.addEventListener('touchmove', function(e) {
	//        e.preventDefault(); // prevent scrolling when inside DIV
	//    }, false);

	touchsurface.addEventListener('touchend', function (e) {
		var touchobj = e.changedTouches[0];
		distX = touchobj.pageX - startX; // get horizontal dist traveled by finger while in contact with surface
		distY = touchobj.pageY - startY; // get vertical dist traveled by finger while in contact with surface
		elapsedTime = new Date().getTime() - startTime; // get time elapsed
		if (elapsedTime <= allowedTime) { // first condition for awipe met
			if (Math.abs(distX) >= threshold && Math.abs(distY) <= restraint) { // 2nd condition for horizontal swipe met
				swipedir = (distX < 0) ? 'left' : 'right'; // if dist traveled is negative, it indicates left swipe
				if (e.cancelable) {
					e.preventDefault();
				}
			} else if (Math.abs(distY) >= threshold && Math.abs(distX) <= restraint) { // 2nd condition for vertical swipe met
				swipedir = (distY < 0) ? 'up' : 'down'; // if dist traveled is negative, it indicates up swipe
			}
		}
		handleswipe(swipedir);
	}, false);
}

function initDatabaseAndDbFunction() {

	if (window.indexedDB) {

		// IndexedDB
		indexedDB = window.indexedDB;
		dbVersion = 2;

		createObjectStore = function (upgradeDb) {
			if (!upgradeDb.objectStoreNames.contains('store')) {
				var object = upgradeDb.createObjectStore('store', {
					keyPath: "id",
					autoIncrement: true
				});
				object.createIndex("url", "url", {
					unique: true
				});
				object.createIndex("blob", "blob", {
					unique: true
				});
			}
		};

		// Create/open database
		openDbRequest = new Promise(function (resolve, reject) {
			openDbRequest = indexedDB.open('audioFiles', dbVersion);

			openDbRequest.onupgradeneeded = function (e) {
				var db = e.target.result;
				console.log('running onupgradeneeded');
				createObjectStore(db);
			};

			openDbRequest.onsuccess = function (e) {
				db = e.target.result;
				resolve(db);
			};

			openDbRequest.onerror = function (e) {
				reject(e.target.error);
			};
		});

		addToDb = function (url, blob) {
			if (!(blob instanceof Blob)) {
				console.error("parameter blob value is not Blob type", blob);
				return;
			}

			openDbRequest.then((db) => {
				var item = {
					//"question": question,
					"url": url,
					"blob": blob
					//"part": part
				};

				var transaction = db.transaction(["store"], "readwrite");
				var objectStore = transaction.objectStore('store');

				var req = objectStore.add(item);

				req.onsuccess = function () {
					console.log("Item successfully added.")
				};

				req.onerror = function (e) {
					transaction.abort();
					//console.error('Error addToDb: ', e.target.error, item);
				};
			}).catch((e) => {
				console.error('Error: ', e);
			});
		};

		// only return one object (not array) even index not unique and have many record.
		// should only use with unique index.

		getAllFromDb = function () {
			return new Promise(function (resolve, reject) {
				openDbRequest.then((db) => {
					var transaction = db.transaction(["store"], 'readonly');
					var objectStore = transaction.objectStore('store');

					var request = objectStore.getAll();

					request.onsuccess = function (event) {
						resolve(event.target.result);
					};

					request.onerror = function (e) {
						reject(e.target.error);
					};
				}).catch((e) => {
					console.error("error", e);
				});
			});
		};

		searchFromDb = function (lower, upper, indexName) {

			return new Promise(function (resolve, reject) {
				if (!lower && !upper) {
					return;
				}

				var data = [];
				var range;

				if (lower && upper) {
					// prevent from mistype.
					if (lower > upper) {
						upper = lower;
						lower = upper;
					}
					// All keys ≥ lower && ≤ upper
					range = IDBKeyRange.bound(lower, upper);
				} else if (!lower) {

					// All keys ≤ upper
					range = IDBKeyRange.upperBound(upper);
				} else {
					// All keys ≥ lowwer
					range = IDBKeyRange.lowerBound(lower);
				}

				openDbRequest.then((db) => {
					var transaction = db.transaction(["store"], 'readonly');
					var objectStore = transaction.objectStore('store');

					var request;

					// find by index name
					if (indexName) {
						request = objectStore.index(indexName).openCursor(range);

						// find by key path.
					} else {
						request = objectStore.openCursor(range);
					}

					request.onsuccess = function (event) {
						var cursor = event.target.result;

						// loop through result. similar to for or while 
						if (cursor) {
							data.push(cursor.value);

							// equal continue in for or while loop.
							cursor.continue();
						} else {

							// if not have any data left (iterator >= cursor.length)
							resolve(data);
						}
					}

					request.onerror = function (e) {
						reject(e.target.error);
					};
				}).catch((e) => {
					console.error('Error: ', e);
				});
			});
		};

		deleteFromDbWithRange = function (lower, upper, indexName) {
			if (!lower && !upper) {
				return;
			}

			var range;

			if (lower && upper) {
				// prevent from mistype.
				if (lower > upper) {
					upper = lower;
					lower = upper;
				}

				// All keys ≥ lower && ≤ upper
				range = IDBKeyRange.bound(lower, upper);
			} else if (!lower) {

				// All keys ≤ upper
				range = IDBKeyRange.upperBound(upper);
			} else {
				// All keys ≥ lowwer
				range = IDBKeyRange.lowerBound(lower);
			}

			openDbRequest.then((db) => {
				var transaction = db.transaction(["store"], 'readwrite');
				var objectStore = transaction.objectStore('store');
				var request;

				// find by index name
				if (indexName) {
					request = objectStore.index(indexName).openCursor(range);

					// find by key path
				} else {
					request = objectStore.openCursor(range);
				}

				request.onsuccess = function (event) {
					var cursor = event.target.result;

					// loop through result. similar to for or while 
					if (cursor) {
						var deleteRequest = cursor.delete();

						deleteRequest.onerror = function (e) {
							console.error("error delete(): ", e);
						};

						// equal continue in for or while loop.
						cursor.continue();
					} else {
						return transaction.complete; // if not have any data left (iterator >= cursor.length)
					}
				}

				request.onerror = function (e) {
					console.error(e.target.error);
				};
			}).catch((e) => {
				console.error('Error: ', e);
			});
		};

		deleteFromDb = function (value, indexName) {
			openDbRequest.then((db) => {
				var transaction = db.transaction(["store"], 'readwrite');
				var objectStore = transaction.objectStore('store');

				if (indexName) {
					var request = objectStore.index(indexName).openCursor(IDBKeyRange.only(value)); // The value of indexName == value

					request.onsuccess = function (event) {
						var cursor = event.target.result;

						if (cursor) {
							var deleteRequest = cursor.delete();
							cursor.continue();

							deleteRequest.onerror = function (e) {
								console.error("error delete(): ", e);
							};
						} else {
							return;
						}
					};

					request.onerror = function (e) {
						console.error(e.target.error);
					};
				} else {
					objectStore.delete(value);
				}

				return transaction.complete;
			}).catch((e) => {
				console.error('Error: ', e);
			});
		};

		getFromDb = function (value, indexName) {
			return new Promise(function (resolve, reject) {
				openDbRequest.then((db) => {
					var transaction = db.transaction(["store"], 'readonly');
					var objectStore = transaction.objectStore('store');

					var request;

					if (indexName) {
						request = objectStore.index(indexName).get(value);
					} else {
						request = objectStore.get(value);
					}

					request.onsuccess = function (event) {
						resolve(event.target.result);
					};

					request.onerror = function (e) {
						reject(e.target.error);
					};
				}).catch((e) => {
					console.error("error", e);
				});
			});
		};

		getFile2 = function (url, part, question, isDirection) {
			return new Promise((resolve, reject) => {
				// Create XHR
				var xhr = new XMLHttpRequest();
				var blob;

				xhr.open("GET", url, true);

				// Set the responseType to blob
				xhr.responseType = "blob";

				xhr.addEventListener("load", async function () {
					if (xhr.status === 200) {
						console.log("file retrieved from server", url);

						// Blob as response
						blob = xhr.response;

						// Put the received blob into IndexedDB
						let indexUrl;
						if (question) {
							indexUrl = url + userEid + idAssessment + part + question;
						} else {
							indexUrl = url + userEid + idAssessment + part;
						}

						if (isDirection) {
							let direction = await readFileAsText(blob);

							let dImgUrl = $(direction).find('img').eq(0).attr('src');
							let dAudioUrl = $(direction).find('source').eq(0).attr('src');

							if (dImgUrl) {
								getFile2(dImgUrl, part);
							}

							if (dAudioUrl) {
								getFile2(dAudioUrl, part);
							}
						}

						addToDb(indexUrl, blob);

						resolve(true);
					} else {
						let index = loadRetry.findIndex((obj) => {
							obj.url == url
						});

						if (index < 0) {
							loadRetry.push({
								url: url,
								count: 0
							});
						} else {
							loadRetry[index].count = loadRetry[index].count + 1;
						}

						setTimeout(() => {
							if (index < 0 || (index >= 0 && loadRetry[index].count < 10)) {
								console.log("retry load file from server count=" + loadRetry[index].count, url);
								getFile(url, part, question);
							}
						}, 10000);
					}
				}, false);

				xhr.addEventListener("error", function (e) {
					let index = loadRetry.findIndex((obj) => {
						obj.url == url
					});

					if (index < 0) {
						loadRetry.push({
							url: url,
							count: 0
						});
					} else {
						loadRetry[index].count = loadRetry[index].count + 1;
					}

					setTimeout(() => {
						if (index < 0 || (index >= 0 && loadRetry[index].count < 10)) {
							console.log("retry load file from server count=" + loadRetry[index].count, url);
							getFile(url, part, question);
						}
					}, 10000);
				});

				// Send XHR
				xhr.send();
			});
		};

		getFile = function (url, part, question, isDirection) {
			// Create XHR
			var xhr = new XMLHttpRequest();
			var blob;

			xhr.open("GET", url, true);

			// Set the responseType to blob
			xhr.responseType = "blob";

			xhr.addEventListener("load", async function () {
				if (xhr.status === 200) {
					console.log("file retrieved from server", url);

					// Blob as response
					blob = xhr.response;

					// Put the received blob into IndexedDB
					let indexUrl;
					if (question) {
						indexUrl = url + userEid + idAssessment + part + question;
					} else {
						indexUrl = url + userEid + idAssessment + part;
					}

					if (isDirection) {
						let direction = await readFileAsText(blob);

						let dImgUrl = $(direction).find('img').eq(0).attr('src');
						let dAudioUrl = $(direction).find('source').eq(0).attr('src');

						if (dImgUrl) {
							getFile(dImgUrl, part);
						}

						if (dAudioUrl) {
							getFile(dAudioUrl, part);
						}
					}

					addToDb(indexUrl, blob);
				} else {
					let index = loadRetry.findIndex((obj) => {
						obj.url == url
					});

					if (index < 0) {
						loadRetry.push({
							url: url,
							count: 0
						});
					} else {
						loadRetry[index].count = loadRetry[index].count + 1;
					}

					setTimeout(() => {
						if (index < 0 || (index >= 0 && loadRetry[index].count < 10)) {
							console.log("retry load file from server count=" + loadRetry[index].count, url);
							getFile(url, part, question);
						}
					}, 10000);
				}
			}, false);

			xhr.addEventListener("error", function (e) {
				let index = loadRetry.findIndex((obj) => {
					obj.url == url
				});

				if (index < 0) {
					loadRetry.push({
						url: url,
						count: 0
					});
				} else {
					loadRetry[index].count = loadRetry[index].count + 1;
				}

				setTimeout(() => {
					if (index < 0 || (index >= 0 && loadRetry[index].count < 10)) {
						console.log("retry load file from server count=" + loadRetry[index].count, url);
						getFile(url, part, question);
					}
				}, 10000);
			});

			// Send XHR
			xhr.send();
		};

	} else {
		console.log("This browser doesn't support IndexedDB");
		$('#switchBootstrap20').bootstrapSwitch('state', false);
		$('#switchBootstrap20').bootstrapSwitch('disabled', true);
	}
}

function getAndStoreAudioFile(index) {
	return new Promise(async function (resolve, reject) {
		for (var i = index; i <= 7; i++) {
			if (i == 5 || i == 6) {
				continue;
			} else {
				if (mainData["part" + i]) {
					if (i != 1) {
						waitingAlert("Loading question's data of part " + i);
					}
					for (var j = 0; j < mainData["part" + i].length; j++) {
						let question;
						let data = mainData["part" + i][j];
						let index;
						let childQuestion;
						let fatherQuestion;

						if (data.length > 0) {
							fatherQuestion = data.filter((q) => {
								return isValidPatternX_Y(q.objective);
							});

							// question = JSON.parse(fatherQuestion[0].question);
							question = fatherQuestion[0].question;
						} else {
							// question = JSON.parse(data.question);
							question = data.question;
						}

						if (question.audio) {
							if (data.length > 0) {
								childQuestion = data.filter((question) => {
									return !isValidPatternX_Y(question.objective);
								});

								index = getRealIndex(arrayRealQuestion, childQuestion[0]);
							} else {
								index = getRealIndex(arrayRealQuestion, data);
							}

							// check if url exists in db or not.
							let result = await getFromDb(question.audio + userEid + idAssessment + i + index, "url").catch((e) => {
								console.log("error :", e, question.audio);
							});

							if (!result) {
								if (i == assessmentData.savedAnswer.lastVisitedPart) {
									await getFile2(question.audio, i, index);
								} else {
									getFile(question.audio, i, index);
								}
							}
						}

						if (question.image) {
							if (data.length > 0) {
								if (!childQuestion) {
									childQuestion = data.filter((question) => {
										return !isValidPatternX_Y(question.objective);
									});
								}

								if (!index) {
									index = getRealIndex(arrayRealQuestion, childQuestion[0]);
								}
							} else {
								if (!index) {
									index = getRealIndex(arrayRealQuestion, data);
								}
							}

							// check if url exists in db or not.
							let result = await getFromDb(question.image + userEid + idAssessment + i + index, "url").catch((e) => {
								console.log("error :", e, question.audio);
							});

							if (!result) {
								getFile(question.image, i, index);
							}
						}
					}
				}

				// load question number and look-at-number audio.
				if (partsMetaData["part" + i]) {

					// load direction html page.
					if (partsMetaData["part" + i]["directions"]) {
						let directionUrl = partsMetaData["part" + i]["directions"];

						let result = await getFromDb(directionUrl + userEid + idAssessment + i, "url").catch((e) => {
							console.log("error :", e, question.audio);
						});

						if (!result) {
							getFile(directionUrl, i, "", true);
						}
					}

					// load number audio
					if (partsMetaData["part" + i]["number"]) {
						for (var prop in partsMetaData["part" + i]["number"]) {
							if (partsMetaData["part" + i]["number"].hasOwnProperty(prop)) {
								let numUrl = partsMetaData["part" + i]["number"][prop];

								// check if url exists in db or not.
								let result = await getFromDb(numUrl + userEid + idAssessment + i + prop, "url").catch((e) => {
									console.log("error :", e, question.audio);
								});

								if (!result) {
									getFile(numUrl, i, prop);
								}
							}
						}
					}

					// load look at number audio
					if (partsMetaData["part" + i]["look-at-number"]) {
						for (var prop in partsMetaData["part" + i]["look-at-number"]) {
							if (partsMetaData["part" + i]["look-at-number"].hasOwnProperty(prop)) {
								let lookUrl = partsMetaData["part" + i]["look-at-number"][prop];

								// check if url exists in db or not.
								let result = await getFromDb(lookUrl + userEid + idAssessment + i + prop, "url").catch((e) => {
									console.log("error :", e, question.audio);
								});

								if (!result) {
									getFile(lookUrl, i, prop);
								}
							}
						}
					}
				}

				// end if just load part 1 first;
				if (i == assessmentData.savedAnswer.lastVisitedPart) {
					break;
				}
			}
		}
		successAlert();
		resolve(true);
	});
}

function replaceWithLocalBlob(data, part, question) {
	return new Promise(async function (resolve) {

		// remove last used url.
		if (localImageUrl) {
			window.URL.revokeObjectURL(localImageUrl);
		}

		if (localAudioUrl) {
			window.URL.revokeObjectURL(localAudioUrl);
		}

		if (data.imageUrl) {
			let result = await getFromDb(data.imageUrl + userEid + idAssessment + part + question, "url").catch((e) => {
				console.log("error :", e, data.imageUrl);
			});
			if (result) {
				localImageUrl = window.URL.createObjectURL(result.blob);
				data.imageUrl = localImageUrl;
			}
		}

		if (data.audioUrl) {
			let result = await getFromDb(data.audioUrl + userEid + idAssessment + part + question, "url").catch((e) => {
				console.log("error :", e, data.audioUrl);
			});
			if (result) {

				// if file has been encrypted.
				if (cryptoPassword) {
					result.blob = await decrypt(result.blob);
				}

				localAudioUrl = window.URL.createObjectURL(result.blob);
				data.audioUrl = localAudioUrl;
			}
		}

		resolve(data);
	});
}

// encrypt audio file to string text;
function encrypt(blob) {
	return new Promise((resolve, reject) => {
		var reader = new FileReader();

		reader.onload = function () {
			// theFile is the blob... CryptoJS wants a string...
			arrayBuffer = reader.result;

			const view = new Int8Array(arrayBuffer);

			// convert arrayBuffer to string.
			let binaryString = [...view].map((n) => n.toString(2)).join(' ');

			let encrypted = CryptoJS.AES.encrypt(binaryString, cryptoPassword);

			resolve(encrypted);
		}

		reader.readAsArrayBuffer(blob);
	});
}

// decrypt from encrypted text to audio file.
async function decrypt(blob) {
	let encryptedStr = await readFileAsText(blob).catch(e => {
		console.error(e)
	});

	var de = CryptoJS.AES.decrypt(encryptedStr, cryptoPassword);
	var arrayBinary = de.toString(CryptoJS.enc.Utf8).split(' ');
	var arrayByte = [];

	for (var i = 0; i < arrayBinary.length; i++) {
		arrayByte.push(parseInt(arrayBinary[i], 2));
	}

	var arrayBuffer = new Uint8Array(arrayByte);
	var blob = new Blob([arrayBuffer]);

	return blob;
}

function getFileAndDecrypt(url) {
	return new Promise((resolve, reject) => {
		if (localAudioUrl) {
			window.URL.revokeObjectURL(localAudioUrl);
		}

		// Create XHR
		var xhr = new XMLHttpRequest();
		var blob;

		xhr.open("GET", url, true);

		// Set the responseType to blob
		xhr.responseType = "blob";

		xhr.addEventListener("load", function () {
			if (xhr.status === 200) {

				// Blob as response
				blob = xhr.response;

				let decryptedBlob = decrypt(blob);

				localAudioUrl = window.URL.createObjectURL(decryptedBlob);

				resolve(true);
			} else {
				errorAlert(error_network);
				resolve(false);
			}
		}, false);

		// Send XHR
		xhr.send();
	});
}

function readFileAsText(blobText) {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();

		reader.onload = function () {
			resolve(reader.result);
		};

		reader.onerror = function (e) {
			reject(e);
		}

		reader.readAsText(blobText);
	});
}


// get decrypt password from server.
function getPassword() {
	let url = "get-password?idA=" + idAssessment;

	$.ajax({
		type: "POST",
		url: url,
		dataType: 'json',
		contentType: "application/json",
		success: function (result) {
			console.log("decrypt password: ", result);
			if (result && result.password) {
				cryptoPassword = result.password;
			}
		},
		error: function (e) {
			console.log("ERROR: ", e);
			errorAlert(error);
		}
	});
}


function getTimeLimit() {
	return new Promise((resolve, reject) => {
		let url = "get-timeLimit?idA=" + idAssessment;

		$.ajax({
			type: "POST",
			url: url,
			dataType: 'json',
			contentType: "application/json",
			success: function (result) {
				console.log("time limit: ", result);
				resolve(result);
			},
			error: function (e) {
				console.log("ERROR: ", e);
			}
		});
	});
}

function checkTimeLimit() {
	return new Promise(async (resolve, reject) => {
		TIME_REMAINING_SECOND = await getTimeLimit();

		if (TIME_REMAINING_SECOND == 0) {
			TIME_REMAINING_SECOND = 7200;
		}

		if (savedAnswers && savedAnswers.timeElapsed) {
			TIME_REMAINING_SECOND = TIME_REMAINING_SECOND - savedAnswers.timeElapsed;
		}

		if (TIME_REMAINING_SECOND <= 0) {
			resolve(true);
		} else {
			resolve(false);
		}
	});
}

function settingWindowResize() {
	$(window).resize(function () {
		resizeWindow();
	});

	resizeWindow();
}

function resizeWindow() {
	let fIndexPart = lengthPartAll.part1 + 1;
	let lIndexPart = lengthPartAll.part1 + lengthPartAll.part2;

	if (window.innerWidth < 1200 && window.innerWidth >= 922) {
		$(".question-area").removeClass("col-sm-8");
		$(".question-area").removeClass("col-sm-12");
		$("#test_page > div.toeic-contain > div:nth-child(1) > div:eq(1)").removeClass("col-sm-4");
		$("#test_page > div.toeic-contain > div:nth-child(1) > div:eq(1)").removeClass("col-sm-12");

		if (!$(".question-area").hasClass("col-sm-7")) {
			$(".question-area").addClass("col-sm-7");
		}
		if (!$("#test_page > div.toeic-contain > div:nth-child(1) > div:eq(1)").hasClass("col-sm-5")) {
			$("#test_page > div.toeic-contain > div:nth-child(1) > div:eq(1)").addClass("col-sm-5");
		}
	} else if (window.innerWidth >= 1200) {
		$(".question-area").removeClass("col-sm-7");
		$(".question-area").removeClass("col-sm-12");
		$("#test_page > div.toeic-contain > div:nth-child(1) > div:eq(1)").removeClass("col-sm-5");
		$("#test_page > div.toeic-contain > div:nth-child(1) > div:eq(1)").removeClass("col-sm-12");

		if (!$(".question-area").hasClass("col-sm-8")) {
			$(".question-area").addClass("col-sm-8");
		}
		if (!$("#test_page > div.toeic-contain > div:nth-child(1) > div:eq(1)").hasClass("col-sm-4")) {
			$("#test_page > div.toeic-contain > div:nth-child(1) > div:eq(1)").addClass("col-sm-4");
		}
	}
	if (window.innerWidth < 992) {
		$(".question-area").removeClass("col-sm-7");
		$(".question-area").removeClass("col-sm-8");
		$("#test_page > div.toeic-contain > div:nth-child(1) > div:eq(1)").removeClass("col-sm-4");
		$("#test_page > div.toeic-contain > div:nth-child(1) > div:eq(1)").removeClass("col-sm-5");

		if (!$(".question-area").hasClass("col-sm-12")) {
			$(".question-area").addClass("col-sm-12");
		}
		if (!$("#test_page > div.toeic-contain > div:nth-child(1) > div:eq(1)").hasClass("col-sm-12")) {
			$("#test_page > div.toeic-contain > div:nth-child(1) > div:eq(1)").addClass("col-sm-12");
		}

		for (let i = fIndexPart; i <= lIndexPart; i++) {
			$(`#answer_form_${i}`).css("margin-left", "-3px");
		}
	} else {
		for (let i = fIndexPart; i <= lIndexPart; i++) {
			$(`#answer_form_${i}`).css("margin-left", "9px");
		}
	}
}

function disableTextSelectAndCopyPaste() {
	$('head').append(`<style>body{
		-webkit-touch-callout: none;
		-webkit-user-select: none;
		-khtml-user-select: none;
		-moz-user-select: none;
		-ms-user-select: none;
		user-select: none;
	}</style>`);

	$('html').bind('cut copy paste', function (e) {
		e.preventDefault();
	});
}

function getFaceRecognitionFlag() {
	$.ajax({
		type: "POST",
		url: "get-face-regconition?idA=" + idAssessment,
		dataType: 'json',
		contentType: "application/json",
		success: function (result) {
			console.log("getFaceRecognitionFlag(): ", result);
			faceRec = result;
		},
		error: function (er) {
			console.log("ERROR: ", er);
			errorAlert("unexpected error occurred, please refresh this page...");
		}
	});
}

function saveImages(blob) {
	const fd = new FormData();
	fd.append("idG", assessmentData.assessment.assessmentGradingId);
	if (blob) {
		const file = new File([blob], "filename.jpeg", { type: blob.type });
		images.push(file);
	}

	for (var i = 0; i < images.length; i++) {
		fd.append('image', images[i]);
	}

	$.ajax({
		url: 'picture/save',
		data: fd,
		processData: false,
		contentType: false,
		type: 'POST',
		success: function (data) {
			console.log("saveImages", data);
			images = [];
			countSaveImage = 0;
		},
		error: function (err) {
			handleError(err);
			if (countSaveImage < 10) {
				saveImages();
				countSaveImage++;
			}
		}
	});
}

function checkSubmit() {
	if (faceRec.endRecognition) {
		startCamera();
		$("#test_page").addClass("hidden");
	} else {
		submit(true);
	}
}

function recognize(blob) {
	const fd = new FormData();

	const file = new File([blob], "filename.jpeg");

	fd.append('image', file);

	$.ajax({
		url: 'hawk-api/recognize',
		data: fd,
		processData: false,
		contentType: false,
		type: 'POST',
		success: function (data) {
			console.log("saveImages", data);
		},
		error: function (err) {
			console.log(err)
		}
	});
}

function renderPBP(index) {
	$(question_contain).empty();
	if (mainData["part" + index]) {
		for (var i = 0; i < mainData["part" + index].length; i++) {
			var dataQuestion = mainData["part" + index][i];
			// Render part question
			if ((index == 1 || index == 2 || index == 5) && !template) {
				renderForSingleQuestionPBP(dataQuestion);
			} else if ((index == 3 || index == 4 || index == 6 || index == 7) && !template) {
				// console.log("Call renderForMultiQuestion...");
				renderForMultiQuestionPBP(dataQuestion);
			} else {
				console.log("Warning: index= " + index + "is not processed.");
			}
		}
	}
}

function switchPart(index) {
	indexPart = index;
	indexQuestion = 0;
	renderPBP(index);
}

async function renderForSingleQuestionPBP(data) {

	if (assessmentData.assessment.practice) {
		addFeedbackTextForDialog(data);
	}

	data = parseData(data);

	var indexQ = getRealIndex(arrayRealQuestion, data);

	// if user granted permission to load question'data to local, replace question's audio, image with local data.
	if (window.indexedDB && loadFilePermission) {
		data = await replaceWithLocalBlob(data, indexPart, indexQ);
	}

	// if file has been encrypted get file blob and decrypt the file and create url for the file and set to localAudioUrl.
	if (cryptoPassword) {
		if (data.audioUrl) {
			let ready = await getFileAndDecrypt(data.audioUrl);
			if (ready) {
				data.audioUrl = localAudioUrl;
			} else {
				data.audioUrl = "";
			}
		}
	}

	// mark color
	// markColorAnswerSheet(indexQ);
	// Render question
	if (!$(question_contain).find(".title-part").length) {
		question_contain.innerHTML = `
		<div class="title-part" >
			<h3 id="title_part">${setTitlePart(data.poolName)}</h3>
			${(assessmentData.assessment.practice && indexPart < 5) ? `<button class="btn btn-info" style="margin: 5px" onclick="showAudioTextDialog()">Show Audio Text</button>` : ''}
			<ul class="nav nav-tabs">
				${indexPart == 5 && mainData["part" + indexPart] ? `<li class="active" onclick="switchPart(5)"><a href="javascript:void(0)">Part 5</a></li>` : `<li onclick="switchPart(5)"><a href="javascript:void(0)">Part 5</a></li>`}
				${indexPart == 6 && mainData["part" + indexPart] ? `<li class="active" onclick="switchPart(6)"><a href="javascript:void(0)">Part 6</a></li>` : `<li onclick="switchPart(6)"><a href="javascript:void(0)">Part 6</a></li>`}
				${indexPart == 7 && mainData["part" + indexPart] ? `<li class="active" onclick="switchPart(7)"><a href="javascript:void(0)">Part 7</a></li>` : `<li onclick="switchPart(7)"><a href="javascript:void(0)">Part 7</a></li>`}
			</ul>
		</div>
		<div class="test-box">
		</div>`;
	}

	$(".test-box").append(`
			<div class="toeic-index" style="margin-top: 15px;">
				<h4 id="question_number">Question ${getRealIndex(arrayRealQuestion, data)}</h4>
			</div>
			<div class="toiec-question" style="margin-top: 0;">
				<div id="pic_question">
					${data.imageUrl ? `<img style="cursor: pointer;" src="${data.imageUrl}" onclick="showImage(this)" alt="${error_network}">` : ''}
				</div>
				<p class="text-question" id="text_question">
					${data.justText}
				</p>
			</div>
			${data.audioUrl ? `
				<div class="for-listening text-center" id="audio_question">
					<div class="black">
					</div>
					${data.audioUrl ? `<audio class="mt-md-3s" preload="auto" src="${data.audioUrl}" id="audioQuestion" controls controlsList="nodownload">` : ''}
				</div>`
			: ''}
			<div class="toeic-select">
				<form class="toiec-form">
					<label class="radio-inline answerTextOption" onclick="selectAnswer(${data.answerAId})">
						<strong>A. </strong>
						<span id="answerA">${data.answerA}</span>
					</label>
					<label class="radio-inline answerTextOption" onclick="selectAnswer(${data.answerBId})">
						<strong>B. </strong>
						<span id="answerB">${data.answerB}</span>
					</label>
					<label class="radio-inline answerTextOption" onclick="selectAnswer(${data.answerCId})">
						<strong>C. </strong>
						<span id="answerC">${data.answerC}</span>
					</label>
					${(data.answerDId && indexPart != 2) ? `
						<label class="radio-inline answerTextOption" onclick="selectAnswer(${data.answerDId})">
							<strong>D. </strong><span> ${data.answerD}</span>
						</label>
					` : ''}
				</form>
			</div>
	`);
}

// For part 3, 4, 6 ,7
async function renderForMultiQuestionPBP(data) {
	// console.log("renderForMultiQuestion");

	var childQuestion = [];
	var fatherQuestion;

	// info question
	fatherQuestion = data.filter((question) => {
		return isValidPatternX_Y(question.objective);
	});

	// child question
	childQuestion = data.filter((question) => {
		return !isValidPatternX_Y(question.objective);
	});

	// sorting child question
	childQuestion.sort(function (a, b) {

		return parseInt(a.objective.trim().slice(-1)) - parseInt(b.objective.trim().slice(-1));
	});

	var indexQ = getRealIndex(arrayRealQuestion, childQuestion[0]);
	var lastIndexQ = getRealIndex(arrayRealQuestion, childQuestion[childQuestion.length - 1]);

	if (assessmentData.assessment.practice) {
		addFeedbackTextForDialog(data);
	}

	// mark color
	// markColorAnswerSheet(indexQ, lastIndexQ);
	// Render question father
	let parseFatherData = parseData(fatherQuestion[0]);
	let infoQuestion = showQuestionMulti(parseFatherData, indexQ, lastIndexQ);

	// if user granted permission to load question'data to local, replace question's audio, image with local data.
	if (window.indexedDB && loadFilePermission) {
		infoQuestion = await replaceWithLocalBlob(infoQuestion, indexPart, indexQ);
	}

	// if file has been encrypted get file blob and decrypt the file and create url for the file and set to localAudioUrl.
	if (cryptoPassword) {
		if (infoQuestion.audioUrl) {
			let ready = await getFileAndDecrypt(infoQuestion.audioUrl);
			if (ready) {
				infoQuestion.audioUrl = localAudioUrl;
			} else {
				infoQuestion.audioUrl = "";
			}
		}
	}

	if (!$(question_contain).find(".title-part").length) {
		question_contain.innerHTML = `
		<div class="title-part" >
			<h3 id="title_part_multi">${setTitlePart(infoQuestion.poolName)}</h3>
			${(assessmentData.assessment.practice && indexPart < 5) ? `<button class="btn btn-info" style="margin: 5px" onclick="showAudioTextDialog()">Show Audio Text</button>` : ''}
			<ul class="nav nav-tabs">
				${indexPart == 5 && mainData["part" + indexPart] ? `<li class="active" onclick="switchPart(5)"><a href="javascript:void(0)">Part 5</a></li>` : `<li onclick="switchPart(5)"><a href="javascript:void(0)">Part 5</a></li>`}
				${indexPart == 6 && mainData["part" + indexPart] ? `<li class="active" onclick="switchPart(6)"><a href="javascript:void(0)">Part 6</a></li>` : `<li onclick="switchPart(6)"><a href="javascript:void(0)">Part 6</a></li>`}
				${indexPart == 7 && mainData["part" + indexPart] ? `<li class="active" onclick="switchPart(7)"><a href="javascript:void(0)">Part 7</a></li>` : `<li onclick="switchPart(7)"><a href="javascript:void(0)">Part 7</a></li>`}
			</ul>
		</div>
		<div class="test-box">
		</div>`;
	}

	$(".test-box").append(`
			<div class="toeic-index" style="margin-top: 15px;">
				<h4 id="question_number_multi">Question ${getRealIndex(arrayRealQuestion, childQuestion[0])} - ${getRealIndex(arrayRealQuestion, childQuestion[childQuestion.length - 1])} </h4>
			</div>
			<div class="toiec-question" style="margin-top: 0;">
				<div id="pic_question_multi">
					${infoQuestion.imageUrl ? `<img style="cursor: pointer;" src="${infoQuestion.imageUrl}" onclick="showImage(this)" alt="${error_network}">` : ''}
				</div>
				<p class="text-question" id="text_question_multi">
					${infoQuestion.justText}
				</p>
			</div>
			${infoQuestion.audioUrl ? `
				<div class="for-listening text-center" id="audio_question_multi">
					<div class="black">
					</div>
					${infoQuestion.audioUrl ? `<audio preload="auto" src="${infoQuestion.audioUrl}" id="audioQuestion" controls controlsList="nodownload">` : ''}
				</div>
			` : ''}

			<div class="multi-question" id="multi_question">
				${	/*Render quesion child*/
		childQuestion.map((item, index) => {
			let question = parseData(item);
			return `<div class="toiec-question">
			
												
			
											<p class="text-question" id="text_question">
												${getRealIndex(arrayRealQuestion, item)}. ${question.justText}
											</p>
										</div>
										<form class="toiec-form id-${parseFatherData.id}" id="${index}">
											<label class="radio-inline answerTextOption" onclick="selectAnswer(${question.answerAId})">
												<strong>A. </strong>
												<span>${question.answerA}</span>
											</label>
											<label class="radio-inline answerTextOption" onclick="selectAnswer(${question.answerBId})">
												<strong>B. </strong>
												<span>${question.answerB}</span>
											</label>
											<label class="radio-inline answerTextOption" onclick="selectAnswer(${question.answerCId})">
												<strong>C. </strong>
												<span>${question.answerC}</span>
											</label>
											${ question.answerDId ? `
												<label class="radio-inline answerTextOption" onclick="selectAnswer(${question.answerDId})">
													<strong>D. </strong>
													<span>${question.answerD}</span>
												</label>` : ''}
										</form>`;
		}).join("")
		}
			</div>
	`);
	if ($('button.numberInQuestion' + parseFatherData.id).length > 0) {
		addEventForNumberInQuestion(parseFatherData.id);
	}
}