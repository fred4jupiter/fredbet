"use strict";

$(document).ready(function() {
    $('.rotate-image-btn').on('click', function(ev) {
        var degress = $(this).data('deg');
        var data = $('#preview').attr('src');
        rotate(data, degress, function (result) {
            $('#preview').attr('src', result);
            $("#myFileBase64").attr("value", result);
        });
    });

    $(".popup-link").on("click", function() {
        var imageUrl = $(this).data('image-url');
        $('#imagepreview').attr('src', imageUrl); // here asign the image to the modal when the user click the enlarge link
        $('#imagemodal').modal('show'); // imagemodal is the id attribute assigned to the bootstrap modal, then i use the show function
    });

    const compressor = new Compress();
    $('#upload').on('change', function () {
        compressor.compress([...this.files], {
            size: 4,
            quality: .75
        }).then(function (results) {
            const output = results[0];
            const base64 = output.prefix + output.data;
            $('#preview').attr('src', base64);
            $('#myFileBase64').attr('value', base64);
        });
    });
});

function rotate(srcBase64, degrees, callback) {
    const canvas = document.createElement('canvas');
    let ctx = canvas.getContext("2d");
    let image = new Image();

    image.onload = function () {
        canvas.width = degrees % 180 === 0 ? image.width : image.height;
        canvas.height = degrees % 180 === 0 ? image.height : image.width;

        ctx.translate(canvas.width / 2, canvas.height / 2);
        ctx.rotate(degrees * Math.PI / 180);
        ctx.drawImage(image, image.width / -2, image.height / -2);

        callback(canvas.toDataURL("image/jpeg", 100));
    };

    image.src = srcBase64;
}
