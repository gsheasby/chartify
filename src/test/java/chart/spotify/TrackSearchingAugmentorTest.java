package chart.spotify;

import chart.ChartTestUtils;
import chart.csv.CsvChartEntry;
import chart.postgres.PostgresConnection;
import chart.postgres.raw.ArtistRecord;
import chart.postgres.raw.TrackRecord;
import com.google.common.collect.ImmutableList;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class TrackSearchingAugmentorTest {
    private SpotifyApi api;
    private PostgresConnection connection;
    private TrackSearchingAugmentor augmentor;

    @Before
    public void setUp() {
        api = mock(SpotifyApi.class);
        connection = mock(PostgresConnection.class);
        augmentor = TrackSearchingAugmentor.create(api, connection);
    }

    @Test
    public void augmentKeepsAllProperties() {
        Track track = ChartTestUtils.track();
        when(api.searchForTrack(track.getName(), track.getArtists().get(0).getName()))
                .thenReturn(ImmutableList.of(track));

        CsvChartEntry csvChartEntry = ChartTestUtils.csvChartEntry(2, 3);
        SpotifyChartEntry expected = ChartTestUtils.entry(2, 3);

        SpotifyChartEntry entry = augmentor.augment(csvChartEntry);

        assertEquals(expected, entry);
    }

    @Test
    public void augmentDoesNotSearchSpotifyIfTrackAlreadyInPostgres() {
        Track track = ChartTestUtils.track();
        TrackRecord trackRecord = TrackRecord.from(track, false);
        SimpleArtist artist = ChartTestUtils.artist();
        ArtistRecord record = ArtistRecord.from(artist, false);
        when(connection.getArtist(track.getArtists().get(0).getName())).thenReturn(Optional.of(record));
        when(connection.getTrack(track.getName(), record.id())).thenReturn(Optional.of(trackRecord));

        CsvChartEntry csvChartEntry = ChartTestUtils.csvChartEntry(2, 3);
        SpotifyChartEntry expected = ChartTestUtils.entry(2, 3);
        assertEquals(record.name(), csvChartEntry.artist());

        SpotifyChartEntry entry = augmentor.augment(csvChartEntry);

        assertEquals(expected, entry);
        verifyNoMoreInteractions(api);
    }
}
