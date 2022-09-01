package de.fred4jupiter.fredbet.pointcourse;

import de.fred4jupiter.fredbet.web.info.pointcourse.ChartData;

public interface PointCourseContainer {

    ChartData createChartData();

    boolean isEmpty();
}
