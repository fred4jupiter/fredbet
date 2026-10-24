$(function() {
	$('#randomDice').on('click', function() {
		$('#goalsTeamOne').val(randomIntFromInterval(0, 5));
		$('#goalsTeamTwo').val(randomIntFromInterval(0, 5));
		checkPenalty();
	});
});

function randomIntFromInterval(min, max) { // min and max included
	return Math.floor(Math.random() * (max - min + 1) + min);
}
