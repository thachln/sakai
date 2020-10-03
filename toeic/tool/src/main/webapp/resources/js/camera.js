var videoElement = document.querySelector('video#screenshot');
var videoSelect = document.querySelector('select#videoSource');

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
  $("#recognition").removeClass("hidden");
}

function handleError(error) {
  console.log('Error: ', error);
  if (!start) {
    handleFlag(true);
    $("#recognition").addClass("hidden");
  } else if (!end) {
    handleFlag(false, true);
    $("#recognition").addClass("hidden");
  }
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
  img.src = canvas.toDataURL('image/webp');
  canvas.toBlob(function (blob) {
    saveImages(blob);
    console.log(blob);
  }, 'image/png', 1);

  stopCamera();
  if (!start) {
    handleFlag(true);
    $("#recognition").addClass("hidden");
  } else if (!end) {
    handleFlag(false, true);
    $("#recognition").addClass("hidden");
  }
}
function handleFlag(s, e, p) {
  if (s) {
    isStart = true;
    startTest();
    start = true;
  } else if (e) {
    submit(true);
    end = true;
  } else if (p) {

  }
}

function stopCamera() {
  if (window.stream) {
    window.stream.getTracks().forEach(function (track) {
      track.stop();
    });
  }
}