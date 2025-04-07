package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.domain.Country;

record ExtraBetToExport(String userName, Country finalWinner, Country semiFinalWinner, Country thirdFinalWinner,
                        Integer pointsOne, Integer pointsTwo, Integer pointsThree) {

}
