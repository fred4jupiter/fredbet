package de.fred4jupiter.fredbet.betting;

import de.fred4jupiter.fredbet.betting.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.data.RandomValueGenerator;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ExtraBettingService {

    private final ExtraBetRepository extraBetRepository;

    private final MatchService matchService;

    private final FredbetProperties fredbetProperties;

    private final RandomValueGenerator randomValueGenerator;

    public ExtraBettingService(ExtraBetRepository extraBetRepository, MatchService matchService,
                               FredbetProperties fredbetProperties, RandomValueGenerator randomValueGenerator) {
        this.extraBetRepository = extraBetRepository;
        this.matchService = matchService;
        this.fredbetProperties = fredbetProperties;
        this.randomValueGenerator = randomValueGenerator;
    }

    public void saveExtraBet(Country finalWinner, Country semiFinalWinner, Country thirdFinalWinner, String username) {
        ExtraBet found = extraBetRepository.findByUserName(username);
        if (Country.NONE.equals(finalWinner) && Country.NONE.equals(semiFinalWinner) && found != null) {
            // reset/delete existing extra bet
            extraBetRepository.delete(found);
            return;
        }

        if (found == null) {
            found = new ExtraBet();
        }

        found.setFinalWinner(finalWinner);
        found.setSemiFinalWinner(semiFinalWinner);
        if (matchService.isGameForThirdAvailable()) {
            found.setThirdFinalWinner(thirdFinalWinner);
        }
        found.setUserName(username);

        extraBetRepository.save(found);
    }

    public ExtraBet loadExtraBetForUser(String username) {
        ExtraBet extraBet = extraBetRepository.findByUserName(username);
        if (extraBet == null) {
            extraBet = new ExtraBet();
            extraBet.setUserName(username);
        }

        return extraBet;
    }

    public boolean hasOpenExtraBet(String currentUserName) {
        ExtraBet extraBet = extraBetRepository.findByUserName(currentUserName);
        return extraBet == null;
    }

    public List<ExtraBet> loadExtraBetDataOthers() {
        List<ExtraBet> allExtraBets = extraBetRepository.findAll(Sort.by(Sort.Direction.ASC, "userName"));
        return allExtraBets.stream()
            .filter(extraBet -> !extraBet.getUserName().equals(fredbetProperties.adminUsername()))
            .sorted(Comparator.comparing(ExtraBet::getUserName, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    public void createExtraBetForUser(String username) {
        ImmutableTriple<Country, Country, Country> triple = randomValueGenerator.generateTeamTriple();
        if (triple != null) {
            Country extraBetCountryFinalWinner = triple.getLeft();
            Country extraBetCountrySemiFinalWinner = triple.getMiddle();
            Country extraBetCountryThirdFinalWinner = triple.getRight();
            saveExtraBet(extraBetCountryFinalWinner, extraBetCountrySemiFinalWinner, extraBetCountryThirdFinalWinner,
                username);
        }
    }

    public List<ExtraBet> findAllExtraBets() {
        return extraBetRepository.findAll();
    }

    public void createExtraBetForUser(String userName, Country finalWinner, Country semiFinalWinner, Country thirdFinalWinner,
                                      Integer pointsOne, Integer pointsTwo, Integer pointsThree) {
        ExtraBet extraBet = new ExtraBet();
        extraBet.setUserName(userName);
        extraBet.setFinalWinner(finalWinner);
        extraBet.setSemiFinalWinner(semiFinalWinner);
        extraBet.setThirdFinalWinner(thirdFinalWinner);
        extraBet.setPointsOne(pointsOne);
        extraBet.setPointsTwo(pointsTwo);
        extraBet.setPointsThree(pointsThree);
        extraBetRepository.save(extraBet);
    }
}
