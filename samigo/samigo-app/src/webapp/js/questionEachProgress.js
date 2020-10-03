<!-- Javascript for the Limited Duration Question Progress Panel-->
$(function() {
	var minDuration = $("#minDuration").text();
	var maxDuration = $("#maxDuration").text();

	var xMin;
	var xMax;
//	console.log("minDuration=" + minDuration);
//	console.log("maxDuration=" + maxDuration);

	// Update the count down every 1 second
	
	if (minDuration > 0 || maxDuration > 0) {
		var minDurationCountdown = minDuration;
		var maxDurationCountdown = maxDuration;
		
		// Check data from local storage
		if (isSupportStorage()) {
			var localData = getLocalStorage("minDurationCountdown");
			
			if (localData != null) {
				var jsonLocalData = JSON.parse(localData);
				
				if (jsonLocalData != null && jsonLocalData.minDurationCountdown != null) {
					minDurationCountdown = jsonLocalData.minDurationCountdown;
				}
			} else {
				console.log("There is not previous minDurationCountdown.");
			}
				
			localData = getLocalStorage("maxDurationCountdown");
			
			if (localData != null) {
				var jsonLocalData = JSON.parse(localData);
				
				if (jsonLocalData != null && jsonLocalData.maxDurationCountdown != null) {
					maxDurationCountdown = jsonLocalData.maxDurationCountdown;
				}
			} else {
				console.log("There is not previous maxDurationCountdown.");
			}
		} else {
			console.log("The browser is not supported local storage!");
		}

		if (minDuration > 0) {
			minDurationCountdown = Math.min(minDuration, minDurationCountdown);
			
			// Disable button "Next".
			$("#takeAssessmentForm\\:next").attr("disabled", true);
			$("[id*='nextTop']").attr("disabled", true);

//			console.log("minDurationCountdown=" + minDurationCountdown);
			
			var minDurationCountdownLab = $("#minDurationCountdownLab");
			// Create timer for min duration
			xMin = setInterval(function() {
				if (minDurationCountdown > 0) {
					minDurationCountdown--;
					
					// Update label
					minDurationCountdownLab.text(minDurationCountdown);
					$("#minDurationCountdown").val(minDurationCountdown);
					// Store
					var jsonData = JSON.stringify({'minDurationCountdown': minDurationCountdown});
					saveLocalStorage("minDurationCountdown", jsonData);
					
					// Highlight
					if (minDurationCountdown < 6) {
						minDurationCountdownLab.css("color", "red");
					}
				} else {
					clearInterval(xMin);
					endMinDuration();
				}
			}, 1000);
		} else {
//			console.log("Not minDuration > 0; minDuration = " + minDuration);
		}
		
		if (maxDuration > 0) {
			maxDurationCountdown = Math.min(maxDuration, maxDurationCountdown);
			console.log("maxDurationCountdown=" + maxDurationCountdown);

			var maxDurationCountdownLab = $("#maxDurationCountdownLab");
			// Create timer for max duration
			xMax = setInterval(function() {
	
				if (maxDurationCountdown > 0) {
					maxDurationCountdown--;
					
					// Update label
					maxDurationCountdownLab.text(maxDurationCountdown);
					$("#maxDurationCountdown").val(maxDurationCountdown);
					var jsonData = JSON.stringify({'maxDurationCountdown': maxDurationCountdown});
					saveLocalStorage("maxDurationCountdown", jsonData);
					
					// Highlight
					if (maxDurationCountdown < 6) {
						maxDurationCountdownLab.css("color", "red");
					}
				} else {
					endMaxDuration();
					clearInterval(xMax);
					
				}
			}, 1000);
		} else {
//			console.log("Not maxDuration > 0; maxDuration = " + maxDuration);
		}
	
	} else {
//		console.log("Not minDuration > 0 || maxDuration > 0: " + (minDuration > 0 || maxDuration > 0));
	}
	
	// Capture the click event of Next button: clear the countdown
	$("#takeAssessmentForm\\:next").click(function(e) {
		clearCountdowns();
		clearInterval(xMin);
		clearInterval(xMax);
	});
	
	$("[id*='nextTop']").click(function(e) {
		clearCountdowns();
		clearInterval(xMin);
		clearInterval(xMax);
	});
});

function clearCountdowns() {
	if (isSupportStorage()) {
		localStorage.removeItem("minDurationCountdown");
		localStorage.removeItem("maxDurationCountdown");
	}
}

/**
 * Processing after finished countdown of Minimum
 * @returns none
 */
function endMinDuration() {
//	console.log("End min duration");
	// Enable button "Next"
	$('#takeAssessmentForm\\:next').removeAttr("disabled");
	$("[id*='nextTop']").removeAttr("disabled");
}

/**
 * Processing after finished countdown of Maximum
 * @returns none
 */
function endMaxDuration() {
	
	var btnSubmitForGrade = $("#takeAssessmentForm\\:submitForGrade");
//	console.log("End max duration; btnSubmitForGrade=" + btnSubmitForGrade + ";(btnSubmitForGrade === undefined)=" + (btnSubmitForGrade === undefined) + ";(btnSubmitForGrade === null)=" + (btnSubmitForGrade === null)
//			+ "(btnSubmitForGrade.attr('class') != 'active') = " + (btnSubmitForGrade.attr('class') != "active"));
	if (btnSubmitForGrade === undefined || btnSubmitForGrade === null || btnSubmitForGrade.attr('class') != 'active') {
		// Click button "Next"
		var btnNext = $("#takeAssessmentForm\\:next");
		
		if (btnNext != null) {
//			console.log("Call btnNext.click()");
			
			clearCountdowns();

			btnNext.click();
		} else {
			console.log("Warning: do nothing")
		}
	} else {
//		console.log("Call btnSubmitForGrade.click()");
		clearCountdowns();
		btnSubmitForGrade.click();
	}

}
