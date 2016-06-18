$(document).ready(function() {
	checkPenalty();
});

function checkPenalty() {
	var goalsOne = $('#goalsTeamOne, #teamResultOne').val();
	var goalsTwo = $('#goalsTeamTwo, #teamResultTwo').val();
	if (goalsOne != "" && goalsTwo != "" && goalsOne == goalsTwo) {
		$('#penaltyBox').show();
	} else {
		$('#penaltyBox').hide();
	}
}

function clearTeamNameFields() {
	$('#nameTeamOne').val("");
	$('#nameTeamTwo').val("");
}