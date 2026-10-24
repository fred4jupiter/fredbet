$(function() {
	$('#editImageGroup').on('click', function() {
		var $selected = $('#groupSelection option:selected');
		$('#imageGroupId').val($selected.val());
		$('#imageGroupName').val($selected.text());
	});
});
