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
var newTestBtn = _("newTest");

var question_contain = _("question_contain");
var multi_question_area = _("multi_question");

var toeicForm = document.getElementsByClassName("toeic-form");

// Init
var localData = JSON.parse(localStorage.getItem('infoMyTest'));
var indexPart = 1;
var indexQuestion = 0;
var arrayRealQuestion = getRealQuestion(dataToeic.data);
var isStart = false;
var TIME_REMAINING_MINUTES = 20;
var counterTimeToeic;
var arrSelectedAnswers = [];
var correctListening = 0;
var correctReading = 0;
var arrScoreListening = [5, 5, 5, 5, 5, 5, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 110, 115, 120, 125, 130, 135, 140, 145, 150, 160, 165, 170, 175, 180, 185, 190, 195, 200, 210, 215, 220, 230, 240, 245, 250, 255, 260, 270, 275, 280, 290, 295, 300, 310, 315, 320, 325, 330, 340, 345, 350, 360, 365, 370, 380, 385, 390, 395, 400, 405, 410, 420, 425, 430, 440, 445, 450, 460, 465, 470, 475, 480, 485, 490, 495, 495, 495, 495, 495, 495, 495, 495, 495, 495, 495];
var arrScoreReading = [5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 60, 65, 70, 80, 85, 90, 95, 100, 110, 115, 120, 125, 130, 140, 145, 150, 160, 165, 170, 175, 180, 190, 195, 200, 210, 215, 220, 225, 230, 235, 240, 250, 255, 260, 265, 270, 280, 285, 290, 300, 305, 310, 320, 325, 330, 335, 340, 350, 355, 360, 365, 370, 380, 385, 390, 395, 400, 405, 410, 415, 420, 425, 430, 435, 445, 450, 455, 465, 470, 480, 485, 490, 495, 495, 495, 495];
var scoreTOEIC = 0;
//scoreTOEIC = parseInt(arrScoreListening[correctListening]) + parseInt(arrScoreReading[correctReading]);

// Event btn
start_test.addEventListener("click", function(){
	isStart = true;
	startTest();
});
// Event for next button
nextBtn.addEventListener("click", function() {
	nextQuestion();
});
// Event for back button
backBtn.addEventListener("click", function() {
	prevQuestion();
});

// Event for submit button
// submitBtn.addEventListener("click", function() {
// 	//finishToeic();
// });

// START
renderInfoToeic(dataToeic);

// =====================Function=================== //

function _(x) {
	return document.getElementById(x);
}

function initData() {
	if (localData) {
		TIME_REMAINING_MINUTES = localData.timeLeft;
		indexPart = localData.indexPart;
		indexQuestion = localData.indexQuestion;
		arrSelectedAnswers = localData.arrSelectedAnswers;
	}
}

function startTest() {
	if (isStart) {
		start_page.classList.add("hidden");
		test_page.classList.remove("hidden");
		// Init data
		initData();
		// Counter time toeic
		timer(TIME_REMAINING_MINUTES * 60, timeToeicId, finishToeic);	
		// Render answer sheet
		renderAnswerSheet(arrayRealQuestion);			
		// Render toeic area
		renderToeic(dataToeic);

	} else {
		start_page.classList.remove("hidden");
		test_page.classList.add("hidden");
	}
}

function renderToeic(dataAll) {
	// End Test
	if (indexPart > 7 ) {
		finishToeic();
		return;
	}

	// Data part question array
	var dataQuestion = dataAll.data["part" + indexPart][indexQuestion];

	// Render part question
	if (indexPart == 1 || indexPart == 2 || indexPart == 5) {
		renderForSingleQuestion(dataQuestion);
	} else if (indexPart == 3 || indexPart == 4 || indexPart == 6 || indexPart == 7) {
		renderForMultiQuestion(dataQuestion);
	}

	if (indexPart > 4 && indexQuestion > 0) {
		backBtn.classList.remove("hidden");
	}

	if (indexPart < 4 || (indexPart  == 5 && indexQuestion == 0)) {
		backBtn.classList.add("hidden");
	}

	if ((indexQuestion == dataToeic.data["part" + indexPart].length -1) && indexPart == 7) {
		nextBtn.classList.add("hidden");
		backBtn.classList.remove("hidden");
		submitBtn.classList.remove("hidden");
	}
}

// Read data part
function readDataPart(dataAll, partNum) {
	const data = dataAll.data["part" + partNum];
	return data;
}

// Render infomation toeic test
function renderInfoToeic(data) {
	if (localData) {
		start_test.innerHTML = 'CONTINUE';
		newTestBtn.classList.remove("hidden");
	} else {
		start_test.innerHTML = 'START';
		newTestBtn.classList.add("hidden");
	}

	info_toeic.innerHTML = `
		<p><b>User:</b> ${data.infoStudent}</p>
		<p><b>Day Begin:</b> ${data.dayBegin}</p>
		<p><b>Day End:</b> ${data.dayEnd}</p>
		<p><b>Assigment:</b> ${data.assigment}</p>
	`;
}

// For part 1, 2, 5
function renderForSingleQuestion(data) {
	//console.log("renderForSingleQuestion");
	// reset
	question_contain.innerHTML = '';
	data = parseData(data);

	// mark color
	markColorAnswerSheet(getRealIndex(arrayRealQuestion, data));
	// Render question
	question_contain.innerHTML = `
		<div class="title-part" >
			<h3 id="title_part">${setTitlePart(data.poolName)}</h3>
		</div>
		<div class="test-box">
			<div class="toeic-index">
				<h4 id="question_number">Question ${getRealIndex(arrayRealQuestion, data)}</h4>
				<div class="time-remain-question" id="time_question">
					<!-- remaining <span >00:00</span> -->
				</div>
			</div>
			<div class="toiec-question">
				<div class="pic-question" id="pic_question">
					${data.imageUrl ? `<img src="${data.imageUrl}" alt="">` : ''}
				</div>
				<p class="text-question" id="text_question">
					${data.justText}
				</p>
			</div>
			<div class="for-listening text-center" id="audio_question">
				<div class="black">
				</div>
				${data.audioUrl ? `<audio src="${data.audioUrl}" autoplay="true" controls>` : ''}
			</div>
			<div class="toeic-select">
				<form class="toiec-form">
					<label class="radio-inline">
						<strong>A.</strong>
						<span id="answerA">${data.answerA}</span>
					</label>
					<label class="radio-inline">
						<strong>B.</strong>
						<span id="answerB">${data.answerB}</span>
					</label>
					<label class="radio-inline">
						<strong>C.</strong>
						<span id="answerC">${data.answerC}</span>
					</label>
					<label class="radio-inline">
						${indexPart != 2 ? `<strong>D.</strong><span>${data.answerD}</span>` : ''}
					</label>
				</form>
			</div>
		</div>
	`;
}

// For part 3, 4, 6 ,7
function renderForMultiQuestion(data) {
	//console.log("renderForMultiQuestion");
	question_contain.innerHTML = '';

	var childQuestion = [];
	var fatherQuestion;
	var regex = /^[0-9]{1}.[0-9]{1,2}$/g;
	// info question
	fatherQuestion = data.filter((question) => {
		return question.objective.match(regex);
	});	
	// child question
	childQuestion = data.filter((question) => {
		return !question.objective.match(regex);
	});

	// mark color
	markColorAnswerSheet(getRealIndex(arrayRealQuestion, childQuestion[0]), getRealIndex(arrayRealQuestion, childQuestion[childQuestion.length - 1]));
	// Render question father
	let infoQuestion = parseData(fatherQuestion[0]);
	question_contain.innerHTML = `
		<div class="title-part" >
			<h3 id="title_part_multi">${setTitlePart(infoQuestion.poolName)}</h3>
		</div>
		<div class="test-box">
			<div class="toeic-index">
				<h4 id="question_number_multi">Question ${getRealIndex(arrayRealQuestion, childQuestion[0])} - ${getRealIndex(arrayRealQuestion, childQuestion[childQuestion.length - 1])} </h4>
				<div class="time-remain-question">
					<!-- remaining <span class="time-question">00:00</span> -->
				</div>
			</div>
			<div class="toiec-question">
				<div class="pic-question" id="pic_question_multi">
					${infoQuestion.imageUrl ? `<img src="${infoQuestion.imageUrl}" alt="">` : ''}
				</div>
				<p class="text-question" id="text_question_multi">
					${infoQuestion.justText}
				</p>
			</div>
			<div class="for-listening text-center" id="audio_question_multi">
				<div class="black">
				</div>
				${infoQuestion.audioUrl ? `<audio src="${infoQuestion.audioUrl}" autoplay="true" controls>` : ''}
			</div>
			

			<div class="multi-question" id="multi_question">
				${	/*Render quesion child*/
					childQuestion.map((item, index) => {
						let question = parseData(item);
						return 			`<div class="toiec-question">
											<p class="num-question">
												${getRealIndex(arrayRealQuestion, item)}.
											</p>
											<div class="pic-question" id="pic_question">
												${question.imageUrl ? `<img src="${question.imageUrl}" alt="">` : ''}
											</div>
											<p class="text-question" id="text_question">
												${question.justText}
											</p>
										</div>
										<form class="toiec-form">
											<label class="radio-inline">
												<strong>A.</strong>
												<span>${question.answerA}</span>
											</label>
											<label class="radio-inline">
												<strong>B.</strong>
												<span>${question.answerB}</span>
											</label>
											<label class="radio-inline">
												<strong>C.</strong>
												<span>${question.answerC}</span>
											</label>
											<label class="radio-inline">
												<strong>D.</strong>
												<span>${question.answerD}</span>
											</label>
										</form>`;
					})
				}
			</div>
		</div>
	`;

}

function renderAnswerSheet(arrayRealQuestion) {

	//var localAnswers = JSON.parse(localStorage.getItem('infoMyTest'));
	console.log("data localData", localData);
	// if (localAnswers) {

	// }

	//answer_sheet = "";
	for (var i = 0; i < arrayRealQuestion.length; i++) {
		let numTitle;
		if (i < 9) {
			numTitle = "0" + (i + 1).toString();
		} else {
			numTitle = (i + 1).toString();
		}
		answer_sheet.innerHTML += `
		<div class="toeic-form" id="answer_form_${i + 1}">
			<label class="ques-no">${numTitle}.</label>
			<label class="radio-inline">
				<input type="radio" attrId="${arrayRealQuestion[i].id}" poolName="${arrayRealQuestion[i].poolName}" name="optradio[${arrayRealQuestion[i].id}]" value="A">
				<strong>A</strong>
			</label>
			<label class="radio-inline">
				<input type="radio" attrId="${arrayRealQuestion[i].id}" poolName="${arrayRealQuestion[i].poolName}" name="optradio[${arrayRealQuestion[i].id}]" value="B">
				<strong>B</strong>
			</label>
			<label class="radio-inline">
				<input type="radio" attrId="${arrayRealQuestion[i].id}" poolName="${arrayRealQuestion[i].poolName}" name="optradio[${arrayRealQuestion[i].id}]" value="C">
				<strong>C</strong>
			</label>
			<label class="radio-inline">
				${arrayRealQuestion[i].poolName != "part2" ? `<input type="radio" attrId="${arrayRealQuestion[i].id}" poolName="${arrayRealQuestion[i].poolName}" name="optradio[${arrayRealQuestion[i].id}]" value="D"><strong>D</strong>` : ''}
			</label>
		</div>
	`;
		// Set checked
		let temId = arrayRealQuestion[i].id;
	    $('input[attrId= '+ temId + ']').each(function(index) {
	    	// console.log("$(this).val", $(this).val());
	    	if (localData) {
	    		localData.arrSelectedAnswers.forEach(answer => {
	    			if(answer.id == temId) {
	    				if (answer.selectedAnswer == $(this).val()) {
	    					$(this).attr('checked', true);
	    					//console.log("$(this).val " + $(this).val() + " temId " + temId);
	    				}
	    				//$(this).val() == answer.selectedAnswer
						//$(this).prop('checked', true);
	    			}
	    		});
	    	}
	    });
	}

	// Event for radio checked
    $("input[type='radio']").on('click', function(e) {
       getCheckedRadio($(this).attr("attrId"), $(this).val(), $(this).attr("poolName"));
    });
}

function getRealQuestion(data) {
	var arrayRealQuestion = [];

	// Logic
	loopQuestionSingle(data.part1, arrayRealQuestion);
	loopQuestionSingle(data.part2, arrayRealQuestion);
	loopQuestionMulti(data.part3, arrayRealQuestion);
	loopQuestionMulti(data.part4, arrayRealQuestion);
	loopQuestionSingle(data.part5, arrayRealQuestion);
	loopQuestionMulti(data.part6, arrayRealQuestion);
	loopQuestionMulti(data.part7, arrayRealQuestion);

	function loopQuestionSingle(part, array) {
		part.forEach(item => {
			array.push(item);
		});
	}	

	function loopQuestionMulti(part, array) {
		const regex = /^[0-9]{1}.[0-9]{1,2}$/g;
		part.forEach(item => {
			item.forEach(question => {
				if (!question.objective.match(regex)) {
					array.push(question);
				}
			});
		});
	}

	return arrayRealQuestion;
}

function getRealIndex(data, value) {
	let index = 0;
	index = data.findIndex(item => item.id === value.id) + 1;
	return index;
}

function setTitlePart(poolName) {
	var titlePart;
	switch (poolName) {
		case "part1":
			titlePart = "Part 1: Picture description";
			break;
		case "part2":
			titlePart = "Part 2: Question and Response";
			break;
		case "part3":
			titlePart = "Part 3: Short conversation";
			break;
		case "part4":
			titlePart = "Part 4: Short talk";
			break;
		case "part5":
			titlePart = "Part 5: Incomplete sentences";
			break;
		case "part6":
			titlePart = "Part 6: Text completion";
			break;
		case "part7":
			titlePart = "Part 7: Passages";
			break;
		default:
			titlePart = "Part ??: ??";
	}

	return titlePart;
}

function nextQuestion() {
	let dataPart = dataToeic.data["part" + indexPart];
	let lengthPart = dataPart.length;	
	indexQuestion++;

	if (indexQuestion > lengthPart - 1) {
		indexPart++;
		indexQuestion = 0;
	}

	if ((indexQuestion == lengthPart -1) && indexPart == 7) {
		nextBtn.classList.add("hidden");
		submitBtn.classList.remove("hidden");
	}
	renderToeic(dataToeic);

	// console.log('indexQuestion', indexQuestion);
	// console.log('indexPart', indexPart);
}

function prevQuestion() {
	let dataPart = dataToeic.data["part" + indexPart];
	let lengthPart = dataPart.length;
	nextBtn.classList.remove("hidden");
	submitBtn.classList.add("hidden");
	indexQuestion--;

	if (indexQuestion < 0) {
		(indexPart > 1) ? indexPart-- : indexPart;
		indexQuestion = lengthPart - 1;
	}

	renderToeic(dataToeic);
}

function markColorAnswerSheet(firstParam, secondParam) {
	//console.log('firstParam ' + firstParam + ' secondParam ' + secondParam);
	var selectedQuestion;
	// reset
	for (var i = 0; i < toeicForm.length; i++) {
		toeicForm[i].classList.remove("mark-color");
	}	

	if (firstParam && secondParam) {
		for (var i = firstParam; i <= secondParam; i++) {
			//console.log('answer_form_' + i);
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
	//console.log("answer_sheet.scrollTop", selectedQuestion.scrollHeight);
	// selectedQuestion.classList.add("mark-color");
}

function timer(duration, blockId, func) {
	var timer = duration,
		minutes, seconds;
		counterTimeToeic = setInterval(function() {
		minutes = parseInt(timer / 60, 10);
		seconds = parseInt(timer % 60, 10);

		minutes = minutes < 10 ? "0" + minutes : minutes;
		seconds = seconds < 10 ? "0" + seconds : seconds;

		blockId.innerHTML = minutes + ":" + seconds;

		if (--timer < 0) {
			timer = duration;
			clearInterval(counterTimeToeic);
			func();
		}

	    // Put the object into storage
	    var infoMyTest = {
	    	infoData: dataToeic,
			indexPart: indexPart,
			timeLeft: minutes,
			indexQuestion: indexQuestion,
	    	arrSelectedAnswers: arrSelectedAnswers
	    };
		localStorage.setItem('infoMyTest', JSON.stringify(infoMyTest));
	}, 1000);
}

function finishToeic() {
	checkAnswer();
	renderResult(dataToeic);
	resetDataToeic();
}

function resetPage() {
	resetDataToeic();
	result_page.innerHTML = '';

	nextBtn.classList.remove("hidden");
	start_page.classList.remove("hidden");
	test_page.classList.add("hidden");
	result_page.classList.add("hidden");

	renderInfoToeic(dataToeic);
	location.reload();
}

function resetDataToeic() {
	indexPart = 1;
	indexQuestion = 0;
	correctListening = 0;
	correctReading = 0;
	scoreTOEIC = 0;
	arrSelectedAnswers = [];

	question_contain.innerHTML = '';
	answer_sheet.innerHTML = '';
	//result_page.innerHTML = '';

	clearInterval(counterTimeToeic);
	localStorage.removeItem("infoMyTest");
}

//External function to handle all radio selections
function getCheckedRadio(id, value, pool) {

    if (arrSelectedAnswers.findIndex(item => item.id == id) > -1) {
    	arrSelectedAnswers.splice(arrSelectedAnswers.findIndex(item => item.id == id), 1);
    } 
    // Push id value to array answer
    arrSelectedAnswers.push({
    	id: id,
    	pool: pool,
    	selectedAnswer: value
    });	

    //console.log("arrSelectedAnswers", arrSelectedAnswers);
}

function checkAnswer() {
	if (arrSelectedAnswers) {
		arrSelectedAnswers.forEach(answer => {
			let question = arrayRealQuestion.find(ques => ques.id === answer.id);
			if (question) {
				if (question.correctAnswer == answer.selectedAnswer) {
					if (answer.pool == 'part1' || answer.pool == 'part2' || answer.pool == 'part3' || answer.pool == 'part4') {
						correctListening++;
					} else if (answer.pool == 'part5' || answer.pool == 'part6' || answer.pool == 'part7') {
						correctReading++;
					}
				}
			}
		});		
	}
	//console.log("correctListening", correctListening);
	//console.log("correctReading", correctReading);
	scoreTOEIC = parseInt(arrScoreListening[correctListening]) + parseInt(arrScoreReading[correctReading]);
	//console.log("correct ", correctListening + correctReading);
}

function renderResult(data) {
	test_page.classList.add("hidden");
	result_page.classList.remove("hidden");

	result_page.innerHTML = `
		<p><b>User:</b> ${data.infoStudent}</p>
		<p><b>Day Begin:</b> ${data.dayBegin}</p>
		<p><b>Day End:</b> ${data.dayEnd}</p>
		<p><b>Assigment:</b> ${data.assigment}</p>
		<p><b>Correct Listening:</b> ${correctListening} - <b>Correct Reading:</b> ${correctReading}</p>
		<h2>SCORE ${scoreTOEIC}</h2>
		<h3>Comment: You are so so lazy ^^</h3>
		<button type="button" class="btn btn-danger" id="resetBtn" onclick="resetPage()">New Test</button>
	`;

}