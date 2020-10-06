package chart.postgres;

import chart.postgres.raw.ArtistRecord;
import chart.postgres.raw.TrackRecord;
import chart.postgres.raw.YearEndChartEntryRecord;
import chart.spotify.SimpleSpotifyChartEntry;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class YearEndChartSaver {
    private final PostgresConnection connection;

    public YearEndChartSaver(PostgresConnection connection) {
        this.connection = connection;
    }

    public void save(int year, Map<Integer, SimpleSpotifyChartEntry> yearEndChart) {
        Set<YearEndChartEntryRecord> entriesToSave = yearEndChart.entrySet().stream()
                .map(entry -> getYearEndChartEntryRecord(year, entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
        System.out.println("Saving " + entriesToSave.size() + " entries...");
        connection.saveYearEndChartEntries(entriesToSave);
        System.out.println("Success!");
    }

    private YearEndChartEntryRecord getYearEndChartEntryRecord(
            int year,
            Integer position,
            SimpleSpotifyChartEntry chartEntry) {
        // Verify that entry exists
        Optional<TrackRecord> maybeStoredTrack = connection.getTrackById(chartEntry.id());
        String storedTrackId = maybeStoredTrack.isPresent()
                ? chartEntry.id()
                : getTrackIdFromPostgres(chartEntry);

        return YearEndChartEntryRecord.builder()
                .year(year)
                .position(position)
                .track_id(storedTrackId)
                .build();
    }

    private String getTrackIdFromPostgres(SimpleSpotifyChartEntry chartEntry) {
        System.out.println("Looking up track for " + chartEntry.title()
                + " by " + chartEntry.artist()
                + " as no direct match for ID " + chartEntry.id()
                + " could be found.");
        Optional<ArtistRecord> maybeArtist = connection.getArtist(chartEntry.artist());
        if (!maybeArtist.isPresent()) {
            throw new RuntimeException("Failed to lookup artist " + chartEntry.artist());
        }

        String artistId = maybeArtist.get().id();
        Optional<TrackRecord> maybeTrack = connection.getTrack(chartEntry.title(), artistId);
        if (!maybeTrack.isPresent()) {
            throw new RuntimeException("Failed to find track with name " + chartEntry.title());
        }

        return maybeTrack.get().id();
    }
}
