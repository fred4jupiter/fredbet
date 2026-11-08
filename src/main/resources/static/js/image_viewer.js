/**
 * Image viewer based on PhotoSwipe, loaded on every page by the layout.
 *
 * Links with a data-image-viewer attribute open their image (href) in the viewer, the title of the link is shown as caption.
 * The gallery page opens all of its images with openImageViewer(items, index, $link).
 */
var openImageViewer = (function () {
    "use strict";

    var LOADING_HTML = '<div class="image-viewer-loading"><div class="spinner-border text-light" role="status"></div></div>';

    // PhotoSwipe needs the size of an image to fit and zoom it. The sizes are not stored on the server,
    // so they are read from the loaded images. A slide shows a spinner until the size of its image is known.
    var sizeRequests = {};
    var sizes = {};
    var pswp = null;

    function loadSize(src) {
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
        }
        return sizeRequests[src];
    }

    function slideData(item) {
        var size = sizes[item.src];
        if (!size) {
            return {html: LOADING_HTML};
        }
        return {src: item.src, width: size.width, height: size.height, alt: item.alt};
    }

    function registerCaption(instance, items) {
        instance.ui.registerElement({
            name: 'imageViewerCaption',
            className: 'image-viewer-caption',
            order: 9,
            isButton: false,
            appendTo: 'root',
            onInit: function (element, viewer) {
                var $caption = $(element);
                viewer.on('change', function () {
                    var caption = items[viewer.currIndex].caption || '';
                    $caption.text(caption).toggle(caption.length > 0);
                });
            }
        });
    }

    function loadSizesAround(instance, items) {
        // the previous and the next two images, the same range PhotoSwipe preloads
        for (var offset = -1; offset <= 2; offset++) {
            var index = (instance.currIndex + offset + items.length) % items.length;
            var request = loadSize(items[index].src);
            if (request.state() === 'pending') {
                // the slide shows a spinner until then
                request.done(refreshSlide(instance, index));
            }
        }
    }

    function refreshSlide(instance, index) {
        return function () {
            // replaces the spinner if the viewer is still open
            if (pswp === instance) {
                instance.refreshSlideContent(index);
            }
        };
    }

    function open(items, index) {
        // button titles and error message in the language of the user, rendered by the layout as data attributes
        var texts = $('#imageViewerTexts').data() || {};
        var instance = new PhotoSwipe($.extend({}, texts, {
            dataSource: items,
            index: index,
            // the thumbnails are cropped to a square, zooming out of them would look distorted
            showHideAnimationType: 'fade',
            bgOpacity: 0.9
        }));
        instance.addFilter('itemData', function (itemData, itemIndex) {
            return slideData(items[itemIndex]);
        });
        instance.on('uiRegister', function () {
            registerCaption(instance, items);
        });
        instance.on('change', function () {
            loadSizesAround(instance, items);
        });
        instance.on('destroy', function () {
            pswp = null;
        });
        pswp = instance;
        instance.init();
    }

    /**
     * @param items array of {src, alt, caption}
     * @param index index of the image to show first
     * @param $trigger the clicked link, it is marked while the first image is loaded
     */
    function openImageViewer(items, index, $trigger) {
        if (pswp || $trigger.hasClass('image-viewer-trigger-loading')) {
            return;
        }

        // load the first image before opening, so the viewer starts with it right away
        $trigger.addClass('image-viewer-trigger-loading');
        loadSize(items[index].src).done(function () {
            $trigger.removeClass('image-viewer-trigger-loading');
            open(items, index);
        });
    }

    $(function () {
        $(document).on('click', 'a[data-image-viewer]', function (event) {
            event.preventDefault();
            var $link = $(this);
            openImageViewer([{
                src: $link.attr('href'),
                alt: $link.find('img').attr('alt') || '',
                caption: $link.attr('title') || ''
            }], 0, $link);
        });
    });

    return openImageViewer;
})();
