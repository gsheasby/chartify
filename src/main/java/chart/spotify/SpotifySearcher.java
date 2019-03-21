package chart.spotify;

import com.wrapper.spotify.models.Track;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/* TODO TESTS
   - Returns exact match
   - Returns close matches
   - Returns exact match if preceded by close matches
   - Empty case
 */
public class SpotifySearcher {
    private final SpotifyApi api;

    public SpotifySearcher(SpotifyApi api) {
        this.api = api;
    }

    public Optional<Track> searchForTrack(String title, String artist) {
        Optional<Track> bestMatch = Optional.empty();
        List<Track> tracks = api.searchForTrack(title, artist);

        Track closestMatch = null;
        int closestDistance = Integer.MAX_VALUE;
        for (Track item : tracks) {
            boolean sameTitle = item.getName().equalsIgnoreCase(title);
            boolean sameArtist = item.getArtists().stream().anyMatch(a -> a.getName().equalsIgnoreCase(artist));
            if (sameTitle && sameArtist) {
                bestMatch = Optional.of(item);
            }

            if (sameArtist) {
                int distance = StringUtils.getLevenshteinDistance(title, item.getName());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestMatch = item;
                }
            }
        }

        if (!bestMatch.isPresent() && closestMatch != null && closestDistance < 5) {
            bestMatch = Optional.of(closestMatch);
        }

        if (!bestMatch.isPresent()) {
            throw new IllegalStateException(String.format(
                    "Couldn't find exact matches for track %s by %s - potential matches were:\n%s", title, artist,
                    printSearchResults(tracks)));
        }
        return bestMatch;
    }

    private String printSearchResults(List<Track> tracks) {
        return tracks.stream()
                .map(this::printTrack)
                .collect(Collectors.joining("\n"));
    }

    private String printTrack(Track track) {
        return track.getArtists().get(0).getName() + " - " + track.getName();
    }
}
