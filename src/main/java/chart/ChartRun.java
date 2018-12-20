package chart;

import java.util.List;
import java.util.Objects;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;

public class ChartRun implements Comparable<ChartRun> {
    private static final int POSITIONS_TO_SCORE = 60;

    private final Song song;
    private final Integer entryWeek;
    private final DateTime entryDate;

    private List<Integer> positions;

    public ChartRun(Song song, Integer entryWeek, DateTime entryDate) {
        this.song = song;
        this.entryWeek = entryWeek;
        this.entryDate = entryDate;
        this.positions = Lists.newArrayList();
    }

    public void add(Integer position) {
        positions.add(position);
    }

    public Integer getScore() {
        return positions.stream().filter(pos -> pos <= POSITIONS_TO_SCORE)
                 .map(pos -> POSITIONS_TO_SCORE + 1 - pos)
                 .mapToInt(Integer::intValue)
                 .sum();
    }

    private Integer getPeak() {
        //noinspection OptionalGetWithoutIsPresent
        return positions.stream().mapToInt(i -> i).min().getAsInt();
    }

    private Integer getWeeks() {
        return positions.size();
    }

    @Override
    public int compareTo(ChartRun o) {
        Integer score = getScore();
        Integer otherScore = o.getScore();
        if (!Objects.equals(score, otherScore)) {
            // Higher scores are better
            return -score.compareTo(otherScore);
        } else {
            Integer peak = getPeak();
            Integer otherPeak = o.getPeak();
            if (!Objects.equals(peak, otherPeak)) {
                // Lower peaks are better
                return peak.compareTo(otherPeak);
            } else {
                // More weeks are better
                return -getWeeks().compareTo(o.getWeeks());
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%03d\t%02d\t%02d\t%s\t-\t%s\t%s\t%s",
                             getScore(),
                             positions.size(),
                             getPeak(),
                             song.title(),
                             song.artist(),
                             entryDate.toLocalDate(),
                             positions.toString());

//                    getScore() + ": " + song.title() + " - " + song.artist()
//                    + " (" + positions.size() + " weeks; run: " + positions.toString() + ")";
    }

}
