package de.fred4jupiter.fredbet.domain;

public class Statistic {

	private Integer pointsGroup = Integer.valueOf(0);

	private Integer pointsRoundOfSixteen = Integer.valueOf(0);

	private Integer pointsQuarterFinal = Integer.valueOf(0);

	private Integer pointsSemiFinal = Integer.valueOf(0);

	private Integer pointsFinal = Integer.valueOf(0);

	private Integer pointsFavoriteCountry = Integer.valueOf(0);

	private boolean minPointsCandidate;

	private boolean maxPointsCandidate;

	private boolean maxFavoriteCountryCandidate;

	private boolean maxGroupPointsCandidate;

	private Country favoriteCountry;

	private final String username;

	public Statistic(String username) {
		this.username = username;
	}

	public Integer getPointsGroup() {
		return pointsGroup;
	}

	public void setPointsGroup(Integer pointsGroup) {
		this.pointsGroup = pointsGroup != null ? pointsGroup : Integer.valueOf(0);
	}

	public Integer getPointsRoundOfSixteen() {
		return pointsRoundOfSixteen;
	}

	public void setPointsRoundOfSixteen(Integer pointsRoundOfSixteen) {
		this.pointsRoundOfSixteen = pointsRoundOfSixteen != null ? pointsRoundOfSixteen : Integer.valueOf(0);
	}

	public Integer getPointsQuarterFinal() {
		return pointsQuarterFinal;
	}

	public void setPointsQuarterFinal(Integer pointsQuarterFinal) {
		this.pointsQuarterFinal = pointsQuarterFinal != null ? pointsQuarterFinal : Integer.valueOf(0);
	}

	public Integer getPointsSemiFinal() {
		return pointsSemiFinal;
	}

	public void setPointsSemiFinal(Integer pointsSemiFinal) {
		this.pointsSemiFinal = pointsSemiFinal != null ? pointsSemiFinal : Integer.valueOf(0);
	}

	public Integer getPointsFinal() {
		return pointsFinal;
	}

	public void setPointsFinal(Integer pointsFinal) {
		this.pointsFinal = pointsFinal != null ? pointsFinal : Integer.valueOf(0);
	}

	public String getUsername() {
		return username;
	}

	public Integer getSum() {
		return pointsGroup.intValue() + pointsRoundOfSixteen.intValue() + pointsQuarterFinal.intValue() + pointsSemiFinal.intValue()
				+ pointsFinal.intValue();
	}

	public Integer getSumKOMatches() {
		return pointsRoundOfSixteen.intValue() + pointsQuarterFinal.intValue() + pointsSemiFinal.intValue() + pointsFinal.intValue();
	}

	public Integer getPointsFavoriteCountry() {
		return pointsFavoriteCountry;
	}

	public void setPointsFavoriteCountry(Integer pointsFavoriteCountry) {
		this.pointsFavoriteCountry = pointsFavoriteCountry != null ? pointsFavoriteCountry : Integer.valueOf(0);
	}

	public Country getFavoriteCountry() {
		return favoriteCountry;
	}

	public void setFavoriteCountry(Country favoriteCountry) {
		this.favoriteCountry = favoriteCountry;
	}

	public boolean isMaxGroupPointsCandidate() {
		return maxGroupPointsCandidate;
	}

	public void setMaxGroupPointsCandidate(boolean maxGroupPointsCandidate) {
		this.maxGroupPointsCandidate = maxGroupPointsCandidate;
	}

	public boolean isMaxFavoriteCountryCandidate() {
		return maxFavoriteCountryCandidate;
	}

	public void setMaxFavoriteCountryCandidate(boolean maxFavoriteCountryCandidate) {
		this.maxFavoriteCountryCandidate = maxFavoriteCountryCandidate;
	}

	public boolean isMinPointsCandidate() {
		return minPointsCandidate;
	}

	public void setMinPointsCandidate(boolean minPointsCandidate) {
		this.minPointsCandidate = minPointsCandidate;
	}

	public boolean isMaxPointsCandidate() {
		return maxPointsCandidate;
	}

	public void setMaxPointsCandidate(boolean maxPointsCandidate) {
		this.maxPointsCandidate = maxPointsCandidate;
	}
}
