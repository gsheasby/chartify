package chart;

import org.immutables.value.Value;

public interface SimpleChartEntry {
    int position();

    String title();

    String artist();

    String id();

    String href();

    String uri();

    @Value.Default
    default Song toSong() {
        return ImmutableSong.builder()
                .id(id())
                .title(title())
                .artist(artist())
                .build();
    }
}
