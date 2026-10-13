$(function () {
    $('#links').on('click', 'a', function (event) {
        event.preventDefault();
        blueimp.Gallery($('#links a').get(), {
            index: this,
            event: event.originalEvent,
            fullscreen: true
        });
    });
});
