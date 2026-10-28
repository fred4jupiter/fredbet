$(document).ready(function () {
    imageCroppingUpload();

    $('#saveCroppedImage').on('click', function () {
        $('#spinner').removeClass('d-none');
    });
});

function imageCroppingUpload() {
    var cropper = null;
    var image = document.getElementById('crop-source');

    // the server cuts a square thumbnail from the image, so the crop frame is square as well
    var opts = {
        aspectRatio: 1,
        viewMode: 1,
        dragMode: 'move',
        autoCropArea: 0.9,
        background: false
    };

    $('#upload').on('change', function () {
        var file = this.files && this.files[0];
        if (!file) {
            return;
        }

        if (cropper) {
            cropper.destroy();
            URL.revokeObjectURL(image.src);
        }

        // Cropper.js measures its container, so it has to be visible before it starts
        $('#crop-editor').removeClass('d-none');
        image.src = URL.createObjectURL(file);
        cropper = new Cropper(image, opts);
    });

    $('.crop-image-rotate').on('click', function () {
        cropper.rotate(parseInt($(this).data('deg')));
    });

    $('#crop-image').on('click', function () {
        $('#saveCroppedImage').prop('disabled', true);

        var canvas = cropper.getCroppedCanvas({
            maxWidth: 800,
            maxHeight: 800,
            fillColor: '#fff',
            imageSmoothingQuality: 'high'
        });
        canvas.toBlob(function (blob) {
            // put the cropped image into the file input, so the form uploads it like a chosen file
            var transfer = new DataTransfer();
            transfer.items.add(new File([blob], 'profile.jpg', {type: 'image/jpeg'}));
            document.getElementById('croppedFile').files = transfer.files;

            var resultImage = document.getElementById('resultImage');
            if (resultImage.src.startsWith('blob:')) {
                URL.revokeObjectURL(resultImage.src);
            }
            resultImage.src = URL.createObjectURL(blob);

            $('#saveCroppedImage').prop('disabled', false);
        }, 'image/jpeg', 0.9);
    });
}
