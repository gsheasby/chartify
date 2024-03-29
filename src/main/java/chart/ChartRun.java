package chart;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Objects;

public class ChartRun implements Comparable<ChartRun> {
    private static final int POSITIONS_TO_SCORE = 60;

    private final Song song;
    private final Integer entryWeek;
    private final DateTime entryDate;

    private final List<Integer> positions;
    private boolean active = false;

    public ChartRun(Song song, Integer entryWeek, DateTime entryDate) {
        this.song = song;
        this.entryWeek = entryWeek;
        this.entryDate = entryDate;
        this.positions = Lists.newArrayList();
    }

    public void add(Integer position) {
        positions.add(position);
    }

    public void setActive(boolean isActive) {
        this.active = isActive;
    }

    public Song getSong() {
        return song;
    }

    public DateTime getEntryDate() {
        return entryDate;
    }

    public boolean isActive() {
        return active;
    }

    public Integer getScore() {
        return positions.stream().filter(pos -> pos <= POSITIONS_TO_SCORE)
                 .map(pos -> POSITIONS_TO_SCORE + 1 - pos)
                 .mapToInt(Integer::intValue)
                 .sum();
    }

    public Integer getPeak() {
        //noinspection OptionalGetWithoutIsPresent
        return positions.stream().mapToInt(i -> i).min().getAsInt();
    }

    public Integer getWeeks() {
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
    }

    public String getRun() {
        return positions.toString();
    }
}
