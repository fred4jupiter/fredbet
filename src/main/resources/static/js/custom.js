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

function oneVoteUp() {
	var val = $('#goalsTeamOne').val();
	$('#goalsTeamOne').val((val*1)+1); 
	checkPenalty();
}

function oneVoteDown() {
	var val = $('#goalsTeamOne').val();
	if (val > 0) {
		$('#goalsTeamOne').val((val*1)-1);
		checkPenalty();
	}
}

function twoVoteUp() {
	var val = $('#goalsTeamTwo').val();
	$('#goalsTeamTwo').val((val*1)+1);
	checkPenalty();
}

function twoVoteDown() {
	var val = $('#goalsTeamTwo').val();
	if (val > 0) {
		$('#goalsTeamTwo').val((val*1)-1); 
		checkPenalty();
	}
}