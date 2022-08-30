package de.fred4jupiter.fredbet.repository;

import java.util.List;
import java.util.Map;

interface BetRepositoryCustom {

    List<UsernamePoints> calculateRanging();

    Map<Long, PointCourseResultSimple> fetchPointCourseResultSimple();
}
