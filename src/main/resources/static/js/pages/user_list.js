"use strict";

$(function () {
    const $entries = $('#userList > [data-username]');

    $('#userFilter').on('input', function () {
        const filter = $(this).val().trim().toLowerCase();
        let visible = 0;
        $entries.each(function () {
            const matches = $(this).data('username').toString().indexOf(filter) !== -1;
            $(this).toggleClass('d-none', !matches);
            if (matches) {
                visible++;
            }
        });
        $('#noUserMatch').toggleClass('d-none', visible > 0);
    });
});
