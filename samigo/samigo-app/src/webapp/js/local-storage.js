/**
 * This script support to store the exercise into the local storage in client
 */

function isSupportStorage() {
	return typeof(Storage) !== "undefined";
}

/**
 * Save the json data into local storage with key "javaChecker".
 * @param jsonData
 * @returns false if the browser is not supported to save local storage.
 */
function saveLocalStorage(key, jsonData) {
	if (isSupportStorage()) {
		localStorage.setItem(key, jsonData);
		return true;
	} else {
		return false;
	}
}

/**
 * Get the local storage of key "javaChecker".
 * @returns data of key "javaChecker". If the browser is not support local storage, return null.
 */
function getLocalStorage(key) {
	if (isSupportStorage()) {
		return localStorage.getItem(key);
	} else {
		return null;
	}
}

function removeItem(key) {
	if (isSupportStorage()) {
		localStorage.removeItem(key);
	}
}

function clearCountdowns() {
	if (isSupportStorage()) {
		localStorage.removeItem("minDurationCountdown");
		localStorage.removeItem("maxDurationCountdown");
	}
}