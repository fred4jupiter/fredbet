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

    initResizableColumns($('#csvPreviewTable'));
});

/**
 * Makes the columns of the preview table resizable: drag the handle on the right edge of a column header, use the
 * arrow keys on the focused handle or double click it to restore the initial width.
 */
function initResizableColumns($table) {
    const MIN_WIDTH = 80;
    const MIN_INITIAL_WIDTH = 170;
    const MAX_INITIAL_WIDTH = 280;
    const KEY_STEP = 10;
    const KEY_STEP_LARGE = 50;

    if (!$table.length) {
        return;
    }

    const $cols = $table.find('colgroup col');
    const $resizers = $table.find('.csv-column-resizer');
    const initialWidths = [];

    // start with the natural width of each column, limited so that long values (e.g. SVG content) do not take all the space
    $table.find('thead th').each(function () {
        const naturalWidth = Math.ceil($(this).outerWidth());
        initialWidths.push(Math.min(Math.max(naturalWidth, MIN_INITIAL_WIDTH), MAX_INITIAL_WIDTH));
    });

    // the current widths are kept here, because the computed width of a <col> is the rendered width and not the set width
    const widths = initialWidths.slice();
    $.each(widths, function (index, width) {
        applyColumnWidth(index, width);
    });
    updateTableWidth();
    $table.addClass('csv-columns-fixed');

    function setColumnWidth(index, width) {
        widths[index] = Math.max(MIN_WIDTH, Math.round(width));
        applyColumnWidth(index, widths[index]);
        updateTableWidth();
    }

    function applyColumnWidth(index, width) {
        $cols.eq(index).css('width', width + 'px');
        $resizers.eq(index).attr({'aria-valuenow': width, 'aria-valuemin': MIN_WIDTH});
    }

    function columnWidth(index) {
        return widths[index];
    }

    // with a fixed table layout the table needs an explicit width, the surrounding .table-responsive scrolls horizontally
    function updateTableWidth() {
        let total = 0;
        $.each(widths, function (index, width) {
            total += width;
        });
        $table.css('width', total + 'px');
    }

    $resizers.on('pointerdown', function (event) {
        if (event.button !== 0) {
            return;
        }
        event.preventDefault();

        const $resizer = $(this);
        const index = $resizers.index($resizer);
        const startX = event.pageX;
        const startWidth = columnWidth(index);

        $resizer.addClass('active').trigger('focus');
        $('body').addClass('csv-column-resizing');

        $(document)
            .on('pointermove.csvColumnResize', function (moveEvent) {
                setColumnWidth(index, startWidth + moveEvent.pageX - startX);
            })
            .on('pointerup.csvColumnResize pointercancel.csvColumnResize', function () {
                $(document).off('.csvColumnResize');
                $resizer.removeClass('active');
                $('body').removeClass('csv-column-resizing');
            });
    });

    $resizers.on('keydown', function (event) {
        const index = $resizers.index(this);
        const step = event.shiftKey ? KEY_STEP_LARGE : KEY_STEP;
        if (event.key === 'ArrowLeft') {
            setColumnWidth(index, columnWidth(index) - step);
        } else if (event.key === 'ArrowRight') {
            setColumnWidth(index, columnWidth(index) + step);
        } else if (event.key === 'Home') {
            setColumnWidth(index, initialWidths[index]);
        } else {
            return;
        }
        event.preventDefault();
    });

    $resizers.on('dblclick', function () {
        const index = $resizers.index(this);
        setColumnWidth(index, initialWidths[index]);
    });

    // a click on the handle must not open the select of the column header
    $resizers.on('click', function (event) {
        event.preventDefault();
        event.stopPropagation();
    });
}
