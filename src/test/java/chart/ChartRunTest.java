package chart;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.collect.Lists;

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
        firstRunShouldBeBetter(
                list(2, 3),
                list(2, 42));
    }

    @Test
    public void higher_scores_are_better_with_fewer_weeks_and_worse_peak() {
        firstRunShouldBeBetter(
                list(2, 3),
                list(1, 42, 42));
    }

    @Test
    public void more_weeks_are_better() {
        firstRunShouldBeBetter(
                list(58, 60, 60),
                list( 58, 59)
        );
    }

    @Test
    public void higher_peak_is_better() {
        firstRunShouldBeBetter(
                list(1, 4),
                list(2, 3)
        );
    }

    @Test
    public void higher_peak_is_better_even_with_fewer_weeks() {
        firstRunShouldBeBetter(
                list(59),
                list(60, 60)
        );
    }

    @Test
    public void weeks_at_peak_do_not_matter() {
        bothRunsShouldBeEqual(list(1, 1, 3), list(1, 2, 2));
    }

    @Test
    public void lower_positions_in_run_do_not_matter() {
        bothRunsShouldBeEqual(list(1, 3, 4), list(1, 2, 5));
    }

    private ChartRun newChartRun() {
        return new ChartRun(mock(Song.class), ENTRY_WEEK, ENTRY_DATE);
    }

    private void runShouldHaveScore(ChartRun run, Integer expected) {
        assertEquals(expected, run.getScore());
    }

    private void firstRunShouldBeBetter(List<Integer> betterPositions, List<Integer> worsePositions) {
        ChartRun betterRun = newChartRun();
        betterPositions.forEach(betterRun::add);

        ChartRun worseRun = newChartRun();
        worsePositions.forEach(worseRun::add);

        assertEquals(-1L, betterRun.compareTo(worseRun));
    }

    private void bothRunsShouldBeEqual(List<Integer> firstRunPositions, List<Integer> secondRunPositions) {
        ChartRun firstRun = newChartRun();
        firstRunPositions.forEach(firstRun::add);

        ChartRun secondRun = newChartRun();
        secondRunPositions.forEach(secondRun::add);

        assertEquals(0L, firstRun.compareTo(secondRun));
    }

    private List<Integer> list(Integer... positions) {
        return Lists.newArrayList(positions);
    }
}
