package chart.spotify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.csv.CsvChartEntry;
import chart.csv.ImmutableCsvChartEntry;

public class SpotifyChartEntryTest {
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
    private SpotifyChartEntry canonical;

    @Before
    public void setUp() {
        artist = new SimpleArtist();
        artist.setName(ARTIST);

        track = canonicalTrack();

        canonical = ImmutableSpotifyChartEntry.builder()
                                              .track(track)
                                              .position(POSITION)
                                              .weeksOnChart(WEEKS)
                                              .lastPosition(LAST_POSITION)
                                              .build();
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
    public void testEquals_unequalId() {
        Track trackWithDifferentId = canonicalTrack();
        trackWithDifferentId.setId(ID + "2");
        SpotifyChartEntry differentId = ImmutableSpotifyChartEntry.builder()
                                                                  .from(canonical)
                                                                  .track(trackWithDifferentId)
                                                                  .build();
        assertNotEquals("IDs should have been different", canonical, differentId);
    }

    @Test
    public void testEquals_unequalHref() {
        Track trackWithDifferentHref = canonicalTrack();
        trackWithDifferentHref.setHref(HREF + "2");
        SpotifyChartEntry differentHref = ImmutableSpotifyChartEntry.builder()
                .from(canonical)
                .track(trackWithDifferentHref)
                .build();
        assertNotEquals("hrefs should have been different", canonical, differentHref);
    }

    @Test
    public void testEquals_unequalUri() {
        Track trackWithDifferentUri = canonicalTrack();
        trackWithDifferentUri.setUri(URI + "2");
        SpotifyChartEntry differentHref = ImmutableSpotifyChartEntry.builder()
                .from(canonical)
                .track(trackWithDifferentUri)
                .build();
        assertNotEquals("uri should have been different", canonical, differentHref);
    }

    @Test
    public void fromKeepsAllProperties() {
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

        SpotifyChartEntry entry = SpotifyChartEntry.from(csvChartEntry);

        assertEquals(expected, entry);
    }
}