$(function () {
    // team list: filter by team name
    $('#teamFilter').on('input', function () {
        const search = $(this).val().trim().toLowerCase();
        $('.team-entry').each(function () {
            const teamName = ($(this).attr('data-team-name') || '').toLowerCase();
            $(this).toggleClass('d-none', search.length > 0 && !teamName.includes(search));
        });
    });

    // team form: toggle between team of a team bundle and team name
    $('input[name="useTeamBundleEntry"]').on('change', function () {
        const useTeamBundleEntry = $(this).val() === 'true';
        $('#teamBundleEntryGroup').toggleClass('d-none', !useTeamBundleEntry);
        $('#nameGroup').toggleClass('d-none', useTeamBundleEntry);
    });

    // team form: preview of the selected crest
    const $crestPreview = $('#crestPreview');
    $('#crestFile').on('change', function () {
        const file = this.files[0];
        if ($crestPreview.length && file && (file.type === 'image/svg+xml' || file.name.toLowerCase().endsWith('.svg'))) {
            $crestPreview.attr('src', URL.createObjectURL(file));
        }
    });
});
