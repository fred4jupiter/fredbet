"use strict";
$(function () {
	const $info = $('#connectionInfo');

	// origin (protocol, host and port) plus the context path of the application, e.g. https://fredbet.example.com/
	$('#connectionBaseUrl').text(window.location.origin + $info.data('contextRoot'));

	const standalone = window.matchMedia('(display-mode: standalone)').matches || window.navigator.standalone === true;
	$('#connectionMode').text(standalone ? $info.data('modeApp') : $info.data('modeBrowser'));

	$('#connectionSecure').text(window.location.protocol === 'https:' ? $info.data('yes') : $info.data('no'));
});
