package de.fred4jupiter.fredbet.info;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.entity.Info;
import de.fred4jupiter.fredbet.domain.entity.InfoPK;
import de.fred4jupiter.fredbet.web.info.InfoType;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class InfoServiceUT {

    @InjectMocks
    private InfoService infoService;

    @Mock
    private InfoRepository infoRepository;

    @Test
    public void saveInfoContentUpdatesExistingInfo() {
        Info existing = new Info(new InfoPK("rules", "en"), "old");
        when(infoRepository.findById(any(InfoPK.class))).thenReturn(Optional.of(existing));
        when(infoRepository.save(existing)).thenReturn(existing);

        Info saved = infoService.saveInfoContent(InfoType.RULES, "new", Locale.ENGLISH);

        assertThat(saved.getContent()).isEqualTo("new");
        verify(infoRepository).save(existing);
    }

    @Test
    public void saveInfoContentIfNotPresentDoesNotOverwriteExistingEntry() {
        when(infoRepository.findById(any(InfoPK.class))).thenReturn(Optional.of(new Info(new InfoPK("rules", "en"), "existing")));

        infoService.saveInfoContentIfNotPresent(InfoType.RULES, "ignored", "en");

        verify(infoRepository, never()).save(any(Info.class));
    }

    @Test
    public void findByCreatesEmptyInfoWhenNoneExists() {
        when(infoRepository.findById(any(InfoPK.class))).thenReturn(Optional.empty());
        when(infoRepository.save(any(Info.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Info info = infoService.findBy(InfoType.MISC, Locale.GERMAN);

        assertThat(info.getPk().getName()).isEqualTo("misc");
        assertThat(info.getPk().getLocaleString()).isEqualTo("de");
        assertThat(info.getContent()).isEmpty();
    }
}

