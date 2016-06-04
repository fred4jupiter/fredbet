package de.fred4jupiter.fredbet.domain;

public class Statistic {

	private Integer pointsGroup = Integer.valueOf(0);

	private Integer pointsRoundOfSixteen = Integer.valueOf(0);

	private Integer pointsQuarterFinal = Integer.valueOf(0);

	private Integer pointsSemiFinal = Integer.valueOf(0);

	private Integer pointsFinal = Integer.valueOf(0);

	private final String username;

	public Statistic(String username) {
		this.username = username;
	}

	public Integer getPointsGroup() {
		return pointsGroup;
	}

	public void setPointsGroup(Integer pointsGroup) {
		this.pointsGroup = pointsGroup;
	}

	public Integer getPointsRoundOfSixteen() {
		return pointsRoundOfSixteen;
	}

	public void setPointsRoundOfSixteen(Integer pointsRoundOfSixteen) {
		this.pointsRoundOfSixteen = pointsRoundOfSixteen;
	}

	public Integer getPointsQuarterFinal() {
		return pointsQuarterFinal;
	}

	public void setPointsQuarterFinal(Integer pointsQuarterFinal) {
		this.pointsQuarterFinal = pointsQuarterFinal;
	}

	public Integer getPointsSemiFinal() {
		return pointsSemiFinal;
	}

	public void setPointsSemiFinal(Integer pointsSemiFinal) {
		this.pointsSemiFinal = pointsSemiFinal;
	}

	public Integer getPointsFinal() {
		return pointsFinal;
	}

	public void setPointsFinal(Integer pointsFinal) {
		this.pointsFinal = pointsFinal;
	}

	public String getUsername() {
		return username;
	}

	public Integer getSum() {
		return pointsGroup.intValue() + pointsRoundOfSixteen.intValue() + pointsQuarterFinal.intValue() + pointsSemiFinal.intValue()
				+ pointsFinal.intValue();
	}
}
