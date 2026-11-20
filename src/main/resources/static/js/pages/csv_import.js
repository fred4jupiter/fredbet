"use strict";

$(function () {
    const $mappings = $('.csv-column-mapping');

    // a field can only be mapped to one column: reset other columns that use the same field
    $mappings.on('change', function () {
        const $changed = $(this);
        const field = $changed.val();
        if (!field) {
            return;
        }

        $mappings.not($changed).each(function () {
            if ($(this).val() === field) {
                $(this).val('');
            }
        });
    });
});
