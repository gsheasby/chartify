package chart.spotify;

import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
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
            System.out.println(String.format(
                    "Couldn't find exact matches for track %s by %s - potential matches were:\n%s", title, artist,
                    printSearchResults(tracks)));
        }
        return bestMatch;
    }

    public Optional<SimpleArtist> searchForArtist(String name) {
        try {
            return api.getArtist(name).map(this::convertToSimpleArtist);
        } catch (IOException | WebApiException e) {
            throw new RuntimeException(e);
        }
    }

    private SimpleArtist convertToSimpleArtist(Artist artist) {
        SimpleArtist simpleArtist = new SimpleArtist();
        simpleArtist.setId(artist.getId());
        simpleArtist.setName(artist.getName());
        simpleArtist.setType(artist.getType());
        simpleArtist.setExternalUrls(artist.getExternalUrls());
        simpleArtist.setHref(artist.getHref());
        simpleArtist.setUri(artist.getUri());
        return simpleArtist;
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
