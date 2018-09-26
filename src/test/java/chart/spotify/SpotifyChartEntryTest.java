package chart.spotify;

import chart.ChartTestUtils;
import com.wrapper.spotify.models.Track;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class SpotifyChartEntryTest {
    private static final int POSITION = 1;
    private static final int WEEKS = 2;
    private static final int LAST_POSITION = 3;
    private static final String ID = ChartTestUtils.ID;
    private static final String HREF = ChartTestUtils.HREF;
    private static final String URI = ChartTestUtils.URI;

    private SpotifyChartEntry canonical;

    @Before
    public void setUp() {
        Track track = ChartTestUtils.track();

        canonical = ImmutableSpotifyChartEntry.builder()
                                              .track(track)
                                              .position(POSITION)
                                              .weeksOnChart(WEEKS)
                                              .lastPosition(LAST_POSITION)
                                              .build();
    }

    @Test
    public void testEquals_unequalId() {
        Track trackWithDifferentId = ChartTestUtils.track();
        trackWithDifferentId.setId(ID + "2");
        SpotifyChartEntry differentId = ImmutableSpotifyChartEntry.builder()
                                                                  .from(canonical)
                                                                  .track(trackWithDifferentId)
                                                                  .build();
        assertNotEquals("IDs should have been different", canonical, differentId);
    }

    @Test
    public void testEquals_unequalHref() {
        Track trackWithDifferentHref = ChartTestUtils.track();
        trackWithDifferentHref.setHref(HREF + "2");
        SpotifyChartEntry differentHref = ImmutableSpotifyChartEntry.builder()
                .from(canonical)
                .track(trackWithDifferentHref)
                .build();
        assertNotEquals("hrefs should have been different", canonical, differentHref);
    }

    @Test
    public void testEquals_unequalUri() {
        Track trackWithDifferentUri = ChartTestUtils.track();
        trackWithDifferentUri.setUri(URI + "2");
        SpotifyChartEntry differentHref = ImmutableSpotifyChartEntry.builder()
                .from(canonical)
                .track(trackWithDifferentUri)
                .build();
        assertNotEquals("uri should have been different", canonical, differentHref);
    }
}