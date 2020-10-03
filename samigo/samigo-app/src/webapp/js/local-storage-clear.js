<!-- Javascript for the Limited Duration Question Progress Panel-->
$(function() {
	if (typeof(Storage) !== "undefined") {
		localStorage.removeItem("minDurationCountdown");
		localStorage.removeItem("maxDurationCountdown");
	}
});