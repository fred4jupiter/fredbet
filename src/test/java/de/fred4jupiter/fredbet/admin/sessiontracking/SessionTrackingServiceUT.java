package de.fred4jupiter.fredbet.admin.sessiontracking;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.entity.SessionTracking;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class SessionTrackingServiceUT {

    @InjectMocks
    private SessionTrackingService sessionTrackingService;

    @Mock
    private SessionTrackingRepository sessionTrackingRepository;

    @Test
    public void registerLoginUpdatesExistingSessionTracking() {
        SessionTracking existing = new SessionTracking();
        existing.setUserName("alfredo");
        when(sessionTrackingRepository.findById("alfredo")).thenReturn(Optional.of(existing));

        sessionTrackingService.registerLogin("alfredo", "session-1");

        assertThat(existing.getSessionId()).isEqualTo("session-1");
        assertThat(existing.getLastLogin()).isNotNull();
        verify(sessionTrackingRepository).save(existing);
    }

    @Test
    public void registerLogoutSkipsBlankSessionId() {
        sessionTrackingService.registerLogout(" ");

        verify(sessionTrackingRepository, never()).deleteBySessionId(" ");
    }

    @Test
    public void findLoggedInUsersFiltersEntriesOlderThanOneDay() {
        SessionTracking active = new SessionTracking();
        active.setUserName("active");
        active.setLastLogin(LocalDateTime.now().minusHours(2));

        SessionTracking outdated = new SessionTracking();
        outdated.setUserName("outdated");
        outdated.setLastLogin(LocalDateTime.now().minusDays(2));

        when(sessionTrackingRepository.findAllByOrderByLastLoginDesc()).thenReturn(List.of(active, outdated));

        List<SessionTracking> loggedInUsers = sessionTrackingService.findLoggedInUsers();

        assertThat(loggedInUsers).containsExactly(active);
    }
}

