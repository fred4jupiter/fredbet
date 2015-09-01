$('.form_datetime').datetimepicker({
	language : 'de',
	weekStart : 1,
	todayBtn : 1,
	autoclose : 1,
	todayHighlight : 1,
	startView : 2,
	forceParse : 0,
	showMeridian : 1,
	startDate: '-3d',
	endDate: '+1y'
});
$('.form_date').datetimepicker({
	language : 'de',
	weekStart : 1,
	todayBtn : 1,
	autoclose : 1,
	todayHighlight : 1,
	startView : 2,
	minView : 2,
	forceParse : 0
});
$('.form_time').datetimepicker({
	language : 'de',
	weekStart : 1,
	todayBtn : 1,
	autoclose : 1,
	todayHighlight : 1,
	startView : 1,
	minView : 0,
	maxView : 1,
	forceParse : 0
});