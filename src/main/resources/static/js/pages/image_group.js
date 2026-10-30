$(function() {
	$('#confirm-delete-group').on('show.bs.modal', function(event) {
		var $button = $(event.relatedTarget);
		$(this).find('input[name="id"]').val($button.data('id'));
		$(this).find('[data-group-name]').text($button.data('name'));
	});
});
