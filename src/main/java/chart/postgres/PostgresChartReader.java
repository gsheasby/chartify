package chart.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.ChartReader;
import chart.postgres.raw.ArtistRecord;
import chart.postgres.raw.ChartEntryRecord;
import chart.postgres.raw.TrackPositionRecord;
import chart.postgres.raw.TrackArtistRecord;
import chart.spotify.ChartPosition;
import chart.spotify.ImmutableChartPosition;
import chart.spotify.ImmutableSimpleSpotifyChart;
import chart.spotify.ImmutableSimpleSpotifyChartEntry;
import chart.spotify.ImmutableSpotifyChart;
import chart.spotify.ImmutableSpotifyChartEntry;
import chart.spotify.SimpleSpotifyChart;
import chart.spotify.SimpleSpotifyChartEntry;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class PostgresChartReader implements ChartReader<SpotifyChart, SimpleSpotifyChart> {
    private final PostgresConnection connection;

    public PostgresChartReader(PostgresConnection connection) {
        this.connection = connection;
    }

    @Override
    public SpotifyChart findLatestChart() {
        int latestWeek = connection.getLatestWeek();
        return findDerivedChart(latestWeek);
    }

    @Override
    public SpotifyChart findDerivedChart(int week) {
        List<TrackPositionRecord> trackPositions = connection.getTrackPositions(week);
        Set<String> trackIds = getTrackIds(trackPositions);
        Map<String, Integer> lastPositions = connection.getPositions(trackIds, week - 1);
        Map<String, Integer> weeksOnChart = connection.getWeeksOnChart(trackIds, week);
        Multimap<String, SimpleArtist> artistsForTracks = getArtistsForTracks(trackIds);

        List<ChartEntryRecord> chartEntries = connection.getChartEntries(trackIds, week);
        Multimap<String, ChartPosition> chartRuns = convertToChartRuns(chartEntries);

        List<SpotifyChartEntry> entries = trackPositions.stream()
                .map(entry -> createSpotifyEntry(entry, lastPositions, weeksOnChart, artistsForTracks, chartRuns))
                .collect(Collectors.toList());

        DateTime date = connection.getChartDate(week);

        return ImmutableSpotifyChart.builder()
                .entries(entries)
                .week(week)
                .date(date)
                .build();
    }

    private Multimap<String, ChartPosition> convertToChartRuns(List<ChartEntryRecord> chartEntries) {
        Multimap<String, ChartPosition> chartRuns = ArrayListMultimap.create();

        for (ChartEntryRecord record : chartEntries) {
            String trackId = record.track_id();
            ChartPosition chartPosition = ImmutableChartPosition.builder()
                    .week(record.chart_week())
                    .position(record.position())
                    .build();
            chartRuns.put(trackId, chartPosition);
        }

        return chartRuns;
    }

    @Override
    public SimpleSpotifyChart findChart(int week) {
        List<TrackPositionRecord> trackPositions = connection.getTrackPositions(week);
        Set<String> trackIds = getTrackIds(trackPositions);
        Multimap<String, SimpleArtist> artistsForTracks = getArtistsForTracks(trackIds);

        List<SimpleSpotifyChartEntry> entries = trackPositions.stream()
                                    .map(entry -> createSimpleSpotifyEntry(entry, artistsForTracks))
                                    .collect(Collectors.toList());

        DateTime date = connection.getChartDate(week);

        return ImmutableSimpleSpotifyChart.builder()
                .entries(entries)
                .week(week)
                .date(date)
                .build();
    }

    private Set<String> getTrackIds(List<TrackPositionRecord> chartEntries) {
        return chartEntries.stream().map(TrackPositionRecord::track_id).collect(Collectors.toSet());
    }

    private Multimap<String, SimpleArtist> getArtistsForTracks(Set<String> trackIds) {
        List<TrackArtistRecord> trackArtists = connection.getTrackArtists(trackIds);
        Set<String> artistIds = trackArtists.stream()
                                            .map(TrackArtistRecord::artist_id)
                                            .collect(Collectors.toSet());
        Map<String, ArtistRecord> artistsById = connection.getArtists(artistIds);

        return getArtistsForTracks(trackArtists, artistsById);
    }

    private Multimap<String, SimpleArtist> getArtistsForTracks(List<TrackArtistRecord> trackArtists,
                                                               Map<String, ArtistRecord> artistsById) {
        Multimap<String, SimpleArtist> artistsByTrack = ArrayListMultimap.create();

        for (TrackArtistRecord trackArtistRecord : trackArtists) {
            String artistId = trackArtistRecord.artist_id();
            if (!artistsById.containsKey(artistId)) {
                continue;
            }

            // TODO information is squashed here - not a big deal for now as is_youtube artists only go with is_youtube tracks
            artistsByTrack.put(trackArtistRecord.track_id(), artistsById.get(artistId).simpleArtist());
        }

        return artistsByTrack;
    }

    private SpotifyChartEntry createSpotifyEntry(TrackPositionRecord chartEntry,
                                                 Map<String, Integer> lastPositions,
                                                 Map<String, Integer> weeksOnChart,
                                                 Multimap<String, SimpleArtist> artistsForTracks,
                                                 Multimap<String, ChartPosition> chartRuns) {
        Track track = getTrackWithoutArtists(chartEntry);
        String trackId = chartEntry.track_id();
        track.setArtists(new ArrayList<>(artistsForTracks.get(trackId)));
        return ImmutableSpotifyChartEntry.builder()
                .track(track)
                .position(chartEntry.position())
                .lastPosition(lastPositions.containsKey(trackId)
                                      ? Optional.of(lastPositions.get(trackId))
                                      : Optional.empty())
                 // by construction, the 0 shouldn't happen in practice
                .weeksOnChart(weeksOnChart.getOrDefault(trackId, 0))
                .isYoutube(chartEntry.is_youtube())
                .chartRun(chartRuns.get(trackId))
                .build();
    }

    private SimpleSpotifyChartEntry createSimpleSpotifyEntry(TrackPositionRecord chartEntry,
                                                             Multimap<String, SimpleArtist> artistsForTracks) {
        Track track = getTrackWithoutArtists(chartEntry);
        track.setArtists(new ArrayList<>(artistsForTracks.get(chartEntry.track_id())));
        return ImmutableSimpleSpotifyChartEntry.builder()
                                               .position(chartEntry.position())
                                               .track(track)
                                               .isYoutube(chartEntry.is_youtube())
                                               .build();
    }

    private Track getTrackWithoutArtists(TrackPositionRecord chartEntry) {
        Track track = new Track();
        track.setId(chartEntry.track_id());
        track.setName(chartEntry.track_name());
        track.setHref(chartEntry.track_href());
        track.setUri(chartEntry.track_uri());
        return track;
    }
}
