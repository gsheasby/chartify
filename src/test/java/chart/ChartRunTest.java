package chart;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.joda.time.DateTime;
import org.junit.Test;

public class ChartRunTest {
    private static final Integer ENTRY_WEEK = 1;
    private static final DateTime ENTRY_DATE = DateTime.parse("2018-01-01");

    @Test
    public void score_is_based_on_positions() {
        ChartRun run = newChartRun();
        run.add(1);
        runShouldHaveScore(run, 60);

        run.add(2);
        runShouldHaveScore(run, 119);

        run.add(60);
        runShouldHaveScore(run, 120);
    }

    @Test
    public void score_ignores_positions_outside_top_sixty() {
        ChartRun run = newChartRun();
        run.add(60);
        runShouldHaveScore(run, 1);

        run.add(61);
        runShouldHaveScore(run, 1);

        run.add(75);
        runShouldHaveScore(run, 1);
    }

    @Test
    public void higher_scores_are_better() {
        ChartRun betterRun = newChartRun();
        betterRun.add(2);
        betterRun.add(3);

        ChartRun worseRun = newChartRun();
        worseRun.add(2);
        worseRun.add(42);

        assertEquals(-1L, betterRun.compareTo(worseRun));
    }

    @Test
    public void higher_scores_are_better_with_fewer_weeks_and_worse_peak() {
        ChartRun betterRun = newChartRun();
        betterRun.add(2);
        betterRun.add(3);

        ChartRun worseRun = newChartRun();
        worseRun.add(1);
        worseRun.add(42);
        worseRun.add(42);

        assertEquals(-1L, betterRun.compareTo(worseRun));
    }

    private ChartRun newChartRun() {
        return new ChartRun(mock(Song.class), ENTRY_WEEK, ENTRY_DATE);
    }

    private void runShouldHaveScore(ChartRun run, Integer expected) {
        assertEquals(expected, run.getScore());
    }
}
