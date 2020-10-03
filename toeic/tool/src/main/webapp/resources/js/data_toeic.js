const suggestComment = {
   level1: 'is very bad',
   level2: 'is quite bad',
   level3: 'is average',
   level4: 'is very good',
   level5: 'is excellent',
   badListening: `You should improve your English listening skills, learn new words, listen to their pronunciation and practise using them.`,
   goodListening: 'Your English listening skills is quite good. You need to maintain and promote them.',
   badReading: 'You should increase your vocabulary and improve your grammar.',
   goodReading: 'Your reading comprehensive part is verry good. You need to maintain and promote them.'
};

var infoEachPart = {};

// error messages
const error_network = "Couldn't load question, your internet access might be disconnected.";
const error_number = "Couldn't number audio.";
const error_look = "Couldn't look at number audio.";
const error_download = "Unexpected error occurred while downloading, please try again.";
const error_retry = `Couldn't load question, your internet access might be disconnected. <input type="button" id="retry" value="Retry">`;
const error = "Unexpected error occurred, please try again or refresh this page.";
const wait_download = "Your download is starting, please wait...";
const wait_submit = "Submitting your assessment, please wait...";
const wait_save = "Saving your answers, please wait...";
const wait = "Progressing, please wait...";

function waitingAlert(message) {
   toastr.remove();
  toastr.info(message, '', {positionClass: 'toast-top-center', containerId: 'toast-top-center', "timeOut": 0, "extendedTimeOut": 0});
}

function successAlert(){
  toastr.remove();
  toastr.success('', 'Successful', {positionClass: 'toast-top-center', containerId: 'toast-top-center'});
}

function errorAlert(message) {
  toastr.error(message, '', {positionClass: 'toast-top-center', containerId: 'toast-top-center', "timeOut": 0, "extendedTimeOut": 0, tapToDismiss: false});
}