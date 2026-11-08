package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.pointcourse.ChartData;
import de.fred4jupiter.fredbet.pointcourse.PointCourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class PointCourseControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private PointCourseService pointCourseService;

    @Test
    public void showChartWithJsonDataAttribute() {
        ChartData chartData = new ChartData(List.of("A-B", "C-D"));
        chartData.addDataSet("admin", List.of(3, 5));
        when(pointCourseService.reportPointsCourse(anyString(), any())).thenReturn(chartData);

        MvcTestResult result = mvc.get().uri("/pointcourse/show").exchange();

        assertThat(result).hasStatusOk().hasViewName("info/pointcourse")
            .model().extractingByKey("chartJson").asString()
            .contains("\"labels\":[\"A-B\",\"C-D\"]", "\"datasets\":[{\"label\":\"admin\",\"data\":[3,5]}]");
        assertThat(result).bodyText().contains(
            "id=\"pointCourseChart\"",
            "data-chart=\"{&quot;",
            "js/pages/pointcourse");
    }

    @Test
    public void escapesUserNamesInDataAttribute() {
        ChartData chartData = new ChartData(List.of("A-B"));
        chartData.addDataSet("\"><script>alert(1)</script>", List.of(3));
        when(pointCourseService.reportPointsCourse(anyString(), any())).thenReturn(chartData);

        assertThat(mvc.get().uri("/pointcourse/show")).hasStatusOk()
            .bodyText().doesNotContain("<script>alert(1)</script>").contains("&lt;script&gt;alert(1)");
    }

    @Test
    public void showNoDataMessageWithoutChartScripts() {
        when(pointCourseService.reportPointsCourse(anyString(), any())).thenReturn(new ChartData(List.of()));

        MvcTestResult result = mvc.get().uri("/pointcourse/show").exchange();

        assertThat(result).hasStatusOk().model().doesNotContainKey("chartJson");
        assertThat(result).bodyText()
            .doesNotContain("pointCourseChart", "Chart.bundle.min", "js/pages/pointcourse");
    }
}
