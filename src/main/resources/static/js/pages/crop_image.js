$(document).ready(function () {
    imageCroppingUpload();
});

function imageCroppingUpload() {
    var $uploadCrop;

    var opts = {
        viewport: {width: 300, height: 300},
        boundary: {width: 300, height: 300},
        type: 'canvas',
        enableResize: true,
        enableExif: true,
        enableOrientation: true,
        format: 'jpeg'
    };

    function readFile(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();

            reader.onload = function (e) {
                $uploadCrop.croppie('bind', {
                    url: e.target.result
                }).then(function () {
                    console.log('jQuery bind complete');
                });
            }
            reader.readAsDataURL(input.files[0]);
        } else {
            console.log("Sorry - you're browser doesn't support the FileReader API");
        }
    }

    $('#upload').on('change', function () {
        readFile(this);
    });

    $uploadCrop = $('#croppie-container').croppie(opts);

    $('.crop-image-rotate').on('click', function (ev) {
        $uploadCrop.croppie('rotate', parseInt($(this).data('deg')));
    });

    $('#crop-image').on('click', function (ev) {
        $uploadCrop.croppie('result', opts).then(function (resp) {
            $("#resultImage").attr("src", resp);
            $("#croppedFileBase64").attr("value", resp);
        });
    });
}
