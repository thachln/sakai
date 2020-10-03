var dataResult;
var dataItems;

var resultTable = null;
var itemTable = null;
var loadedPubAssessmentId;
var selectedAssessmentGID = null;
var selectedAssessmentDbclick = null;
var staff;
var isStaff;
$(function () {

	initPage();

	$('.col-sm-12.col-md-6:eq(0)').append(
		`
			<button class="btn btn-info" style="margin-left: 30px; margin-top: 0px;" id="exportSR" onclick="exportStudentAssessmentResult()" disabled>Export</button>
		`
	);

	highlightSelectedRow();
	doubleClickRow();
	settingShowResultTable();

	$('head').append(`<style>.button_color:focus, input[type="submit"]:focus, input[type="button"]:focus, input[type="reset"]:focus, button:focus, button.btn-primary:focus {
		outline: none !important;
	}</style>`);

	$("#createStaff").click(function (e) {
		create();
	});

	$('#dlgCam').on('hidden.bs.modal', function () {
		stopCamera();
	});

	getStaffList();
});

function initPage() {

	// Khởi tạo Table kết quả
	if (resultTable == null) {
		resultTable = $("#resultTable")
			.DataTable(
				{
					// responsive: true,
					// scrollX : true,
					"order": [0, 'asc'],
					columns: [
						// {
						// data: "agentId"
						// },
						{
							data: "attemptDate"
						},
						{
							data: "submittedDate"
						},
						{
							data: "finalScore"
						},
						{
							data: "assessmentGradingId"
						}
					],

					columnDefs: [
						{
							targets: [0, 1],
							render: function (data, type, row) {
								if (data) {
									return formatDate(data);
								} else {
									return "";
								}
							}
						},
						{
							targets: [2],
							render: function (data, type, row) {
								if (data) {
									return data;
								} else {
									return "";
								}
							}
						},
						{
							targets: [3],
							render: function (data, type, row) {
								return `<a href="result?idA=${loadedPubAssessmentId}&idG=${data}" target="_blank">Show</a>`;
							}
						}
					]
				});

	}

	if (itemTable == null) {
		itemTable = $("#itemTable").DataTable(
			{
				// responsive: true,
				// scrollX : true,
				"order": [1, 'asc'],
				columns: [
					{
						data: "part"
					},
					{
						data: "question"
					},
					// {
					// data: "answerText"
					// },
					{
						data: "answer"
					},
					{
						data: "userAnswer"
					},
					{
						data: "isCorrect"
					}
				],
				columnDefs: [
					{
						targets: [4],
						render: function (data, type, row) {

							return renderCorrect(data);
						}
					}
					// {
					// 	"targets": [ 2 ],
					// 	"visible": false,
					// 	"searchable": false
					// }
					// ,{
					// targets: [1],
					// render: function (data, type, row) {
					//	
					// return renderAnswerText(data);
					// }
					// }
				]
			});
	}
}

function loadResult(pubAssessmentId) {
	showLoading('.modal-content:eq(0)');
	waitingAlert("Loading your assessment result data, please wait...");

	resultTable.clear().draw();
	$.ajax({
		type: "POST",
		url: "get-all-student-assessmentGrading?idA=" + pubAssessmentId,
		dataType: 'json',
		contentType: "application/json",
		success: function (result) {
			console.log("load assessmentGrading success!");
			dataResult = result;

			resultTable.rows.add(dataResult); // Add new data
			resultTable.columns.adjust().draw(); // Redraw the DataTable

			// reset selected Assessment Grading Id
			selectedAssessmentGID = null;

			hideLoading('.modal-content:eq(0)');
			successAlert();
		},
		error: function (e) {
			console.log("ERROR: ", e);
			errorAlert("Unexpected error occurred, please try again.");
			hideLoading('.modal-content:eq(0)');
		}
	});
}

function loadItems(rowData) {
	if (rowData) {
		waitingAlert("Loading student's assessment data, please wait...");
		showLoading('.modal-content:eq(1)');
		itemTable.clear().draw();

		// if selected assessment have loaded before then just show with local
		// data.
		if (localStorage.getItem('assessmentResult' + rowData.assessmentGradingId)) {
			dataItems = JSON.parse(localStorage.getItem('assessmentResult' + rowData.assessmentGradingId));

			itemTable.rows.add(dataItems); // Add new data
			itemTable.columns.adjust().draw(); // Redraw the DataTable

			hideLoading('.modal-content:eq(1)');
			successAlert();
		} else {
			$.ajax({
				type: "POST",
				url: "get-list-publishedItem?idA=" + rowData.assessmentGradingId + "&publishedId=" + rowData.publishedAssessmentId,
				dataType: 'json',
				contentType: "application/json",
				success: function (result) {
					console.log("load item success!");

					// save assessment data to local.
					localStorage.setItem('assessmentResult' + rowData.assessmentGradingId, JSON.stringify(result));

					dataItems = result;

					itemTable.rows.add(dataItems); // Add new data
					itemTable.columns.adjust().draw(); // Redraw the DataTable

					hideLoading('.modal-content:eq(1)');
					successAlert();
				},
				error: function (e) {
					console.log("ERROR: ", e);
					errorAlert("Unexpected error occurred, please try again.");
					hideLoading('.modal-content:eq(1)');
				}
			});
		}
	}
}

function settingShowResultTable() {
	$('a[href="#showResult"]').on('click', function (e) {
		e.preventDefault();

		if (!loadedPubAssessmentId) {
			loadedPubAssessmentId = $(this).attr("id");
			loadResult(loadedPubAssessmentId);

		} else if (loadedPubAssessmentId && loadedPubAssessmentId != $(this).attr("id")) {
			loadedPubAssessmentId = $(this).attr("id");
			loadResult(loadedPubAssessmentId);
		}

		$('#dlgAssessmentGrading').modal('show');
	});
}

// add color for isCorrect column
function renderCorrect(data) {
	if (data) {
		return `<p style="color: green">TRUE</p>`
	} else {
		return `<p style="color: red">FALSE</p>`
	}
}

function exportStudentAssessmentResult() {
	if (selectedAssessmentGID) {
		let url = 'export?assessmentGradingId=' + selectedAssessmentGID;
		downloadFile(url);
	} else {
		alert("No selected row.")
	}
}

function formatDate(value) {
	if (value) {
		value = new Date(value);
		dd = value.getDate();
		MM = value.getMonth() + 1;
		date = [((dd < 10) ? "0" + dd : dd), ((MM < 10) ? "0" + MM : MM), value.getFullYear()].reverse().join("/");
		hh = value.getHours();
		mm = value.getMinutes()
		time = ((hh < 10) ? "0" + hh : hh) + ":" + ((mm < 10) ? "0" + mm : mm);

		return date + ' ' + time;
	}

	return null;
}

function doubleClickRow() {
	$('#resultTable tbody').on('dblclick', 'tr', function () {
		let rowData = resultTable.row(this).data();

		// if didn't select any row before
		if (!selectedAssessmentDbclick) {
			selectedAssessmentDbclick = rowData.assessmentGradingId;
			loadItems(rowData);

			// if select a new different assessment row.
		} else if (selectedAssessmentDbclick && selectedAssessmentDbclick != rowData.assessmentGradingId) {
			selectedAssessmentDbclick = rowData.assessmentGradingId;
			loadItems(rowData);
		}

		// if select same assessment row then just show the table.
		$('#dlgItemGrading').modal('show');
	});
}

function highlightSelectedRow() {
	$('#resultTable tbody').on('click', 'tr', function () {

		// deselect row if selected
		if ($(this).hasClass('selected')) {
			$(this).removeClass('selected');

			// reset selected task
			selectedAssessmentGID = null;

			// deactive button export single assessment
			if ($('#exportSR').prop('disabled') == false) {
				$('#exportSR').prop("disabled", true);
			}
		} else {

			// deselect selected row
			resultTable.$('tr.selected').removeClass('selected');

			// highlight selected row
			$(this).addClass('selected');

			let rowData = resultTable.row(this).data();
			selectedAssessmentGID = rowData.assessmentGradingId;

			// active button export single assessment
			if ($('#exportSR').prop('disabled')) {
				$('#exportSR').prop("disabled", false);
			}
		}
	});
}

function downloadFile(url) {
	waitingAlert("Your download is starting, please wait...");
	$.ajax({
		url: url,
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
				errorAlert("unexpected error occurred while downloading, please try again.");
			}
		},
		error: function (e) {
			console.log("ERROR: ", e);
			errorAlert("unexpected error occurred while downloading, please try again.");
		}
	});
}

function showLoading(className) {
	$(className).block({
		message: '',
		overlayCSS: {
			backgroundColor: '#FFF',
			cursor: 'wait',
		},
		css: {
			border: 0,
			padding: 0,
			backgroundColor: 'none',
		}
	});
}

function hideLoading(className) {
	$(className).unblock();
}

function waitingAlert(message) {
	if ($('.offline-ui').length) {
		$('.offline-ui').remove();
	}
	$('body').append(`<div class="offline-ui offline-ui-down offline-ui-waiting">${message}</div>`)
		.slideDown("slow");
}

function successAlert() {
	$('.offline-ui').html("Successful!").removeClass().addClass('offline-ui offline-ui-up offline-ui-up-5s');
}

function errorAlert(message) {
	$('.offline-ui').html(message).removeClass().addClass('offline-ui offline-ui-down offline-ui-down-5s');
}

// camera functions
function showCameraModal() {
	$('#dlgCam').modal('show');
	startCamera();
}

var videoElement = document.querySelector('video#screenshot');
var videoSelect = document.querySelector('select#videoSource');
var images = [];
var count = 0;

videoSelect.onchange = getStream;

function startCamera() {
	navigator.mediaDevices.enumerateDevices()
		.then(gotDevices).then(getStream).catch(handleError);
}

function gotDevices(deviceInfos) {
	for (var i = 0; i !== deviceInfos.length; ++i) {
		var deviceInfo = deviceInfos[i];
		var option = document.createElement('option');
		option.value = deviceInfo.deviceId;
		if (deviceInfo.kind === 'videoinput') {
			console.log("video", deviceInfo);
			option.text = deviceInfo.label || 'camera ' +
				(videoSelect.length + 1);
			videoSelect.appendChild(option);
		} else {
			console.log('Found one other kind of source/device: ', deviceInfo);
		}
	}
}

function getStream() {
	stopCamera();

	var constraints = {
		video: {
			deviceId: {
				exact: videoSelect.value
			}
		}
	};

	navigator.mediaDevices.getUserMedia(constraints).
		then(gotStream).catch(handleError);
}

function gotStream(stream) {
	window.stream = stream; // make stream available to console
	videoElement.srcObject = stream;
}

function handleError(error) {
	console.log('Error: ', error);
}

const screenshotButton = document.querySelector('#screenshot-button');
const img = document.querySelector('img#screenshot');
const video = document.querySelector('video#screenshot');
const canvas = document.createElement('canvas');

screenshotButton.onclick = video.onclick = function () {
	canvas.width = video.videoWidth;
	canvas.height = video.videoHeight;
	canvas.getContext('2d').drawImage(video, 0, 0);
	// Other browsers will fall back to image/png
	let x = document.createElement("IMG");
	// let newImg = getImagePortion(canvas, 120, 150, 150, 80, 2);

	x.setAttribute("width", "150px");
	// x.setAttribute("src", newImg);
	x.setAttribute("src", canvas.toDataURL('image/webp'));
	x.setAttribute("alt", "Person");
	x.setAttribute("class", "thumb");

	$("#images").append(x);
	canvas.toBlob(function (blob) {
		images.push(blob);
	}, 'image/png', 1);

	count++;

	if (count >= 3) {
		$("#createStaff").prop("disabled", false);
	}
}

function stopCamera() {
	if (window.stream) {
		window.stream.getTracks().forEach(function (track) {
			track.stop();
		});
	}
}

function create() {
	const fd = new FormData();

	for (var i = 0; i < images.length; i++) {
		fd.append('images', images[i]);
	}
	let url = "hawk-api/create";
	if (isStaff) {
		fd.append("id", isStaff.id);
		url = "hawk-api/add";
	}

	$.ajax({
		url: url,
		data: fd,
		processData: false,
		contentType: false,
		type: 'POST',
		success: function (data) {
			console.log("saveImages", data);
			images = [];
			count = 0;
			$("#images").empty();
			$("#createStaff").prop("disabled", true);
			stopCamera();
		},
		error: function (err) {
			handleError(err);
			errorAlert("Error, please try again or refresh this page.");
		}
	});
}

function getImagePortion(imgObj, newWidth, newHeight, startX, startY, ratio) {
	/* the parameters: - the image element - the new width - the new height - the x point we start taking pixels - the y point we start taking pixels - the ratio */
	//set up canvas for thumbnail
	var tnCanvas = document.createElement('canvas');
	var tnCanvasContext = tnCanvas.getContext('2d');
	tnCanvas.width = newWidth; tnCanvas.height = newHeight;

	/* use the sourceCanvas to duplicate the entire image. This step was crucial for iOS4 and under devices. Follow the link at the end of this post to see what happens when you don’t do this */
	var bufferCanvas = document.createElement('canvas');
	var bufferContext = bufferCanvas.getContext('2d');
	bufferCanvas.width = imgObj.width;
	bufferCanvas.height = imgObj.height;
	bufferContext.drawImage(imgObj, 0, 0);

	/* now we use the drawImage method to take the pixels from our bufferCanvas and draw them into our thumbnail canvas */
	tnCanvasContext.drawImage(bufferCanvas, startX, startY, newWidth * ratio, newHeight * ratio, 0, 0, newWidth, newHeight);
	return tnCanvas.toDataURL('image/webp');
}

function getStaffList() {
	$.ajax({
		type: "POST",
		url: "hawk-api/staff",
		dataType: 'json',
		contentType: "application/json",
		success: function (data) {
			console.log("getStaffList: ", data);
			staff = data;
			if (staff.code == 200 && staff["data"]) {
				for (var i = 0; i < staff.data.length; i++) {
					if (portal.user.id == staff.data[i].name) {
						isStaff = staff.data[i];
						break;
					}
				}
			}
		},
		error: function (er) {
			console.log("ERROR: ", er);
		}
	});
}