// Parse data to new json data for toeic test
function parseData(data) {
	var dataParse = {};

	// parse data url from question
	//let textData = parseAndCheckJson(data.question);
	dataParse = {
		id: data.id,
		poolName: data.poolName,
		imageUrl: data.question.image,
		audioUrl: data.question.audio,
		justText: data.question.justText ? data.question.justText.replace("[Blank]","") : '',
		answerA: data.answers.answerA.replace("[Blank]",""),
		answerB: data.answers.answerB.replace("[Blank]",""),
		answerC: data.answers.answerC.replace("[Blank]",""),
		answerD: data.answers.answerD.replace("[Blank]",""),
		answerAId: data.answers.answerAId,
		answerBId: data.answers.answerBId,
		answerCId: data.answers.answerCId,
		answerDId: data.answers.answerDId,
		answerAFeedback: data.answers.answerAFeedback,
		answerBFeedback: data.answers.answerBFeedback,
		answerCFeedback: data.answers.answerCFeedback,
		answerDFeedback: data.answers.answerDFeedback,
		// correctAnswer: data.correctAnswer,
		objective: data.objective,
		feedbackCorrect: data.feedbackCorrect,
		feedbackInCorrect: data.feedbackInCorrect
	};
	return dataParse;
}

function parseAndCheckJson(jsonText) {
	var textData = {
		justText: "",
		image: "",
		audio: "",
	};
// if (/^[\],:{}\s]*$/.test(jsonText.replace(/\\["\\\/bfnrtu]/g, '@').
// replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,
// ']').
// replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {
// //the json is ok
// let tempData = JSON.parse(jsonText);
// textData.image = tempData.image;
// textData.audio = tempData.audio;
// textData.justText = tempData.text ? tempData.text : '';
// } else {
// //the json is not ok
// textData.justText = jsonText ? jsonText : '';
// }

	try {
		let tempData =  JSON.parse(jsonText);
		textData.image = tempData.image;
		textData.audio = tempData.audio;
		textData.justText = tempData.text ? tempData.text : '';
	} catch (objError) {
		// if (objError instanceof SyntaxError) {
		// console.log("Error:" + objError);
		// } else {
		// console.log("Error:" + objError);
		// }
	    
		  // the json is not ok
		  textData.justText = jsonText ? jsonText : '';
	}
	
	return textData;
}

function parseMainData(assessmentData) {
	var dataParse = {};

	assessmentData.partsContents.forEach((item, index) => {
		let partName = item.title.slice(-1);
		if (partName == 1 || partName == 2 || partName == 5) {
			dataParse["part" + partName] = parseToArrayPart(item);
		} else {
			dataParse["part" + partName] = parseToArrayPartMulti(item);
		}

		// Parse info part to object
		infoEachPart['part' + (index + 1)] = item.description;
	});

	return dataParse;
}

function parseToArrayPart(obj) {
	let newArray = [];
	let a, b, c, d;
	obj.itemContents.forEach((item, index) => {
		a = item.answers.find(answer => answer.label == "A");
		b = item.answers.find(answer => answer.label == "B");
		c = item.answers.find(answer => answer.label == "C");
		d = item.answers.find(answer => answer.label == "D");

		newArray.push({
			"id": item.questionId,
			"poolName": (obj.poolName) ? obj.poolName : "part" + obj.title.slice(-1),
			"question": item.content,
			"answers": {
				"answerA": (a) ? a.text : '',
				"answerB": (b) ? b.text : '',
				"answerC": (c) ? c.text : '',
				"answerD": (d) ? d.text : '',
				"answerAId": (a) ? a.answerId : '',
				"answerBId": (b) ? b.answerId : '',
				"answerCId": (c) ? c.answerId : '',
				"answerDId": (d) ? d.answerId : '',
				"answerAFeedback": (a) ? a.feedback : '',
				"answerBFeedback": (b) ? b.feedback : '',
				"answerCFeedback": (c) ? c.feedback : '',
				"answerDFeedback": (d) ? d.feedback : ''
			},
			// "correctAnswer": item.key,
			"objective": item.objective,
			"feedbackCorrect": "Corret",
			"feedbackInCorrect": "Incorrect"
		});
	});

	return newArray;
}

function isValidPatternX_Y(text) {
	const regex = /^[0-9]{1,2}\.[0-9]{1,2}$/g;
	
	return (text !== null) && (text.trim().match(regex));
}


function parseToArrayPartMulti(obj) {
	let newArray = [];
	let mainArray = [];
	let a, b, c, d;

	obj.itemContents.forEach(item => {
		if (isValidPatternX_Y(item.objective.trim())) {
			mainArray.push(item.objective);
		}
	});
	
	if (obj.title.trim().slice(-1) < 5) {
		mainArray.sort(function(a, b) {
			a = a.split(".");
			b = b.split(".");
			
			return parseInt(a[1]) - parseInt(b[1]);
		});
	}

	for (var mindex = 1; mindex <= mainArray.length; mindex++) {
		let childArray = [];
		let data = mainArray[mindex-1];
		for (var index = 1; index <= obj.itemContents.length; index++) {
			let item = obj.itemContents[index-1];			
			let objective = item.objective;
			// let my1 = mindex.toString().length == 1 ? '0' + mindex.toString()
			// : mindex.toString();
			// let my2 = index.toString().length == 1 ? '0' + index.toString() :
			// index.toString();

			data = data.trim();

			// Check objective is start by data
			let isSub = objective.startsWith(data + ".");
			let isSame = objective.trim() == data;
			let isSubOrSame = isSub || isSame;
			// console.log("isSub=" + isSub + ";isSame=" + isSame +
			// ";isSubOrSame=" + isSubOrSame);
			if (isSubOrSame) {
				for (var p = 0; p < item.answers.length; p++) {
					let ans = item.answers[p];
					if (ans.feedback) {
						ans.feedback = ans.feedback.replace(/\n/g, "<br>");
						ans.feedback = ans.feedback.replace(/&quot;/g, "");
					}
				}

				a = item.answers.find(answer => answer.label == "A");
				b = item.answers.find(answer => answer.label == "B");
				c = item.answers.find(answer => answer.label == "C");
				d = item.answers.find(answer => answer.label == "D");

				childArray.push({
					"id": item.questionId,
					"poolName": (obj.poolName) ? obj.poolName : obj.title.replace(/\s/g, ''),
					"question": item.content,
					"answers": {
						"answerA": (a) ? a.text : '',
						"answerB": (b) ? b.text : '',
						"answerC": (c) ? c.text : '',
						"answerD": (d) ? d.text : '',
						"answerAId": (a) ? a.answerId : '',
						"answerBId": (b) ? b.answerId : '',
						"answerCId": (c) ? c.answerId : '',
						"answerDId": (d) ? d.answerId : '',
						"answerAFeedback": (a) ? a.feedback : '',
						"answerBFeedback": (b) ? b.feedback : '',
						"answerCFeedback": (c) ? c.feedback : '',
						"answerDFeedback": (d) ? d.feedback : ''
					},
					// "correctAnswer": item.key.length > 1 ? "" : item.key,
					"objective": item.objective,
					"feedbackCorrect": "Corret",
					"feedbackInCorrect": "Incorrect"
				});				
			} else {
				// console.log("skip data: objective=" + objective + "; data=" +
				// data);
			}
		}
		// childArray.sort(function(a, b) {
		// 	if (a.objective.trim().length > b.objective.trim().length) {
		// 		return 1;
		// 	} else if (a.objective.trim().length == b.objective.trim().length) {
		// 		return parseInt(a.objective.trim().slice(-1)) - parseInt(b.objective.trim().slice(-1));
		// 	}
		// });
		
		newArray.push(childArray);
	}

	return newArray;
}

function getMetaData() {
	let count = 0;
	let data = null;
	let metaUrl
	
	for (var i = 1; i < 5; i++) {
		let part = "part" + i;
		
		if (mainData.hasOwnProperty(part)) {
			if (i < 3) {
				// let data = JSON.parse(mainData[part][0].question);
				let data = mainData[part][0].question;
				metaUrl = (data.audio) ? data.audio : data.image;
				break;
			} else if (i == 3 || i == 4) {
				for (var j = 0; j < 4; j++) {
					try {
						// let data = JSON.parse(mainData[part][j].question);
						data = mainData[part][j].question;
						metaUrl = (data.audio) ? data.audio : data.image;
						break;
					} catch (error) {
						//console.log(error);
						continue;
					}
				}
			}
		}
	}
	
	if (metaUrl) {
		introUrl = metaUrl;
		for (var prop in mainData) {
			if (mainData.hasOwnProperty(prop)) {
	
				metaUrl = metaUrl.split("/");
				metaUrl.pop();
				metaUrl.pop();
				metaUrl.push(prop + "/metadata.json");
				metaUrl = metaUrl.join("/");
	
				getData(metaUrl, prop);
			}
		}
		
		introUrl = introUrl.split("/");
		introUrl.pop();
		introUrl.pop();
		introUrl.push("begin.html");
		introUrl = introUrl.join("/");
	}
}

function getData(url, prop) {
	$.get(url, function (data) {
		partsMetaData[prop] = data;
	}).fail(function(e) {
		console.error("failed to get metadata.json: ", url, e);
	});
}