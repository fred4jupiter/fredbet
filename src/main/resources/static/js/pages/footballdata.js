function showHideActive() {
    var enabled = $('#enabled').val();
    if (enabled === "true") {
        $('#fd-setup').show();
    } else {
        $('#fd-setup').hide();
    }
}
