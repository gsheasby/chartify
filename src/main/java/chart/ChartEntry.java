package chart;

import java.util.Optional;

public interface ChartEntry {
    Integer position();

    Optional<Integer> lastPosition();

    Integer weeksOnChart();

    String title();

    String artist();
}
