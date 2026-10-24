$(function () {
    $('#enabled').on('change', showHideActive);
    showHideActive();
});

function showHideActive() {
    $('#fd-setup').toggle($('#enabled').is(':checked'));
}
