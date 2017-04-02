$(document).ready(function() {
	checkPenalty();
	$('.my-image').croppie();	 
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

function oneCountUp() {
	var val = $('#goalsTeamOne, #teamResultOne').val();
	$('#goalsTeamOne, #teamResultOne').val((val*1)+1); 
	checkPenalty();
}

function oneCountDown() {
	var val = $('#goalsTeamOne, #teamResultOne').val();
	if (val > 0) {
		$('#goalsTeamOne, #teamResultOne').val((val*1)-1);
		checkPenalty();
	}
}

function twoCountUp() {
	var val = $('#goalsTeamTwo, #teamResultTwo').val();
	$('#goalsTeamTwo, #teamResultTwo').val((val*1)+1);
	checkPenalty();
}

function twoCountDown() {
	var val = $('#goalsTeamTwo, #teamResultTwo').val();
	if (val > 0) {
		$('#goalsTeamTwo, #teamResultTwo').val((val*1)-1); 
		checkPenalty();
	}
}