// Tho.Add.Start. Tracking progress of video
// Refer properties of event at https://www.w3schools.com/tags/ref_av_dom.asp
var deltaTime = 60; // 1 minutes
var lastTime = 0;
var currTime; 
var lastPercent = 0;
var percent; // count % played video

function trackProgress(event, siteId, movieUrl, itemId) {
   // The currentTime property returns the current position of the audio/video playback
   
   currTime = event.currentTime;
   percent = currTime / event.duration * 100;

   // Log for debug after each +1%
   if ((percent - lastPercent >= 1) || (percent >= 100)) {
       console.log("Log percent: learned %=" + percent);

       var url = location.protocol + '//' + location.host + '/lessonbuilder-tool/ajax?op=sendprogress' +
       '&siteid=' + siteId + '&itemid=' + itemId + '&percent=' + percent;
       // call ajax to submit to server.
		$.ajax({
			url : url,
			type : 'GET',
			success : function(result) {
				// console.log("ajax result=" + result);
				percent = Math.round(percent);
				// console.log("Update the progress:" + percent)
				// Determine id of span 
				var spanId = itemId + "_" + "progressId";
				var tagSpanId = "#" + spanId;
				$(tagSpanId).text(" " + percent + '%');
				
				// Change the progress icon from None to On-Going
				if (lastPercent == 0 && percent > 0) {
					$(tagSpanId).removeClass("glyphicon-bullhorn");
					$(tagSpanId).removeClass("text-danger");
					$(tagSpanId).addClass("glyphicon-dashboard");
					$(tagSpanId).addClass("text-warning");

				} else if (percent > 80) {
				// Change the progress icon from On-Going to Completed
					$(tagSpanId).removeClass("glyphicon-dashboard");
					$(tagSpanId).removeClass("text-warning");
					$(tagSpanId).addClass("glyphicon-ok-sign");
					$(tagSpanId).addClass("text-success");
				}
			},
			error : function() {
				console.log("Error!");
			}
		});

		lastPercent = percent;
   }
   
   if (currTime - lastTime > deltaTime) {
       // After frequency, update status
       // Log for debug
//	   console.log("Item Id=" + itemId);
//       console.log("Video Url=" + movieUrl);
//       console.log("currentTime=" + event.currentTime);
//       console.log("currentSrc=" + event.currentSrc);
//       console.log("src=" + event.src);
//       console.log("duration=" + event.duration);
//       console.log("ended=" + event.ended);
//       console.log("networkState=" + event.networkState);
//       console.log("paused=" + event.paused);
//       console.log("error=" + event.error);
//       console.log("readyState=" + event.readyState);
//       console.log("seeking=" + event.seeking);
//       console.log("startDate=" + event.startDate);

       // Re-update the lastTime
       lastTime = currTime;
       
       console.log("Log time: learned %=" + percent);
   }
}

function loadStart(id) {
	console.log("Starting to load video");
//	var videoTag = document.getElementById("video0");
//	videoTag.addClass('loading');
	var videoId = '#' + id; 
	$(videoId).addClass('loading');
}

function canPlay() {
	console.log("The video can play");
	var videoId = '#' + id; 
	$(videoId).removeClass('loading');
}
// Tho.Add.End

$(document).ready(function() {

//  $('#video_id').on('canplay', function (event) {
//    $(this).removeClass('loading');
//  });

	var videoTag = document.getElementById("video0");
	// console.log("videoTag=" + videoTag);
	if (videoTag) {
		// Add event to initialize the position at the last position
		videoTag.addEventListener('loadedmetadata', function() {
			var startPosElement = document.getElementById("video_start_pos");
			if (startPosElement) {
				var currentPercent = startPosElement.value;
				// console.log("currentPercent=" + currentPercent);
				// console.log("video duration=" + videoTag.duration);
				this.currentTime = videoTag.duration * currentPercent / 100;
			} else {
				console.log("Element 'video_start_pos' is not found.");
			}
		}, false);

		// Add event to play the next video
		// 1. Get the next video from the hidden input
		var nextLinkButton = document.getElementById("next1");
		
		console.log("nextLinkButton=" + nextLinkButton);
		// 2. Add event to play the next video if reached at the end.
		if (nextLinkButton) {
			// var nextVideoSrc = nextUrlSrcInput.value;
			var nextLinkUrl = nextLinkButton.href;
			console.log("nextLinkUrl=" + nextLinkUrl);
			if (nextLinkUrl) {
				videoTag.addEventListener('ended', function(e) {

				  console.log("Next page:" + nextLinkUrl);

				  window.open(nextLinkUrl, "_self");
				});
			}
		}

	} else {
		// console.log("No videoTag");
	}

});