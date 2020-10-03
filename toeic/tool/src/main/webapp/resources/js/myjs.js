$(function() {
	$('#confirm_password').on('click', '.btn-close', function () {
		// similar behavior as clicking on a link
		window.location.href = "home";
	});

	// Bind click to OK button within popup
	$('#confirm_password').on('click', '.btn-ok', function(e) {
		var $modalDiv = $(e.delegateTarget);		
		if ($('#pass_ser').val() == assessmentData.password) {
			$modalDiv.modal('hide').removeClass('loading');			
		} else {
			$modalDiv.modal('hide').removeClass('loading');	
			window.location.href = "home";
		}

	});
	
//	// Get list Assessment
//	$.ajax({
//		type: "GET",
//		url: "getListAssessment",
//		success: function(data) {
//			console.log("SUCCESS getListAssessment: ", data);
//			//alert(data);
//		},
//		error: function(er) {
//			console.log("ERROR: ", er);
//
//		}
//	});
	
//	var url_string = window.location.href;
//	var url = new URL(url_string);
//	var idAssessment = url.searchParams.get("idA");
//	
//	if (idAssessment) {
//		console.log("Success " + idAssessment);
//		// Get data Assessment
//		$.ajax({
//			type: "GET",
//			url: "getDataTest/" + idAssessment,
//			success: function(data) {
//				console.log("SUCCESS data Assessment: ", JSON.parse(data));
//				//alert(data);
//			},
//			error: function(er) {
//				console.log("ERROR: ", er);
//
//			}
//		});
//	}
	
	// Bind click to OK button within popup
	$('#confirm-submit').on('click', '.btn-ok', function(e) {

		var $modalDiv = $(e.delegateTarget);
		//var id = $(this).data('recordId');
		console.log("submited");
		$modalDiv.addClass('loading');
		// $.post('/api/record/submit').then(function() {
		// 	$modalDiv.modal('hide').removeClass('loading');
		// });

		// Test
		$modalDiv.modal('hide').removeClass('loading');
		$('.modal-backdrop').remove();
		//finishToeic();
		submitForGrading();
	});

	// Bind click to Reset button within popup and call func reset test
	$('#confirm-reset').on('click', '.btn-ok', function(e) {
		var $modalDiv = $(e.delegateTarget);
		// Test
		$modalDiv.modal('hide').removeClass('loading');
		resetPage();
	});


	// Bind to modal opening to set necessary data properties to be used to make request
	// $('#confirm-submit').on('show.bs.modal', function(e) {
	//   var data = $(e.relatedTarget).data();
	//   $('.title', this).text(data.recordTitle);
	//   $('.btn-ok', this).data('recordId', data.recordId);
	// });


	function submit_demo() {
		var demoText = "Daylademo";
		$.ajax({
			type: "GET",
			url: "submit",
			data: {
				demoText: demoText
			},
			success: function(data) {
				console.log("SUCCESS: ", data);
				alert(data);
			},
			error: function(er) {
				console.log("ERROR: ", er);

			}
		});

		return false;
	}

	// Disable right click on web page
	$("html").on("contextmenu",function(e){
	    return false;
	});
	// Disable cut, copy and paste on web page
	$('html').bind('cut copy paste', function (e) {
	     e.preventDefault();
	});

	// $(document).keydown(function (event) {
	//     if (event.keyCode == 123) { // Prevent F12
	//         return false;
	//     } else if (event.ctrlKey && event.shiftKey && event.keyCode == 73) { // Prevent Ctrl+Shift+I        
	//         return false;
	//     }
	// });
});