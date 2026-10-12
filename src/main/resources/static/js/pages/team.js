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

    // team form: toggle between country and team name
    const countryGroup = document.getElementById('countryGroup');
    const nameGroup = document.getElementById('nameGroup');
    document.querySelectorAll('input[name="useCountry"]').forEach(function (radio) {
        radio.addEventListener('change', function () {
            const useCountry = radio.value === 'true';
            countryGroup.classList.toggle('d-none', !useCountry);
            nameGroup.classList.toggle('d-none', useCountry);
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
