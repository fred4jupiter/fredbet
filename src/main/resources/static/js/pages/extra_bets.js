"use strict";

$(function () {
    const $selects = $('.extra-bets-select');

    // a team can only be picked once: the teams selected in the other places are disabled
    function disableTeamsOfOtherPlaces() {
        $selects.each(function () {
            const $select = $(this);
            const otherValues = $selects.not($select).map(function () {
                return $(this).val();
            }).get().filter(value => value);
            $select.find('option').each(function () {
                const $option = $(this);
                $option.prop('disabled', $option.val() !== '' && otherValues.indexOf($option.val()) !== -1);
            });
        });
    }

    function updateCrest($select) {
        const crest = $select.find('option:selected').data('crest');
        const $crest = $('#' + $select.attr('id') + 'Crest');
        if (crest) {
            $crest.attr('src', crest).removeClass('invisible');
        } else {
            $crest.attr('src', 'data:,').addClass('invisible');
        }
    }

    $selects.on('change', function () {
        updateCrest($(this));
        disableTeamsOfOtherPlaces();
    });

    disableTeamsOfOtherPlaces();
});
