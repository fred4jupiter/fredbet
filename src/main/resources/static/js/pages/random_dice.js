function runRandomDice() {
	$('#goalsTeamOne').val(randomNumber());
	$('#goalsTeamTwo').val(randomNumber());
}

function randomNumber() {
	return randomIntFromInterval(1,4);
}

function randomIntFromInterval(min, max) { // min and max included
	return Math.floor(Math.random() * (max - min + 1) + min);
}