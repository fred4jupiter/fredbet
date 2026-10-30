var GOAL_FIELDS = {
	one: '#goalsTeamOne, #teamResultOne',
	two: '#goalsTeamTwo, #teamResultTwo'
};

$(function() {
	$(GOAL_FIELDS.one + ', ' + GOAL_FIELDS.two).on('change', checkPenalty);
	$(GOAL_FIELDS.one + ', ' + GOAL_FIELDS.two).on('focus', function() {
		$(this).select();
	});

	$('.goal-counter').on('click', function() {
		var $field = $(GOAL_FIELDS[$(this).data('team')]);
		var step = $(this).data('step');
		var val = $field.val() === "" ? -1 : $field.val() * 1;
		if (step > 0 || val > 0) {
			$field.val(val + step);
			checkPenalty();
		}
	});

	checkPenalty();
});

function checkPenalty() {
	var goalsOne = $(GOAL_FIELDS.one).val();
	var goalsTwo = $(GOAL_FIELDS.two).val();
	$('#penaltyBox').toggle(goalsOne != "" && goalsTwo != "" && goalsOne == goalsTwo);
}
