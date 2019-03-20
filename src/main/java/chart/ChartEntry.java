package chart;

import java.util.Optional;

import org.immutables.value.Value;

public interface ChartEntry {
    Integer position();

    Optional<Integer> lastPosition();

    Integer weeksOnChart();

    String title();

    String artist();

    String id();

    String href();

    String uri();

    @Value.Default
    default Song toSong() {
        return ImmutableSong.builder()
                            .title(title())
                            .artist(artist())
                            .build();
    }

    default boolean sameSongAs(SimpleChartEntry entry) {
        return id().equalsIgnoreCase(entry.id())
                || (artist().equalsIgnoreCase(entry.artist())
                && title().equalsIgnoreCase(entry.title()));
    }
}
