/*=========================================================================================
  File Name: wp-control.js
  Description: Controls for Web parts.
  ----------------------------------------------------------------------------------------
  Item Name: Extentions Robust - Responsive Admin Template
  Version: 1.0
  Author: ThachLN
  Author URL: hhttp://myworkspace.vn/user/thachln
==========================================================================================*/

(function(window, document, $) {
    console.log("Extension of Robust Admin...");
    if (isSupportStorage()) {
	} else {
		console.log("The browser is not supported local storage!");
	}
	
	// Override Robust Admin: app.js
	// Toggle fullscreen
	$('a[data-action="expand"]').on('click',function(e){
		e.preventDefault();
		$(this).closest('.card').find('[data-action="expand"] i').toggleClass('ft-maximize ft-minimize');
		$(this).closest('.card').toggleClass('card-fullscreen');
	});
})(window, document, jQuery);

/**
 * Call back function to receive notification from them Robust Admin
 * @param id direct tool link. Ex: http://localhost:7070/portal/directtool/786af851-4249-41d5-9dd4-e7fe00ad7bf2/
 * @param curClass status of the tool ft_maximize | ft_minimize (refer status of "card").
 * <br/>
 * This status is before the control item is clicked.
 * @returns
 */
function nextMaxMinProcess(id, curClass) {

	// Save the link of the tool, current status: maximize | minimize
	// Save the maximize tool into the session
	if (curClass == "ft-maximize") {
		localStorage.setItem("ft-maximize", id);
		sessionStorage.setItem("ft-maximize", id);
	} else {
		sessionStorage.removeItem("ft-maximize");
		localStorage.removeItem("ft-maximize");
	}
}

function getDisplayStatus(id) {
	var displayStatus = localStorage.getItem(id);
	console.log("Get Display status of tool " + id + "=" + displayStatus);
	
	return displayStatus;
}