package chart.spotify;

import chart.csv.CsvChartEntry;
import chart.csv.ImmutableCsvChartEntry;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IdLookupAugmentorTest {
    // TODO lots of stuff copied from SpotifyChartEntryTest
    private static final int POSITION = 1;
    private static final int WEEKS = 2;
    private static final int LAST_POSITION = 3;
    private static final String ARTIST = "Artist";
    private static final String TITLE = "Title";
    private static final String ID = "id";
    private static final String HREF = "href";
    private static final String URI = "uri";
    private static final String YOUTUBE_TITLE = "youtube song";

    private SimpleArtist artist;
    private Track track;
    private SpotifyApi api;
    private SpotifyConfig config;
    private IdLookupAugmentor augmentor;

    @Before
    public void setUp() {
        artist = new SimpleArtist();
        artist.setName(ARTIST);

        track = canonicalTrack();

        api = mock(SpotifyApi.class);
        config = mock(SpotifyConfig.class);
        augmentor = new IdLookupAugmentor(api, config);
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
        CsvChartEntry csvChartEntry = canonicalCsvEntry();
        SpotifyChartEntry expected = canonicalEntry();

        when(api.getTrack(ID)).thenReturn(canonicalTrack());

        SpotifyChartEntry entry = augmentor.augment(csvChartEntry);

        assertEquals(expected, entry);
    }

    @Test
    public void augmentListKeepsAllProperties() {
        CsvChartEntry csvChartEntry = canonicalCsvEntry();
        SpotifyChartEntry expected = canonicalEntry();

        when(api.getTracks(ImmutableList.of(ID)))
                .thenReturn(ImmutableList.of(canonicalTrack()));

        SpotifyChartEntry entry = Iterables.getOnlyElement(
                augmentor.augmentList(ImmutableList.of(csvChartEntry)));

        assertEquals(expected, entry);
    }

    @Test
    public void augmentListAppliesYoutubeMapping() {
        setUpYoutubeMapping();
        when(api.getTracks(ImmutableList.of(ID))).thenReturn(ImmutableList.of());
        when(api.getTracks(ImmutableList.of())).thenReturn(ImmutableList.of());

        SpotifyChartEntry expected = getYoutubeEntry();
        CsvChartEntry entry = canonicalCsvEntry();
        List<SpotifyChartEntry> spotifyChartEntries = augmentor.augmentList(ImmutableList.of(entry));

        assertEquals(expected, Iterables.getOnlyElement(spotifyChartEntries));
    }

    @Test
    public void augmentAppliesYoutubeMapping() {
        setUpYoutubeMapping();
        when(api.getTrack(ID)).thenThrow(new RuntimeException("uh oh"));

        SpotifyChartEntry expected = getYoutubeEntry();
        CsvChartEntry entry = canonicalCsvEntry();
        SpotifyChartEntry spotifyChartEntry = augmentor.augment(entry);

        assertEquals(expected, spotifyChartEntry);
    }

    private SpotifyChartEntry canonicalEntry() {
        return ImmutableSpotifyChartEntry.builder()
                                         .track(track)
                                         .position(POSITION)
                                         .weeksOnChart(WEEKS)
                                         .lastPosition(LAST_POSITION)
                                         .build();
    }

    private SpotifyChartEntry getYoutubeEntry() {
        Track youtubeTrack = new Track();
        String href = "http://www.youtube.com/watch?v=" + ID;
        youtubeTrack.setId(ID);
        youtubeTrack.setName(YOUTUBE_TITLE);
        youtubeTrack.setArtists(ImmutableList.of(artist));
        youtubeTrack.setHref(href);
        youtubeTrack.setUri(href);

        return ImmutableSpotifyChartEntry.builder()
                                         .track(youtubeTrack)
                                         .position(POSITION)
                                         .weeksOnChart(WEEKS)
                                         .lastPosition(LAST_POSITION)
                                         .isYoutube(true)
                                         .build();
    }

    private void setUpYoutubeMapping() {
        YoutubeMapping mapping = ImmutableYoutubeMapping.builder()
                                                        .id(ID)
                                                        .title(YOUTUBE_TITLE)
                                                        .artist(artist.getName())
                                                        .build();
        when(config.mappings()).thenReturn(Maps.newHashMap(ImmutableMap.of("bad-id", mapping)));
    }

    private CsvChartEntry canonicalCsvEntry() {
        return ImmutableCsvChartEntry.builder()
                                     .position(POSITION)
                                     .weeksOnChart(WEEKS)
                                     .lastPosition(LAST_POSITION)
                                     .artist(ARTIST)
                                     .title(TITLE)
                                     .id(ID)
                                     .href(HREF)
                                     .uri(URI)
                                     .build();
    }
}