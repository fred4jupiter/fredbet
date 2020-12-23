"use strict";
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
