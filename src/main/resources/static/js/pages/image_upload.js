"use strict";

$(document).ready(function () {
    // gallery images keep their own format, the crop frame starts on the whole image
    var imageCropper = initImageCropper({
        autoCropArea: 1
    });

    $('#imageUploadForm').on('submit', function (ev) {
        if (!imageCropper.hasImage()) {
            // let the server answer with "no file given"
            return;
        }

        ev.preventDefault();
        var form = this;
        $('#uploadImage').prop('disabled', true);
        $('#spinner').removeClass('d-none');

        imageCropper.toFileInput(document.getElementById('imageFile'), {maxWidth: 1920, maxHeight: 1920}, function () {
            // the native submit does not trigger this handler again
            form.submit();
        });
    });
});
