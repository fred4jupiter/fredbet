/**
 * Photo editor based on Cropper.js, used on the profile image page and the image upload page.
 *
 * Expected markup: a file input #upload, a hidden container #crop-editor holding an img #crop-source,
 * and buttons with the class .crop-image-rotate and a data-deg attribute.
 */
function initImageCropper(cropperOptions) {
    var cropper = null;
    var image = document.getElementById('crop-source');

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
        cropper = new Cropper(image, $.extend({
            viewMode: 1,
            dragMode: 'move',
            background: false
        }, cropperOptions));
    });

    $('.crop-image-rotate').on('click', function () {
        cropper.rotate(parseInt($(this).data('deg')));
    });

    return {
        hasImage: function () {
            return cropper !== null;
        },

        /**
         * Puts the cropped image as JPEG file into the given file input, so the form uploads it like a chosen file.
         * The callback receives the image as Blob, e.g. for a preview.
         */
        toFileInput: function (fileInput, canvasOptions, callback) {
            var canvas = cropper.getCroppedCanvas($.extend({
                fillColor: '#fff',
                imageSmoothingQuality: 'high'
            }, canvasOptions));

            canvas.toBlob(function (blob) {
                var transfer = new DataTransfer();
                transfer.items.add(new File([blob], 'image.jpg', {type: 'image/jpeg'}));
                fileInput.files = transfer.files;
                callback(blob);
            }, 'image/jpeg', 0.9);
        }
    };
}
