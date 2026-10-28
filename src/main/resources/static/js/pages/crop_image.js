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
        var canvas = cropper.getCroppedCanvas({
            maxWidth: 800,
            maxHeight: 800,
            fillColor: '#fff',
            imageSmoothingQuality: 'high'
        });
        var dataUrl = canvas.toDataURL('image/jpeg', 0.9);
        $('#resultImage').attr('src', dataUrl);
        $('#croppedFileBase64').val(dataUrl);
    });
}
