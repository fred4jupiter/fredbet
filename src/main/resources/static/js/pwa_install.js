"use strict";
// Registers the service worker and shows a hint to install FredBet as app (PWA) on mobile devices.
(function () {
	const DISMISSED_KEY = 'fredbet.pwa.installHintDismissed';
	let installPromptEvent = null;

	// the browser fires this event when the app can be installed; keep it to open the install dialog later
	$(window).on('beforeinstallprompt', function (event) {
		event.preventDefault();
		installPromptEvent = event.originalEvent;
		$(showInstallHint);
	});

	$(window).on('appinstalled', function () {
		installPromptEvent = null;
		$('#pwaInstallHint').addClass('d-none');
	});

	function readDismissed() {
		try {
			return window.localStorage.getItem(DISMISSED_KEY) === 'true';
		} catch (e) {
			return false;
		}
	}

	function storeDismissed() {
		try {
			window.localStorage.setItem(DISMISSED_KEY, 'true');
		} catch (e) {
			// storage not available (e.g. private mode), the hint shows up again next time
		}
	}

	function isStandalone() {
		return window.matchMedia('(display-mode: standalone)').matches || window.navigator.standalone === true;
	}

	function isMobile() {
		return window.matchMedia('(pointer: coarse)').matches;
	}

	function isIos() {
		const ua = window.navigator.userAgent;
		// iPadOS reports itself as Macintosh, but has a touch screen
		return /iPhone|iPad|iPod/i.test(ua) || (/Macintosh/i.test(ua) && window.navigator.maxTouchPoints > 1);
	}

	function canShowHint() {
		return !isStandalone() && isMobile() && !readDismissed();
	}

	function showInstallHint() {
		if (!installPromptEvent || !canShowHint()) {
			return;
		}
		const $hint = $('#pwaInstallHint');
		$hint.find('.pwa-install-default').removeClass('d-none');
		$hint.find('.pwa-install-ios').addClass('d-none');
		$hint.removeClass('d-none');
	}

	function showIosHint() {
		// iOS has no install event, the user has to use "Add to Home Screen" of the share menu
		if (!isIos() || !canShowHint()) {
			return;
		}
		const $hint = $('#pwaInstallHint');
		$hint.find('.pwa-install-default').addClass('d-none');
		$hint.find('.pwa-install-ios').removeClass('d-none');
		$hint.removeClass('d-none');
	}

	$(function () {
		const $hint = $('#pwaInstallHint');
		if ($hint.length === 0) {
			return;
		}

		if ('serviceWorker' in window.navigator) {
			window.navigator.serviceWorker.register($hint.data('swUrl'), {scope: $hint.data('swScope')})
				.catch(function (error) {
					console.warn('Service worker registration failed', error);
				});
		}

		$('#pwaInstallButton').on('click', function () {
			if (!installPromptEvent) {
				return;
			}
			installPromptEvent.prompt();
			installPromptEvent.userChoice.then(function () {
				installPromptEvent = null;
				$hint.addClass('d-none');
			});
		});

		$('#pwaInstallClose').on('click', function () {
			storeDismissed();
			$hint.addClass('d-none');
		});

		showIosHint();
	});
})();
