$(function() {
	$('#match-info').on('show.bs.modal', function(event) {
		const data = $(event.relatedTarget).data();
		$(this).find('[data-field]').each(function() {
			$(this).text(data[$(this).data('field')] || '-');
		});
	});
});
