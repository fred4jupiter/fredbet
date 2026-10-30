"use strict";

$(function () {
    // the logo is embedded as data URL, so it is copied here instead of being sent a second time within the page
    const $logo = $('#loginLogo');
    if ($logo.length) {
        $('.login-visual-backdrop').css('background-image', 'url("' + $logo.attr('src') + '")');
    }
});
