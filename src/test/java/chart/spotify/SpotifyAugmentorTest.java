package chart.spotify;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.csv.CsvChartEntry;
import chart.csv.ImmutableCsvChartEntry;

public class SpotifyAugmentorTest {
    // TODO lots of stuff copied from SpotifyChartEntryTest
    private static final int POSITION = 1;
    private static final int WEEKS = 2;
    private static final int LAST_POSITION = 3;
    private static final String ARTIST = "Artist";
    private static final String TITLE = "Title";
    private static final String ID = "id";
    private static final String HREF = "href";
    private static final String URI = "uri";

    private SimpleArtist artist;
    private Track track;

    @Before
    public void setUp() {
        artist = new SimpleArtist();
        artist.setName(ARTIST);

        track = canonicalTrack();
    }

    private Track canonicalTrack() {
        Track canonical = new Track();
        canonical.setName(TITLE);
        canonical.setArtists(ImmutableList.of(artist));
        canonical.setId(ID);
        canonical.setHref(HREF);
        canonical.setUri(URI);
        return canonical;
    }

    @Test
    public void augmentKeepsAllProperties() {
        CsvChartEntry csvChartEntry = ImmutableCsvChartEntry.builder()
                                                            .position(POSITION)
                                                            .weeksOnChart(WEEKS)
                                                            .lastPosition(LAST_POSITION)
                                                            .artist(ARTIST)
                                                            .title(TITLE)
                                                            .id(ID)
                                                            .href(HREF)
                                                            .uri(URI)
                                                            .build();


        SpotifyChartEntry expected = ImmutableSpotifyChartEntry.builder()
                                                               .track(track)
                                                               .position(POSITION)
                                                               .weeksOnChart(WEEKS)
                                                               .lastPosition(LAST_POSITION)
                                                               .build();

        SpotifyApi api = mock(SpotifyApi.class);
        when(api.getTrack(ID)).thenReturn(canonicalTrack());

        SpotifyChartEntry entry = new SpotifyAugmentor(api).augment(csvChartEntry);

        assertEquals(expected, entry);
    }
}