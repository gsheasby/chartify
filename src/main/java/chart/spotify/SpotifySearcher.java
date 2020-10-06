package chart.spotify;

import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.SimpleArtist;
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
        List<Track> tracks = api.searchForTrack(title, artist);
        Optional<Track> bestMatch = getClosestMatch(tracks, title, artist);

        if (!bestMatch.isPresent()) {
            System.out.println(String.format(
                    "Couldn't find exact matches for track %s by %s - potential matches were:\n%s", title, artist,
                    printSearchResults(tracks)));
        }
        return bestMatch;
    }

    private Optional<Track> getClosestMatch(List<Track> tracks, String title, String artist) {
        Track closestMatch = null;
        int closestDistance = Integer.MAX_VALUE;
        for (Track item : tracks) {
            boolean sameTitle = item.getName().equalsIgnoreCase(title);
            boolean sameArtist = item.getArtists().stream().anyMatch(a -> a.getName().equalsIgnoreCase(artist));
            if (sameTitle && sameArtist) {
                return Optional.of(item);
            }

            if (sameArtist) {
                int distance = StringUtils.getLevenshteinDistance(title, item.getName());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestMatch = item;
                }
            }

            if (sameTitle) {
                // Could be a "The" mismatch
                String theArtist = addOrRemoveThe(artist);
                if (item.getArtists().stream().anyMatch(a -> a.getName().equalsIgnoreCase(theArtist))) {
                    List<String> artistNames = item.getArtists().stream()
                            .map(SimpleArtist::getName)
                            .collect(Collectors.toList());
                    System.out.println("Matched artist " + artist + " with one of " + artistNames);
                    return Optional.of(item);
                }
            }
        }

        return (closestMatch != null && closestDistance < 5)
                ? Optional.of(closestMatch)
                : Optional.empty();
    }

    // TODO: same as TrackSearchingAugmenter.getArtistInvariantToThe
    private String addOrRemoveThe(String artist) {
        if (artist.startsWith("The ")) {
            return artist.substring(4);
        } else {
            return "The " + artist;
        }
    }

    public Optional<SimpleArtist> searchForArtist(String name) {
        List<Artist> items = api.searchForArtist(name);
        Optional<Artist> artist = getClosestMatch(items, name);

        if (!artist.isPresent()) {
            System.out.println(String.format(
                    "Couldn't find exact matches for artist %s - potential matches were:\n%s", name,
                    printArtists(items)));
        }
        return artist.map(this::convertToSimpleArtist);
    }

    private Optional<Artist> getClosestMatch(List<Artist> artists, String name) {
        Artist closestMatch = null;
        int closestDistance = Integer.MAX_VALUE;
        for (Artist item : artists) {
            if (item.getName().equalsIgnoreCase(name)) {
                return Optional.of(item);
            }

            int distance = StringUtils.getLevenshteinDistance(name, item.getName());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestMatch = item;
            }
        }

        return closestDistance < 5
                ? Optional.of(closestMatch)
                : Optional.empty();
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

    private String printArtists(List<Artist> artists) {
        return artists.stream().map(Artist::getName).collect(Collectors.joining("\n"));
    }

}
