$(function() {
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
		finishToeic();
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
});