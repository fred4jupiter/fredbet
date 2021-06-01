function runRandomDice() {
	$('#goalsTeamOne').val(randomNumber());
	$('#goalsTeamTwo').val(randomNumber());
	checkPenalty();
}

function randomNumber() {
	return randomIntFromInterval(0,5);
}

function randomIntFromInterval(min, max) { // min and max included
	return Math.floor(Math.random() * (max - min + 1) + min);
}