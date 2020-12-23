"use strict";

$(document).ready(function() {
    var angle = 0;
    $('#rotateRight').on('click', function() {
        angle += 90;
        $("#preview").rotate(angle);
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
        console.log(results)
        const output = results[0]
        const file = Compress.convertBase64ToFile(output.data, output.ext)
        console.log(file)
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

        callback(canvas.toDataURL());
    };

    image.src = srcBase64;
}
