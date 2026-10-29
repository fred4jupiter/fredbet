package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.excel.PointCountResult;
import de.fred4jupiter.fredbet.web.info.PointsFrequencyCommand.UserPointsFrequency;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PointsFrequencyCommandUT {

    @Test
    public void usersAreSortedLikeAMedalTable() {
        PointsFrequencyCommand command = new PointsFrequencyCommand(List.of(
            new PointCountResult("carl", 3, 2L),
            new PointCountResult("carl", 1, 1L),
            new PointCountResult("anna", 3, 2L),
            new PointCountResult("anna", 2, 4L),
            new PointCountResult("bert", 2, 9L),
            new PointCountResult("dora", 0, 5L)
        ));

        assertThat(command.getPointsList()).containsExactly(3, 2, 1);
        assertThat(command.getRows()).extracting(UserPointsFrequency::username).containsExactly("anna", "carl", "bert", "dora");

        UserPointsFrequency carl = command.getRows().get(1);
        assertThat(carl.getCount(3)).isEqualTo(2);
        assertThat(carl.getCount(2)).isZero();
    }

    @Test
    public void maxCountPerPointsIsHighlighted() {
        PointsFrequencyCommand command = new PointsFrequencyCommand(List.of(
            new PointCountResult("anna", 3, 2L),
            new PointCountResult("bert", 3, 2L),
            new PointCountResult("bert", 2, 1L)
        ));

        assertThat(command.isMaxCount(3, 2)).isTrue();
        assertThat(command.isMaxCount(2, 1)).isTrue();
        assertThat(command.isMaxCount(2, 0)).isFalse();
    }
}
