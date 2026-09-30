$(document).ready(function () {
    showHideActive();
});

function showHideActive() {
    if ($('#enabled').is(":checked")) {
        $('#fd-setup').show();
    } else {
        $('#fd-setup').hide();
    }
}
