package de.fred4jupiter.fredbet.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;

@RunWith(MockitoJUnitRunner.class)
public class ExtraPointsCalculationServiceUT {

	@InjectMocks
	private ExtraPointsCalculationService extraPointsCalculationService;

	@Mock
	private ExtraBetRepository extraBetRepository;

	@Mock
	private RuntimeConfigurationService runtimeConfigurationService;

	@Mock
	private MatchGoalsChangedEvent matchGoalsChangedEvent;

	@Mock
	private RuntimeConfig runtimeConfig;

	private Match match;

	private ExtraBet extraBet;

	@Before
	public void setup() {
		when(runtimeConfig.getPointsFinalWinner()).thenReturn(10);
		when(runtimeConfig.getPointsSemiFinalWinner()).thenReturn(5);
		when(runtimeConfig.getPointsThirdFinalWinner()).thenReturn(2);

		match = new Match();

		extraBet = new ExtraBet();
		extraBet.setUserName("Alfredo");
		extraBet.setFinalWinner(Country.GERMANY);
		extraBet.setSemiFinalWinner(Country.FRANCE);
		extraBet.setThirdFinalWinner(Country.SPAIN);
	}

	@Test
	public void noPointsOnOtherMatchThanFinalOrGameOfThird() {
		when(matchGoalsChangedEvent.getMatch()).thenReturn(match);

		match.setGroup(Group.GROUP_B);

		extraPointsCalculationService.onApplicationEvent(matchGoalsChangedEvent);

		assertThat(extraBet.getPointsOne(), equalTo(0));
		assertThat(extraBet.getPointsTwo(), equalTo(0));
		assertThat(extraBet.getPointsThree(), equalTo(0));
		assertThat(extraBet.getPoints(), equalTo(0));
	}

	@Test
	public void finalMatchButWrongBet() {
		when(matchGoalsChangedEvent.getMatch()).thenReturn(match);

		match.setGroup(Group.FINAL);
		match.setCountryOne(Country.AFGHANISTAN);
		match.setCountryTwo(Country.CUBA);

		when(runtimeConfigurationService.loadRuntimeConfig()).thenReturn(runtimeConfig);
		when(extraBetRepository.findAll()).thenReturn(Arrays.asList(extraBet));

		extraPointsCalculationService.onApplicationEvent(matchGoalsChangedEvent);

		assertThat(extraBet.getPointsOne(), equalTo(0));
		assertThat(extraBet.getPointsTwo(), equalTo(0));
		assertThat(extraBet.getPointsThree(), equalTo(0));
		assertThat(extraBet.getPoints(), equalTo(0));
	}

	@Test
	public void finalAndSemiFinalCorrect() {
		when(matchGoalsChangedEvent.getMatch()).thenReturn(match);
		
		match.setGroup(Group.FINAL);
		match.setCountryOne(Country.GERMANY);
		match.setCountryTwo(Country.FRANCE);
		match.setGoalsTeamOne(2);
		match.setGoalsTeamTwo(1);		

		when(runtimeConfigurationService.loadRuntimeConfig()).thenReturn(runtimeConfig);
		when(extraBetRepository.findAll()).thenReturn(Arrays.asList(extraBet));

		extraPointsCalculationService.onApplicationEvent(matchGoalsChangedEvent);

		assertThat(extraBet.getPointsOne(), equalTo(10));
		assertThat(extraBet.getPointsTwo(), equalTo(5));
		assertThat(extraBet.getPointsThree(), equalTo(0));
		assertThat(extraBet.getPoints(), equalTo(15));
		
		verify(extraBetRepository).save(extraBet);
	}
	
	@Test
	public void gameOfThirdCorrect() {
		when(matchGoalsChangedEvent.getMatch()).thenReturn(match);
		
		extraBet.setThirdFinalWinner(Country.GERMANY);
		
		match.setGroup(Group.GAME_FOR_THIRD);
		match.setCountryOne(Country.GERMANY);
		match.setCountryTwo(Country.FRANCE);
		match.setGoalsTeamOne(2);
		match.setGoalsTeamTwo(1);		

		when(runtimeConfigurationService.loadRuntimeConfig()).thenReturn(runtimeConfig);
		when(extraBetRepository.findAll()).thenReturn(Arrays.asList(extraBet));

		extraPointsCalculationService.onApplicationEvent(matchGoalsChangedEvent);

		assertThat(extraBet.getPointsOne(), equalTo(0));
		assertThat(extraBet.getPointsTwo(), equalTo(0));
		assertThat(extraBet.getPointsThree(), equalTo(2));
		assertThat(extraBet.getPoints(), equalTo(2));
		
		verify(extraBetRepository).save(extraBet);
	}
}
