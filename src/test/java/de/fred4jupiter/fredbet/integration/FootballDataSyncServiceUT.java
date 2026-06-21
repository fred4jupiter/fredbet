package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.admin.CacheAdministrationService;
import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.integration.model.FdMatches;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.props.CacheNames;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class FootballDataSyncServiceUT {

    @Mock
    private FootballDataLoader footballDataLoader;

    @Mock
    private MatchService matchService;

    @Mock
    private FdMatchSyncImporter fdMatchSyncImporter;

    @Mock
    private CacheAdministrationService administrationService;

    @InjectMocks
    private FootballDataSyncService footballDataSyncService;

    @Test
    public void syncData_withNullMatches_doesNothing() {
        footballDataSyncService.syncData((FdMatches) null, false);

        verify(fdMatchSyncImporter, never()).mapAndSave(any(FdMatch.class), any(Match.class), eq(false));
        verify(administrationService, never()).clearCacheByCacheName(any(String.class));
    }

    @Test
    public void syncData_withEmptyMatches_doesNothing() {
        footballDataSyncService.syncData(new FdMatches(List.of()), true);

        verify(fdMatchSyncImporter, never()).mapAndSave(any(FdMatch.class), any(Match.class), eq(true));
        verify(administrationService, never()).clearCacheByCacheName(any(String.class));
    }

    @Test
    public void syncData_withFetchedCompetitionData_syncsAllAndClearsCache() {
        Competition competition = new Competition(2001, "Cup", "CUP", 2026, "LEAGUE");

        FdMatch fdMatchOne = new FdMatch(null, "m1", null, null, null, null, null, null, null, null, null);
        FdMatch fdMatchTwo = new FdMatch(null, "m2", null, null, null, null, null, null, null, null, null);
        FdMatches fdMatches = new FdMatches(List.of(fdMatchOne, fdMatchTwo));

        Match existingMatch = new Match();

        when(footballDataLoader.fetchMatches(competition)).thenReturn(fdMatches);
        when(matchService.findByExternalId("m1")).thenReturn(Optional.of(existingMatch));
        when(matchService.findByExternalId("m2")).thenReturn(Optional.empty());

        footballDataSyncService.syncData(competition, true);

        verify(fdMatchSyncImporter).mapAndSave(eq(fdMatchOne), eq(existingMatch), eq(true));
        verify(fdMatchSyncImporter).mapAndSave(eq(fdMatchTwo), any(Match.class), eq(true));
        verify(administrationService).clearCacheByCacheName(CacheNames.AVAIL_GROUPS);
    }
}

