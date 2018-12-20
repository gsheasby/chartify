package chart;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Song {
    public abstract String title();
    public abstract String artist();

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof Song)) {
            return false;
        }

        Song otherSong = (Song) other;
        return title().equalsIgnoreCase(otherSong.title())
                && artist().equalsIgnoreCase(otherSong.artist());
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + title().toLowerCase().hashCode();
        h += (h << 5) + artist().toLowerCase().hashCode();
        return h;
    }
}
