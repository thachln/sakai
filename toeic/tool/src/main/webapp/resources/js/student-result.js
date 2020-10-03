var dataResult;
var dataItems;
var slider = $("#lightSlider").lightSlider();
var resultTable = null;
var itemTable = null;
var url_string = window.location.href;
var selectedAssessmentGID = null;
var selectedAssessmentDbclick = null;
var url = new URL(url_string);
var idAssessment = url.searchParams.get("idA");

$(function () {

	initPage();

	$('.col-sm-12.col-md-6:eq(0)').append(
		`
			<button class="btn btn-info" style="margin-left: 30px; margin-top: 0px;" id="exportSR" onclick="exportStudentAssessmentResult()" disabled>Export</button>
			<button class="btn btn-info" style="margin-left: 10px; margin-top: 0px;" id="exportA" onclick="exportAllAssessmentResult()" disabled>Export All</button>
		`
	);

	highlightSelectedRow();
	doubleClickRow();

	$('head').append(`<style>.button_color:focus, input[type="submit"]:focus, input[type="button"]:focus, input[type="reset"]:focus, button:focus, button.btn-primary:focus {
		outline: none !important;
	}</style>`);

	$("#dlgImages").on("shown.bs.modal", function () {
		slider.refresh();
	})
});

function initPage() {

	// Khởi tạo Table kết quả
	if (resultTable == null) {
		resultTable = $("#resultTable")
			.DataTable(
				{
					// responsive: true,
					// scrollX : true,
					"order": [1, 'asc'],
					columns: [{
						data: "agentId"
					}, {
						data: "attemptDate"
					}, {
						data: "submittedDate"
					}, {
						data: "finalScore"
					}, {
						data: "assessmentGradingId"
					}],

					columnDefs: [{
						targets: [1, 2],
						render: function (data, type, row) {
							if (data) {
								return formatDate(data);
							} else {
								return "";
							}
						}
					}, {
						targets: [3],
						render: function (data, type, row) {
							if (data) {
								return data;
							} else {
								return "";
							}
						}
					}, {
						targets: [4],
						render: function (data, type, row) {

							return `<a href="javascript:void(0)" onclick="loadPictures(${data})">Show</a>`
						}
					}]
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
					//					{
					//						data: "answerText"
					//					}, 
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
				//			    "drawCallback": function( settings ) {
				//			        settingShowHideLongText(); // will be called when dataTable.draw() is called;
				//			    },
				columnDefs: [
					{
						targets: [4],
						render: function (data, type, row) {

							return renderCorrect(data);
						}
					}
					// ,{
					// 	"targets": [ 2 ],
					// 	"visible": false,
					// 	"searchable": false
					// },
				]
			});
	}

	loadResult();

}

function loadResult() {
	showLoading('.container.home');
	waitingAlert("Loading all assessments data, please wait...");

	resultTable.clear().draw();
	$.ajax({
		type: "POST",
		url: "get-all-assessmentGrading?idA=" + idAssessment,
		dataType: 'json',
		contentType: "application/json",
		success: function (result) {
			console.log("load assessmentGrading success!");

			if (result.length) {
				$('#exportA').prop("disabled", false);
			}

			dataResult = result;

			resultTable.rows.add(dataResult); // Add new data
			resultTable.columns.adjust().draw(); // Redraw the DataTable

			// reset selected Assessment Grading Id
			selectedAssessmentGID = null;

			hideLoading('.container.home');
			successAlert();
		},
		error: function (e) {
			console.log("ERROR: ", e);
			errorAlert("Unexpected error occurred, please try again.");
			hideLoading('.container.home');
		}
	});
}

function loadItems(rowData) {
	if (rowData) {
		waitingAlert("Loading student's assessment data, please wait...");
		showLoading('.modal-content');
		itemTable.clear().draw();

		// if selected assessment have loaded before then just show with local data.
		if (localStorage.getItem('assessmentResult' + rowData.assessmentGradingId)) {
			dataItems = JSON.parse(localStorage.getItem('assessmentResult' + rowData.assessmentGradingId));

			itemTable.rows.add(dataItems); // Add new data
			itemTable.columns.adjust().draw(); // Redraw the DataTable

			hideLoading('.modal-content');
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

					hideLoading('.modal-content');
					successAlert();
				},
				error: function (e) {
					console.log("ERROR: ", e);
					errorAlert("Unexpected error occurred, please try again.");
					hideLoading('.modal-content');
				}
			});
		}
	}
}

function settingShowHideLongText() {

	//remove old click events.
	$("a.elipsis").unbind("click");

	// check if datatable have data. prevent from being called from itemTable.clear().draw();
	if ($("a.elipsis").length) {
		$("a.elipsis").on('click', function (e) {
			e.preventDefault();

			if ($(this).prev('span.elipsis').is(":visible")) {
				$(this).html(" Show");
			} else {
				$(this).html(" Hide");
			}

			$(this).prev('span.elipsis').fadeToggle(300);
		});
	}
}

// add color for isCorrect column
function renderCorrect(data) {
	if (data) {
		return `<p style="color: green">TRUE</p>`
	} else {
		return `<p style="color: red">FALSE</p>`
	}
}

function renderQuestion(data) {
	if (data) {
		try {
			data = JSON.parse(data);
			let name = data.audio.split("/");
			name = name.pop();
			let result = `<p><a href="${data.audio}">${name}</a></p> ${data.image ? `<p><img src="${data.image}" alt="Smiley face" height="42" width="42"></p>` : ''}`;

			return result;
		} catch (objError) {
			if (data.length > 20) {
				return data.substr(0, 20) + `<span class="elipsis">` + data.substr(20) + `</span><a class="elipsis" href="#"> Show</a>`;
			} else {
				return data;
			}
		}
	} else {
		return "";
	}
}


// shorten long text;
function renderAnswerText(data) {
	if (data) {
		if (data == '[Blank]') {
			return '';
		} else {
			if (data.length > 20) {
				return data.substr(0, 20) + `<span class="elipsis">` + data.substr(20) + `</span><a class="elipsis" href="#"> Show</a>`;
			} else {
				return data;
			}
		}
	} else {
		return '';
	}
}

function exportAllAssessmentResult() {
	let url = 'export-students-result?idA=' + idAssessment;
	downloadFile(url);
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

function loadPictures(idG) {
	slider.destroy();
	$("#dlgImagesBody").empty();
	$("#dlgImagesBody").append(`<ul id="lightSlider"></ul>`);
	$.ajax({
		type: "POST",
		url: `picture/find-by-assessmentGradingId?idG=${idG}`,
		dataType: 'json',
		contentType: "application/json",
		success: function (data) {
			console.log("loadPictureIds: ", data);
			if (data) {
				for (var i = 0; i < data.length; i++) {
					if (data[i].type.includes("image")) {
						$("#lightSlider").append(`<li data-thumb="picture/${data[i].id}"><img
						src="picture/${data[i].id}" /></li>`);
					} else {
						$("#lightSlider").append(`<li data-thumb="picture/${data[i].id}">
						<video width="320" height="240" controls>
								<source src="picture/${data[i].id}" type="${data[i].type}">
						</video></li>`);
					}
				}

				refreshSlider(data.length);
				$("#dlgImagesBody").css("height", "");
			} else {
				$("#dlgImagesBody").append("<p>No data available.</p>");
				$("#dlgImagesBody").css("height", "550px");
			}
		},
		error: function (er) {
			console.log("ERROR: ", er);
			errorAlert("unexpected error occurred while loading pictures, please refresh this page...");
		}
	});
}

function refreshSlider(length) {
	slider = $('#lightSlider').lightSlider({
		gallery: true,
		item: 1,
		slideMargin: 0,
		thumbItem: length < 4 ? 4 : length
	});

	$("#dlgImages").modal("show");
}