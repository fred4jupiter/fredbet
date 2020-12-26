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
        console.log('imageUrl: '+imageUrl);
        $('#imagepreview').attr('src', imageUrl); // here asign the image to the modal when the user click the enlarge link
        $('#imagemodal').modal('show'); // imagemodal is the id attribute assigned to the bootstrap modal, then i use the show function
    });
});

const compressor = new Compress()
const upload = document.getElementById('upload')
const preview = document.getElementById('preview')

upload.addEventListener('change', function (evt) {
    const files = [...evt.target.files]
    compressor.compress(files, {
        size: 4,
        quality: .75
    }).then((results) => {
        const output = results[0]
        const file = Compress.convertBase64ToFile(output.data, output.ext)
        preview.src = output.prefix + output.data
        $("#myFileBase64").attr("value", output.prefix + output.data);
    })
}, false)

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
