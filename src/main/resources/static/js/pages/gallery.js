"use strict";

$(function () {
    var $links = $('#links a');

    // all images of the page form one gallery, see image_viewer.js
    var items = $links.map(function () {
        var $link = $(this);
        return {
            src: $link.attr('href'),
            alt: $link.find('img').attr('alt') || '',
            caption: $link.attr('title') || ''
        };
    }).get();

    $('#links').on('click', 'a', function (event) {
        event.preventDefault();
        openImageViewer(items, $links.index(this), $(this));
    });
});
