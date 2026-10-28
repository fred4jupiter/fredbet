"use strict";

$(document).ready(function () {
    // the logo is embedded into the login page, so it is kept smaller than gallery images
    initImageCropper({autoCropArea: 1})
        .submitCroppedImage(document.getElementById('loginLogoForm'), document.getElementById('imageFile'), {maxWidth: 1200, maxHeight: 1200});
});
