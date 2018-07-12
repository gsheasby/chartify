package chart.postgres;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.ChartReader;
import chart.postgres.raw.ChartEntryRecord;
import chart.postgres.raw.TrackArtistRecord;
import chart.spotify.ImmutableSimpleSpotifyChart;
import chart.spotify.ImmutableSimpleSpotifyChartEntry;
import chart.spotify.SimpleSpotifyChart;
import chart.spotify.SimpleSpotifyChartEntry;
import chart.spotify.SpotifyChart;

public class PostgresChartReader implements ChartReader<SpotifyChart, SimpleSpotifyChart> {
    private final PostgresConnection connection;

    public PostgresChartReader(PostgresConnection connection) {
        this.connection = connection;
    }

    @Override
    public SpotifyChart findLatestChart() throws IOException {
        /* TODO
         */
        return null;
    }

    @Override
    public SpotifyChart findDerivedChart(int week) throws IOException {
        return null;
    }

    @Override
    public SimpleSpotifyChart findChart(int week) throws IOException {
        /* TODO
           7. (Query 4) SELECT date FROM chart WHERE week = week --> Date
           8. --> SimpleSpotifyChart
         */
        List<ChartEntryRecord> chartEntries = connection.getChartEntries(week);
        Set<String> trackIds = chartEntries.stream()
                                           .map(ChartEntryRecord::track_id)
                                           .collect(Collectors.toSet());
        List<TrackArtistRecord> trackArtists = connection.getTrackArtists(trackIds);
        Set<String> artistIds = trackArtists.stream()
                .map(TrackArtistRecord::artist_id)
                .collect(Collectors.toSet());
        Map<String, SimpleArtist> artistsById = connection.getArtists(artistIds);

        Multimap<String, SimpleArtist> artistsForTracks = getArtistsForTracks(trackArtists, artistsById);

        List<SimpleSpotifyChartEntry> entries = chartEntries.stream()
                                    .map(entry -> createSpotifyEntry(entry, artistsForTracks))
                                    .collect(Collectors.toList());

        DateTime date = connection.getChartDate(week);

        return ImmutableSimpleSpotifyChart.builder()
                .entries(entries)
                .week(week)
                .date(date)
                .build();
    }

    private Multimap<String, SimpleArtist> getArtistsForTracks(List<TrackArtistRecord> trackArtists, Map<String, SimpleArtist> artistsById) {
        Multimap<String, SimpleArtist> artistsByTrack = ArrayListMultimap.create();

        for (TrackArtistRecord trackArtistRecord : trackArtists) {
            SimpleArtist artist = artistsById.get(trackArtistRecord.artist_id()); // TODO what if null?
            artistsByTrack.put(trackArtistRecord.track_id(), artist);
        }

        return artistsByTrack;
    }

    private SimpleSpotifyChartEntry createSpotifyEntry(ChartEntryRecord chartEntry, Multimap<String, SimpleArtist> artistsForTracks) {
        Track track = getTrackWithoutArtists(chartEntry);
        track.setArtists(new ArrayList<>(artistsForTracks.get(chartEntry.track_id())));
        return ImmutableSimpleSpotifyChartEntry.builder()
                                               .position(chartEntry.position())
                                               .track(track)
                                               .build();
    }

    private Track getTrackWithoutArtists(ChartEntryRecord chartEntry) {
        Track track = new Track();
        track.setId(chartEntry.track_id());
        track.setName(chartEntry.track_name());
        track.setHref(chartEntry.track_href());
        track.setUri(chartEntry.track_uri());
        return track;
    }
}
