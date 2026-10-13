document.addEventListener('DOMContentLoaded', function () {
    // team list: filter by team name
    const teamFilter = document.getElementById('teamFilter');
    if (teamFilter) {
        teamFilter.addEventListener('input', function () {
            const search = teamFilter.value.trim().toLowerCase();
            document.querySelectorAll('.team-entry').forEach(function (entry) {
                const teamName = (entry.dataset.teamName || '').toLowerCase();
                entry.classList.toggle('d-none', search.length > 0 && !teamName.includes(search));
            });
        });
    }

    // team form: toggle between team of a team bundle and team name
    const teamBundleEntryGroup = document.getElementById('teamBundleEntryGroup');
    const nameGroup = document.getElementById('nameGroup');
    document.querySelectorAll('input[name="useTeamBundleEntry"]').forEach(function (radio) {
        radio.addEventListener('change', function () {
            const useTeamBundleEntry = radio.value === 'true';
            teamBundleEntryGroup.classList.toggle('d-none', !useTeamBundleEntry);
            nameGroup.classList.toggle('d-none', useTeamBundleEntry);
        });
    });

    // team form: preview of the selected crest
    const crestFile = document.getElementById('crestFile');
    const crestPreview = document.getElementById('crestPreview');
    if (crestFile && crestPreview) {
        crestFile.addEventListener('change', function () {
            const file = crestFile.files[0];
            if (file && (file.type === 'image/svg+xml' || file.name.toLowerCase().endsWith('.svg'))) {
                crestPreview.src = URL.createObjectURL(file);
            }
        });
    }
});
