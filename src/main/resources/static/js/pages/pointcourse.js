"use strict";

$(function () {
    var $canvas = $('#pointCourseChart');
    if (!$canvas.length) {
        return;
    }

    // the rotated match labels on the x axis would take most of the chart height on narrow screens.
    // the match label is still shown in the tooltip when tapping a point.
    var MATCH_LABELS_MIN_WIDTH = 576;

    // jQuery parses the JSON of the data-chart attribute into an object
    var chartData = $canvas.data('chart');

    new Chart($canvas[0].getContext('2d'), {
        type: 'line',
        data: chartData,
        options: {
            responsive: true,
            maintainAspectRatio: false,
            legend: {
                position: 'bottom',
                labels: {
                    boxWidth: 12
                }
            },
            scales: {
                xAxes: [{
                    ticks: {
                        display: $canvas.parent().width() >= MATCH_LABELS_MIN_WIDTH
                    }
                }]
            },
            plugins: {
                colorschemes: {
                    scheme: 'brewer.Paired7'
                }
            },
            onResize: function (chart, size) {
                var ticks = chart.options.scales.xAxes[0].ticks;
                var show = size.width >= MATCH_LABELS_MIN_WIDTH;
                if (ticks.display !== show) {
                    ticks.display = show;
                    chart.update();
                }
            }
        }
    });
});
