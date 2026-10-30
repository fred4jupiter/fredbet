$(document).ready(function () {
    // the server cuts a square thumbnail from the image, so the crop frame is square as well
    var imageCropper = initImageCropper({
        aspectRatio: 1,
        autoCropArea: 0.9
    });

    $('#crop-image').on('click', function () {
        $('#saveCroppedImage').prop('disabled', true);

        imageCropper.toFileInput(document.getElementById('croppedFile'), {maxWidth: 800, maxHeight: 800}, function (blob) {
            var resultImage = document.getElementById('resultImage');
            if (resultImage.src.startsWith('blob:')) {
                URL.revokeObjectURL(resultImage.src);
            }
            resultImage.src = URL.createObjectURL(blob);

            $('#saveCroppedImage').prop('disabled', false);
        });
    });

    $('#saveCroppedImage').on('click', function () {
        $('#spinner').removeClass('d-none');
    });
});
