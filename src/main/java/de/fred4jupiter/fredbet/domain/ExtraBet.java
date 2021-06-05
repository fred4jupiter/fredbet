package de.fred4jupiter.fredbet.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

@Entity
@Table(name = "EXTRA_BET")
public class ExtraBet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXTRA_BET_ID")
    private Long id;

    @Column(name = "USER_NAME")
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "FINAL_WINNER")
    private Country finalWinner;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEMI_FINAL_WINNER")
    private Country semiFinalWinner;

    @Enumerated(EnumType.STRING)
    @Column(name = "THIRD_FINAL_WINNER")
    private Country thirdFinalWinner;

    @Column(name = "POINTS_ONE")
    private Integer pointsOne = 0;

    @Column(name = "POINTS_TWO")
    private Integer pointsTwo = 0;

    @Column(name = "POINTS_THREE")
    private Integer pointsThree = 0;

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        ExtraBet extraBet = (ExtraBet) obj;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(id, extraBet.id);
        builder.append(userName, extraBet.userName);
        builder.append(finalWinner, extraBet.finalWinner);
        builder.append(semiFinalWinner, extraBet.semiFinalWinner);
        builder.append(thirdFinalWinner, extraBet.thirdFinalWinner);
        builder.append(pointsOne, extraBet.pointsOne);
        builder.append(pointsTwo, extraBet.pointsTwo);
        builder.append(pointsThree, extraBet.pointsThree);

        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(id);
        builder.append(userName);
        builder.append(finalWinner);
        builder.append(semiFinalWinner);
        builder.append(thirdFinalWinner);
        builder.append(pointsOne);
        builder.append(pointsTwo);
        builder.append(pointsThree);
        return builder.toHashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
        builder.append("id", id);
        builder.append("userName", userName);
        builder.append("finalWinner", finalWinner);
        builder.append("semiFinalWinner", semiFinalWinner);
        builder.append("thirdFinalWinner", thirdFinalWinner);
        builder.append("pointsOne", pointsOne);
        builder.append("pointsTwo", pointsTwo);
        builder.append("pointsThree", pointsThree);
        return builder.toString();
    }

    public Country getFinalWinner() {
        return finalWinner;
    }

    public void setFinalWinner(Country finalWinner) {
        this.finalWinner = finalWinner;
    }

    public Country getSemiFinalWinner() {
        return semiFinalWinner;
    }

    public void setSemiFinalWinner(Country semiFinalWinner) {
        this.semiFinalWinner = semiFinalWinner;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Country getThirdFinalWinner() {
        return thirdFinalWinner;
    }

    public void setThirdFinalWinner(Country thirdFinalWinner) {
        this.thirdFinalWinner = thirdFinalWinner;
    }

    public Integer getPointsOne() {
        return pointsOne;
    }

    public void setPointsOne(Integer pointsOne) {
        this.pointsOne = pointsOne;
    }

    public Integer getPointsTwo() {
        return pointsTwo;
    }

    public void setPointsTwo(Integer pointsTwo) {
        this.pointsTwo = pointsTwo;
    }

    public Integer getPointsThree() {
        return pointsThree;
    }

    public void setPointsThree(Integer pointsThree) {
        this.pointsThree = pointsThree;
    }

    @Transient
    public Integer getPoints() {
        return pointsOne + pointsTwo + pointsThree;
    }

    public boolean noExtraBetsSet() {
        return !isFinalWinnerSet() || !isSemiFinalWinnerSet();
    }

    public boolean isFinalWinnerSet() {
        return this.finalWinner != null && !Country.NONE.equals(this.finalWinner);
    }

    public boolean isSemiFinalWinnerSet() {
        return this.semiFinalWinner != null && !Country.NONE.equals(this.semiFinalWinner);
    }

    public boolean isThirdFinalWinnerSet() {
        return this.thirdFinalWinner != null && !Country.NONE.equals(this.thirdFinalWinner);
    }
}
