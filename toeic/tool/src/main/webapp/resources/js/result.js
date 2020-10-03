var assessmentData;
var mainData;
var arrayRealQuestion;
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
var infoEachPart = {};
var indexPart;
var indexQuestion;
var toeicForm;

$(function () {
	waitingAlert(wait);
	var url = new URL(window.location.href);
	idAssessment = url.searchParams.get("idA");
	idGrading = url.searchParams.get("idG");

	settingWindowResize();
	addScrollTojQuery();

	$.ajax({
		type: "POST",
		url: "get-result?idA=" + idAssessment + "&idG=" + idGrading,
		dataType: 'json',
		contentType: "application/json",
		success: function (data) {
			assessmentData = data;
			console.log("result", data);

			mainData = parseMainData(assessmentData.assessment);
			arrayRealQuestion = getRealQuestion(mainData);

			getLengthEachPart();
			renderAnswerSheet(arrayRealQuestion);
			renderResult(mainData);

			console.log("mainData", mainData);
			successAlert();
		},
		error: function (er) {
			console.log("ERROR: ", er);
			errorAlert("unexpected error occurred while preparing your assessment result, please refresh this page...");
		}
	});
});

function renderResult(data) {
	for (var prop in data) {
		if (data.hasOwnProperty(prop)) {
			indexPart = parseInt(prop.slice(-1));
			firstPartNum = parseInt(prop.slice(-1));
			indexQuestion = 0;

			for (var i = 0; i < data["part" + indexPart].length; i++) {
				// Data part question array
				indexQuestion = i;
				var dataQuestion = data["part" + indexPart][indexQuestion];

				if (indexPart == 1 || indexPart == 2 || indexPart == 5) {
					renderForSingleQuestion(dataQuestion);
				} else if (indexPart == 3 || indexPart == 4 || indexPart == 6 || indexPart == 7) {
					// console.log("Call renderForMultiQuestion...");
					renderForMultiQuestion(dataQuestion);
				} else {
					console.log("Warning: indexPart=" + indexPart + "is not processed.");
				}
			}
		}
	}
}

// For part 1, 2, 5
async function renderForSingleQuestion(data) {
	let script = getAudioScript(data);
	data = parseData(data);

	var indexQ = getRealIndex(arrayRealQuestion, data);

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

	// Render question
	$(".test-box").append(`
		<div class="question-${indexQ}">
			<div class="toeic-index">
				<h4 id="question_number">Question ${indexQ}</h4>
			</div>
			<div class="toiec-question">
				<div id="pic_question">
					${data.imageUrl ? `<img style="cursor: pointer;" src="${data.imageUrl}" alt="${error_network}">` : ''}
				</div>
				<p class="text-question" id="text_question">
					${data.justText}
				</p>
			</div>
			<div class="for-listening text-center" id="audio_question">
				<div class="black">
				</div>
				${data.audioUrl ? `<audio class="mt-md-3s" preload="auto" src="${data.audioUrl}" id="audioQuestion" controls controlsList="nodownload">` : ''}
			</div>
			${(script && indexPart < 5) ? `<div class="bg_script"><strong>Script: </strong>${script}</div>` : '' }
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
	`);
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

	// Render question father
	let infoQuestion = showQuestionMulti(parseData(fatherQuestion[0]), indexQ, lastIndexQ);

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

	let script = getAudioScript(data);
	let classQ = `question-${indexQ}`;
	for (p = indexQ + 1; p <= lastIndexQ; p++) {
		classQ += ` question-${p}`;
	}

	$(".test-box").append(`
	<div class="${classQ}">
			<div class="toeic-index">
				<h4 id="question_number_multi">Question ${indexQ} - ${lastIndexQ} </h4>
			</div>
			<div class="toiec-question">
				<div id="pic_question_multi">
					${infoQuestion.imageUrl ? `<img style="cursor: pointer;" src="${infoQuestion.imageUrl}" alt="${error_network}">` : ''}
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
	
			${(script && indexPart < 5) ? `<div class="bg_script"><strong>Script: </strong>${script}</div>` : '' }

				${	/*Render quesion child*/
		childQuestion.map((item, index) => {
			let question = parseData(item);
			return `<div class="toiec-question">
			
												
			
											<p class="text-question" id="text_question">
												${getRealIndex(arrayRealQuestion, item)}. ${question.justText}
											</p>
										</div>
										<form class="toiec-form toeic-form-${indexQ}" id="${index}">
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
    `);

	if ($('button.numberInQuestion-' + indexQ).length > 0) {
		addEventForNumberInQuestion(indexQ);
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
		<div class="question-box" id="answer_form_${i + 1}" ${(i < 99) ? `style="margin-left: 8px;"` : ''}>
			<table width="100%" border="0" cellpadding="2" cellspacing="0">
				<tbody>
					<tr align="left">
						${(i < 99) ? `<td width="25px" align="left">${numTitle}.</td>` : `<td width="33px" align="left">${numTitle}.</td>`}
						
						<td align="left" valign="bottom">
							<table cellspacing="0" border="0" style="border-collapse:collapse;">
								<tbody>
									<tr>
										<td>
											<li style="list-style:none; width:50px; text-align:left; float:left;">
												<input type="radio" disabled tabindex="-1" attrId="${arrayRealQuestion[i].id}" poolName="${arrayRealQuestion[i].poolName}" answerId="${arrayRealQuestion[i].answers.answerAId}" num="${i + 1}" name="optradio[${arrayRealQuestion[i].id}]" value="A">
												<strong>A</strong>
											</li>
										</td>
										<td>
											<li style="list-style:none; width:50px; text-align:left; float:left;">
												<input type="radio" disabled tabindex="-1" attrId="${arrayRealQuestion[i].id}" poolName="${arrayRealQuestion[i].poolName}" answerId="${arrayRealQuestion[i].answers.answerBId}" num="${i + 1}" name="optradio[${arrayRealQuestion[i].id}]" value="B">
												<strong>B</strong>
											</li>
										</td>
										<td>
											<li style="list-style:none; width:50px; text-align:left; float:left;">
												<input type="radio" disabled tabindex="-1" attrId="${arrayRealQuestion[i].id}" poolName="${arrayRealQuestion[i].poolName}" answerId="${arrayRealQuestion[i].answers.answerCId}" num="${i + 1}" name="optradio[${arrayRealQuestion[i].id}]" value="C">
												<strong>C</strong>
											</li>
										</td>
										${arrayRealQuestion[i].poolName.slice(-1) != "2" ? `
											<td>
												<li style="list-style:none; width:50px; text-align:left; float:left;">
													<input type="radio" disabled tabindex="-1" attrId="${arrayRealQuestion[i].id}" poolName="${arrayRealQuestion[i].poolName}" answerId="${arrayRealQuestion[i].answers.answerDId}" num="${i + 1}" name="optradio[${arrayRealQuestion[i].id}]" value="D">
													<strong>D</strong>
												</li>
											</td>
										`
										 : ''}

									</tr>
								</tbody>
							</table>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	`;
	}

	for (var j = 0; j < arrayRealQuestion.length; j++) {
		// Set checked
		let temId = arrayRealQuestion[j].id;
		let item = assessmentData.items.find(it => it.itemId == temId);

		$('input[attrId= ' + temId + ']').each(function (index) {
			if (item && item.isCorrect) {
				if (item.userAnswer == $(this).prop("value")) {
					$(this).prop("checked", true);
					$(this).parent().css("margin-right", "0").append(`<img src="resources/images/checktrue.jpg" width="15px" alt="">`);
				}
			} else if (item && !item.isCorrect) {
				if (item.userAnswer == $(this).prop("value")) {
					$(this).prop("checked", true);
					$(this).parent().css("margin-right", "0").append(`<img src="resources/images/checkfalse.jpg" width="15px" alt="">`);
				}
				if (item.answer == $(this).prop("value")) {
					$(this).parent().css("margin-right", "0").append(`<img src="resources/images/checktrue.jpg" width="15px" alt="">`);
				}
			}

		});
	}

	markColorAnswerSheet(1);
	addScrollEvent();
}

function getRealIndex(data, value) {
	let index = 0;

	if (data && value) {
		index = data.findIndex(item => item.id === value.id) + 1;
	}

	return index;
}

function showQuestionMulti(infoQuestion, indexQ, lastIndexQ) {
	if (infoQuestion.justText) {
		const reg = /[\(][0-9]{2,3}[\)]/g;
		const regQuestionNumberXToY = /[0-9]{2,3}[-]{1}[0-9]{2,3}/g;

		let match = infoQuestion.justText.match(regQuestionNumberXToY);
		if (match) {
			infoQuestion.justText = infoQuestion.justText.replace(match[0], indexQ + "-" + lastIndexQ);
		}
		let index = indexQ;

		let matches = infoQuestion.justText.match(reg);
		if (matches) {
			for (var i = 0; i < matches.length; i++) {
				let replaceStr = `<button class="numberInQuestion numberInQuestion-${index}" data-html="true" title="#" id="${i}"><span style="color: black;">${indexQ++}</span></button>`;
				infoQuestion.justText = infoQuestion.justText.replace(matches[i], replaceStr);
			}
		} else {
			return infoQuestion;
		}
	}

	return infoQuestion;
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
		$("#col1 > div > div > div.card-content.collapse.show > div.toeic-contain > div > div:eq(1)").removeClass("col-sm-4");
		$("#col1 > div > div > div.card-content.collapse.show > div.toeic-contain > div > div:eq(1)").removeClass("col-sm-12");

		if (!$(".question-area").hasClass("col-sm-8")) {
			$(".question-area").addClass("col-sm-8");
		}
		if (!$("#col1 > div > div > div.card-content.collapse.show > div.toeic-contain > div > div:eq(1)").hasClass("col-sm-4")) {
			$("#col1 > div > div > div.card-content.collapse.show > div.toeic-contain > div > div:eq(1)").addClass("col-sm-4");
		}
	} else if (window.innerWidth >= 1200) {
		$(".question-area").removeClass("col-sm-8");
		$(".question-area").removeClass("col-sm-12");
		$("#col1 > div > div > div.card-content.collapse.show > div.toeic-contain > div > div:eq(1)").removeClass("col-sm-4");
		$("#col1 > div > div > div.card-content.collapse.show > div.toeic-contain > div > div:eq(1)").removeClass("col-sm-12");

		if (!$(".question-area").hasClass("col-sm-9")) {
			$(".question-area").addClass("col-sm-9");
		}
		if (!$("#col1 > div > div > div.card-content.collapse.show > div.toeic-contain > div > div:eq(1)").hasClass("col-sm-3")) {
			$("#col1 > div > div > div.card-content.collapse.show > div.toeic-contain > div > div:eq(1)").addClass("col-sm-3");
		}
	}
	if (window.innerWidth < 992) {
		$(".question-area").removeClass("col-sm-9");
		$(".question-area").removeClass("col-sm-8");
		$("#col1 > div > div > div.card-content.collapse.show > div.toeic-contain > div > div:eq(1)").removeClass("col-sm-4");
		$("#col1 > div > div > div.card-content.collapse.show > div.toeic-contain > div > div:eq(1)").removeClass("col-sm-3");

		if (!$(".question-area").hasClass("col-sm-12")) {
			$(".question-area").addClass("col-sm-12");
		}
		if (!$("#col1 > div > div > div.card-content.collapse.show > div.toeic-contain > div > div:eq(1)").hasClass("col-sm-12")) {
			$("#col1 > div > div > div.card-content.collapse.show > div.toeic-contain > div > div:eq(1)").addClass("col-sm-12");
		}
	}
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

function addEventForNumberInQuestion(indexQ) {
	let buttonNumberInQuestion = $('button.numberInQuestion-' + indexQ);

	buttonNumberInQuestion.click(function (e) {
		e.preventDefault();
	});

	for (var i = 0; i < buttonNumberInQuestion.length; i++) {
		let id = $(buttonNumberInQuestion[i]).attr('id');
		let titleContent = "";

		let form = $(".toeic-form-" + indexQ).filter(function (index) {
			return $(this).attr('id') == id;
		});

		form.contents().each(function (index, obj) {
			if (this.nodeType == 1) {
				titleContent = titleContent + `<p> ${$(this).text()} <p>`;
			}
		});

		$(buttonNumberInQuestion[i]).prop('title', titleContent);
	}

	$("button.numberInQuestion-" + indexQ).tooltip({
		show: {
			effect: "slideDown",
			delay: 250
		},
		content: function () {
			return $(this).prop('title');
		}
	});
}

function getAudioScript(data) {
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

	return feedback;
}

function addScrollTojQuery() {
	jQuery.fn.scrollTo = function (elem) {
		//$(this).scrollTop($(this).scrollTop() - $(this).offset().top + $(elem).offset().top - 60);
		$(this).stop();
		$(this).animate({
			scrollTop: $(this).scrollTop() - $(this).offset().top + $(elem).offset().top
		}, 500);

		return this;
	};
}

function markColorAnswerSheet(firstParam, secondParam) {

	// reset
	$('.mark-color').removeClass('mark-color');

	if (firstParam && secondParam) {
		for (var i = firstParam; i <= secondParam; i++) {
			$('#answer_form_' + i).addClass('mark-color');
		}
	} else if (firstParam && !secondParam) {
		$('#answer_form_' + firstParam).addClass('mark-color');
	} else {
		return;
	}
}

function addScrollEvent() {
	$(".question-box").on("click", function (e) {
		e.preventDefault();
		let list = $(this).prop("id").split("_");
		let idQ = list[list.length - 1];
		$(".test-box").scrollTo(".question-" + idQ);
		markColorAnswerSheet(idQ);
	});
}