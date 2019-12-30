package chart.postgres;

import chart.postgres.raw.YearEndChartEntryRecord;
import chart.spotify.SimpleSpotifyChartEntry;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class YearEndChartSaver {
    private final PostgresConnection connection;

    public YearEndChartSaver(PostgresConnection connection) {
        this.connection = connection;
    }

    public void save(int year, Map<Integer, SimpleSpotifyChartEntry> yearEndChart) {
        Set<YearEndChartEntryRecord> entriesToSave = yearEndChart.entrySet().stream()
                .map(entry -> YearEndChartEntryRecord.builder()
                        .year(year)
                        .position(entry.getKey())
                        .track_id(entry.getValue().id())
                        .build())
                .collect(Collectors.toSet());
        System.out.println("Saving " + entriesToSave.size() + " entries...");
        connection.saveYearEndChartEntries(entriesToSave);
        System.out.println("Success!");
    }
}
