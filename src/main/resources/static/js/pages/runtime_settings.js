$(document).ready(function() {
	triggerCheckboxSelfRegistration();
});

function triggerCheckboxSelfRegistration() {
	if($('#selfRegistrationEnabled').prop("checked")) {
		$('#registrationCodeBox').show();
	} else {
		$('#registrationCodeBox').hide();
	}
}


