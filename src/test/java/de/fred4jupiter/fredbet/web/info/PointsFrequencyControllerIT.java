package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.excel.PointCountResult;
import de.fred4jupiter.fredbet.excel.ReportService;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class PointsFrequencyControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ReportService reportService;

    @Test
    public void showPointsFrequencyMatrix() {
        MultiValuedMap<Integer, PointCountResult> map = new ArrayListValuedHashMap<>();
        map.put(3, new PointCountResult("anna", 3, 2L));
        map.put(2, new PointCountResult("bert", 2, 4L));
        when(reportService.reportPointsFrequency()).thenReturn(map);

        assertThat(mvc.get().uri("/info/pointsfrequency"))
            .hasStatusOk()
            .hasViewName("info/points_freq")
            .bodyText().contains("points-freq-table", "anna", "bert");
    }

    @Test
    public void showNoDataMessage() {
        when(reportService.reportPointsFrequency()).thenReturn(new ArrayListValuedHashMap<>());

        MvcTestResult result = mvc.get().uri("/info/pointsfrequency").exchange();

        assertThat(result).hasStatusOk().model().doesNotContainKey("pointsFrequencyCommand");
        assertThat(result).bodyText().doesNotContain("points-freq-table");
    }

}
