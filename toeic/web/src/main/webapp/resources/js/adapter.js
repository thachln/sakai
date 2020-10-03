// Parse data to new json data for toeic test
function parseData(data) {
	// var string = "image=media/part1/1.jpgaudio=media/part1/2.mp3";
	// var textData = parseTextData(string);
	// console.log("textData", textData);

	var dataParse = {};

	// parse data url from question
	let textData = parseTextData(data.question);
	dataParse = {
		id: data.id,
		poolName: data.poolName,
		imageUrl: textData.image,
		audioUrl: textData.audio,
		justText: textData.justText,
		answerA: data.answers.answerA,
		answerB: data.answers.answerB,
		answerC: data.answers.answerC,
		answerD: data.answers.answerD,
		//correctAnswer: data[i].correctAnswer,
		objective: data.objective,
		feedbackCorrect: data.feedbackCorrect,
		feedbackInCorrect: data.feedbackInCorrect
	};
	return dataParse;
}

function parseTextData(text) {
	var textData = {
		justText: "",
		image: "",
		audio: ""
	};
	var textLength = text.length;

	var checkAudio = text.includes("audio=");
	var indexAudio = text.search("audio=") + 6;
	var lastIndexAudio = text.search(".mp3") + 4;
	// console.log("checkAudio " + checkAudio + " indexAudio " + indexAudio + " lastIndexAudio " + lastIndexAudio);

	var checkImage = text.includes("image=");
	var indexImage = text.search("image=") + 6;
	var lastIndexImage = text.search(".jpg") + 4;
	// console.log("checkImage " + checkImage +  " indexImage " + indexImage + " lastIndexImage " + lastIndexImage);

	if (checkAudio && checkImage) {
		// slice audio url
		textData.audio = text.slice(indexAudio, lastIndexAudio);
		// slice img url
		textData.image = text.slice(indexImage, lastIndexImage);
	} else if(checkAudio && !checkImage){
		// slice audio url
		textData.audio = text.slice(indexAudio, lastIndexAudio);

		// slice text
		if (text.search("audio=") > 0) {
			textData.justText = text.slice(0, text.search("audio="));
		} else {
			textData.justText = text.slice(lastIndexAudio, textLength);
		}
	} else if(!checkAudio && checkImage) {
		// slice img url
		textData.image = text.slice(indexImage, lastIndexImage);

		// slice text
		if (text.search("image=") > 0) {
			textData.justText = text.slice(0, text.search("image="));
		} else {
			textData.justText = text.slice(lastIndexImage, textLength);
		}
	} else if (!checkAudio && !checkImage) {
		textData.justText = text;
	} else {
		textData = null;
	}

	return textData;
}