function editImageGroup() {
	$("#imageGroupId").val($("#groupSelection").val());
	$("#imageGroupName").val($("#groupSelection option:selected" ).text());
}