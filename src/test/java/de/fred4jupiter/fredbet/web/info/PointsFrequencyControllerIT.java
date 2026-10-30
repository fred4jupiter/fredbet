package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.common.LoggedInSession;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.excel.PointCountResult;
import de.fred4jupiter.fredbet.excel.ReportService;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcIntegrationTest
public class PointsFrequencyControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserDetailsService userDetailsService;

    @MockitoBean
    private ReportService reportService;

    @Test
    public void showPointsFrequencyMatrix() throws Exception {
        MultiValuedMap<Integer, PointCountResult> map = new ArrayListValuedHashMap<>();
        map.put(3, new PointCountResult("anna", 3, 2L));
        map.put(2, new PointCountResult("bert", 2, 4L));
        when(reportService.reportPointsFrequency()).thenReturn(map);

        mockMvc.perform(get("/info/pointsfrequency").session(loggedInSession()))
            .andExpect(status().isOk())
            .andExpect(view().name("info/points_freq"))
            .andExpect(content().string(containsString("points-freq-table")))
            .andExpect(content().string(containsString("anna")))
            .andExpect(content().string(containsString("bert")));
    }

    @Test
    public void showNoDataMessage() throws Exception {
        when(reportService.reportPointsFrequency()).thenReturn(new ArrayListValuedHashMap<>());

        mockMvc.perform(get("/info/pointsfrequency").session(loggedInSession()))
            .andExpect(status().isOk())
            .andExpect(model().attributeDoesNotExist("pointsFrequencyCommand"))
            .andExpect(content().string(not(containsString("points-freq-table"))));
    }

    private MockHttpSession loggedInSession() {
        return LoggedInSession.of(userDetailsService, "admin");
    }
}
