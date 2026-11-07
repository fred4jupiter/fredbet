"use strict";

$(function () {
    var $links = $('#links a');
    if (!$links.length) {
        return;
    }

    var LOADING_HTML = '<div class="gallery-slide-loading"><div class="spinner-border text-light" role="status"></div></div>';

    // PhotoSwipe needs the size of an image to fit and zoom it. The sizes are not stored on the server,
    // so they are read from the loaded images. A slide shows a spinner until the size of its image is known.
    var sizeRequests = {};
    var sizes = {};
    var pswp = null;

    var items = $links.map(function () {
        var $link = $(this);
        return {
            src: $link.attr('href'),
            alt: $link.find('img').attr('alt') || '',
            caption: $link.attr('title') || ''
        };
    }).get();

    function loadSize(index) {
        var src = items[index].src;
        if (!sizeRequests[src]) {
            var deferred = $.Deferred();
            sizeRequests[src] = deferred.promise();
            $('<img>').on('load', function () {
                sizes[src] = {width: this.naturalWidth, height: this.naturalHeight};
                deferred.resolve();
            }).on('error', function () {
                // any size, PhotoSwipe tries to load the image again and shows its error message
                sizes[src] = {width: 800, height: 600};
                deferred.resolve();
            }).attr('src', src);

            deferred.done(function () {
                // replaces the spinner if the slide is already shown
                if (pswp) {
                    pswp.refreshSlideContent(index);
                }
            });
        }
        return sizeRequests[src];
    }

    function loadSizesAround(index) {
        for (var offset = -1; offset <= 2; offset++) {
            loadSize((index + offset + items.length) % items.length);
        }
    }

    function slideData(index) {
        var item = items[index];
        var size = sizes[item.src];
        if (!size) {
            return {html: LOADING_HTML};
        }
        return {src: item.src, width: size.width, height: size.height, alt: item.alt};
    }

    function registerCaption(instance) {
        instance.ui.registerElement({
            name: 'galleryCaption',
            className: 'gallery-caption',
            order: 9,
            isButton: false,
            appendTo: 'root',
            onInit: function (element, gallery) {
                var $caption = $(element);
                gallery.on('change', function () {
                    var caption = items[gallery.currIndex].caption;
                    $caption.text(caption).toggle(caption.length > 0);
                });
            }
        });
    }

    function openGallery(index) {
        var instance = new PhotoSwipe({
            dataSource: items,
            index: index,
            // the thumbnails are cropped to a square, zooming out of them would look distorted
            showHideAnimationType: 'fade',
            bgOpacity: 0.9
        });
        instance.addFilter('itemData', function (itemData, itemIndex) {
            return slideData(itemIndex);
        });
        instance.on('uiRegister', function () {
            registerCaption(instance);
        });
        instance.on('change', function () {
            loadSizesAround(instance.currIndex);
        });
        instance.on('destroy', function () {
            pswp = null;
        });
        pswp = instance;
        instance.init();
    }

    $('#links').on('click', 'a', function (event) {
        event.preventDefault();
        var $link = $(this);
        if (pswp || $link.hasClass('gallery-link-loading')) {
            return;
        }

        // load the clicked image first, so the gallery opens with it right away
        var index = $links.index(this);
        $link.addClass('gallery-link-loading');
        loadSize(index).done(function () {
            $link.removeClass('gallery-link-loading');
            openGallery(index);
        });
    });
});
