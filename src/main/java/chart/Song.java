package chart;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Song {
    public abstract String title();
    public abstract String artist();

    public static Song fromEntry(ChartEntry entry) {
        return ImmutableSong.builder()
                .title(entry.title())
                .artist(entry.artist())
                .build();
    }
}
