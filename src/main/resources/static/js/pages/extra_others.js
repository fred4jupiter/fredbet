$(function() {
    // devices without hover (phones, tablets) open the tooltip by tapping, e.g. to see the team name of a crest
    var touchDevice = window.matchMedia('(hover: none)').matches;
    var $tooltips = $('.extra-tooltip');

    $tooltips.tooltip({ trigger: touchDevice ? 'click' : 'hover focus' });

    if (touchDevice) {
        // only one tooltip at a time
        $tooltips.on('show.bs.tooltip', function() {
            $tooltips.not(this).tooltip('hide');
        });

        // tapping anywhere else closes the open tooltip
        $(document).on('click', function(event) {
            if ($(event.target).closest('.extra-tooltip').length === 0) {
                $tooltips.tooltip('hide');
            }
        });
    }
});
