/**
 * This script support to store the exercise into the local storage in client
 */
var localStorageKey = "codeonline";

function isSupportStorage() {
	return typeof(Storage) !== "undefined";
}

/**
 * Save the json data into local storage with key "javaChecker".
 * @param jsonData
 * @returns false if the browser is not supported to save local storage.
 */
function saveLocalStorage(jsonData) {
	if (isSupportStorage()) {
		localStorage.setItem(localStorageKey, jsonData);
		return true;
	} else {
		return false;
	}
}

/**
 * Get the local storage of key "javaChecker".
 * @returns data of key "javaChecker". If the browser is not support local storage, return null.
 */
function getLocalStorage() {
	if (isSupportStorage()) {
		return localStorage.getItem(localStorageKey);
	} else {
		return null;
	}
}
