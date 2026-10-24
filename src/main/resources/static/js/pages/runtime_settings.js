$(function () {
	$('#selfRegistrationEnabled').on('change', function () {
		$('#registrationCodeBox').toggleClass('d-none', !this.checked);
	});
});
