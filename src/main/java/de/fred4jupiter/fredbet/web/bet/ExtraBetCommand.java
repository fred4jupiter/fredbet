package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.web.MessageUtil;

public class ExtraBetCommand {

	private Long extraBetId;

	private final MessageUtil messageUtil;
	private Country finalWinner;
	private Country semiFinalWinner;

	public ExtraBetCommand(MessageUtil messageUtil) {
		this.messageUtil = messageUtil;
	}

	public ExtraBetCommand(MessageUtil messageUtil, Long extraBetId, Country finalWinner, Country semiFinalWinner) {
		this.messageUtil = messageUtil;
		this.extraBetId = extraBetId;
		this.finalWinner = finalWinner;
		this.semiFinalWinner = semiFinalWinner;
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

	public Long getExtraBetId() {
		return extraBetId;
	}

	public void setExtraBetId(Long extraBetId) {
		this.extraBetId = extraBetId;
	}
}
