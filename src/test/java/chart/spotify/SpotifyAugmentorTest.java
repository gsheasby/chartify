package chart.spotify;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
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
    private SpotifyApi api;
    private SpotifyConfig config;
    private SpotifyAugmentor augmentor;

    @Before
    public void setUp() {
        artist = new SimpleArtist();
        artist.setName(ARTIST);

        track = canonicalTrack();

        api = mock(SpotifyApi.class);
        config = mock(SpotifyConfig.class);
        augmentor = new SpotifyAugmentor(api, config);
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
        CsvChartEntry csvChartEntry = defaultEntry();

        SpotifyChartEntry expected = ImmutableSpotifyChartEntry.builder()
                                                               .track(track)
                                                               .position(POSITION)
                                                               .weeksOnChart(WEEKS)
                                                               .lastPosition(LAST_POSITION)
                                                               .build();

        when(api.getTrack(ID)).thenReturn(canonicalTrack());

        SpotifyChartEntry entry = augmentor.augment(csvChartEntry);

        assertEquals(expected, entry);
    }

    @Test
    public void augmentListAppliesYoutubeMapping() {
        String youtubeSong = "youtube song";
        YoutubeMapping mapping = ImmutableYoutubeMapping.builder()
                                                        .id(ID)
                                                        .title(youtubeSong)
                                                        .artist(artist.getName())
                                                        .build();
        when(config.mappings()).thenReturn(Maps.newHashMap(ImmutableMap.of("bad-id", mapping)));
        when(api.getTracks(ImmutableList.of(ID))).thenReturn(ImmutableList.of());
        when(api.getTracks(ImmutableList.of())).thenReturn(ImmutableList.of());

        Track youtubeTrack = new Track();
        String href = "http://www.youtube.com/watch?v=" + ID;
        youtubeTrack.setId(ID);
        youtubeTrack.setName(youtubeSong);
        youtubeTrack.setArtists(ImmutableList.of(artist));
        youtubeTrack.setHref(href);
        youtubeTrack.setUri(href);

        SpotifyChartEntry expected = ImmutableSpotifyChartEntry.builder()
                .track(youtubeTrack)
                .position(POSITION)
                .weeksOnChart(WEEKS)
                .lastPosition(LAST_POSITION)
                .build();
        CsvChartEntry entry = defaultEntry();
        List<SpotifyChartEntry> spotifyChartEntries = augmentor.augmentList(ImmutableList.of(entry));

        assertEquals(expected, Iterables.getOnlyElement(spotifyChartEntries));
    }

    private CsvChartEntry defaultEntry() {
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