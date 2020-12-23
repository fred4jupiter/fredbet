function compressFile() {
    const compress = new Compress()

    // or simpler
    compress.attach('#myFile', {
        size: 4,
        quality: .75
    }).then((data) => {
        $('#myFileBase64').val(data.data);
    })
}

