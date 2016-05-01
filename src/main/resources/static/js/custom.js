$(document).ready(function() {
	checkPenalty();
});

function checkPenalty() {
	var goalsOne = $('#goalsTeamOne').val();
	var goalsTwo = $('#goalsTeamTwo').val();
	if (goalsOne != "" && goalsTwo != "" && goalsOne == goalsTwo) {
		$('#penaltyBox').show();
	} else {
		$('#penaltyBox').hide();
	}
}