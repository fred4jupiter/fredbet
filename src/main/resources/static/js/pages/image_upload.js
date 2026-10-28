"use strict";

$(document).ready(function () {
    // gallery images keep their own format, the crop frame starts on the whole image
    initImageCropper({autoCropArea: 1})
        .submitCroppedImage(document.getElementById('imageUploadForm'), document.getElementById('imageFile'), {maxWidth: 1920, maxHeight: 1920});

    // the delete dialog is shared by all rows, so it gets the link of the clicked image
    $('#confirm-delete-image').on('show.bs.modal', function (event) {
        $(this).find('a.btn-danger').attr('href', $(event.relatedTarget).data('href'));
    });
});
