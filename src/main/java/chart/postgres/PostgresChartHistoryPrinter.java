package chart.postgres;

import chart.postgres.raw.ChartEntryRecord;
import chart.postgres.raw.TrackRecord;
import chart.spotify.ChartPosition;
import chart.spotify.ImmutableChartPosition;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.wrapper.spotify.models.SimpleArtist;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PostgresChartHistoryPrinter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("MMM-yy");

    private final PostgresConnection connection;

    public PostgresChartHistoryPrinter(PostgresConnection connection) {
        this.connection = connection;
    }

    public void printHistory(SimpleArtist artist) {
        List<TrackRecord> tracks = connection.getTracks(artist.getId());

        Map<String, TrackRecord> tracksById = tracks.stream().collect(Collectors.toMap(
                TrackRecord::id,
                track -> track));

        List<ChartEntryRecord> entries = connection.getChartEntries(tracksById.keySet());
        if (entries.isEmpty()) {
            System.out.println(artist.getName() + ": chart debut");
            System.out.println();
            return;
        }

        Multimap<String, ChartPosition> chartRunsByTrackId = convertToChartRuns(entries);

        List<ChartHistoryItem> history = chartRunsByTrackId.asMap().entrySet()
                .stream()
                .map(entry -> new ChartHistoryItem(tracksById.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());

        List<ChartHistoryItem> sortedHistory = history.stream()
                .sorted(Comparator.comparingInt(ChartHistoryItem::getEntryWeek))
                .collect(Collectors.toList());

        System.out.println("Chart history for " + artist.getName());
        sortedHistory.forEach(this::print);
        System.out.println();
    }

    // TODO copied from PostgresChartReader
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

    private void print(ChartHistoryItem item) {
        TrackRecord trackRecord = item.getTrack();
        String title = trackRecord.name();
        int entryWeek = item.getEntryWeek();
        DateTime entryDate = connection.getChartDate(entryWeek);
        Collection<ChartPosition> chartRun = item.getChartRun();
        int weeks = chartRun.size();
        int peak = chartRun.stream().map(ChartPosition::position).sorted().findFirst().orElse(-1);
        System.out.println(String.format("%s, %s, %d weeks, PP #%d",
                title, FORMATTER.print(entryDate), weeks, peak));
    }

    // TODO make immutable
    private class ChartHistoryItem {
        private final TrackRecord track;
        private final Collection<ChartPosition> chartRun;

        private ChartHistoryItem(TrackRecord track, Collection<ChartPosition> chartRun) {
            this.track = track;
            this.chartRun = chartRun;
        }

        public int getEntryWeek() {
            return chartRun.stream()
                    .map(ChartPosition::week)
                    .min(Integer::compareTo)
                    .orElseThrow(() -> new IllegalStateException("Empty chart run!"));
        }

        public TrackRecord getTrack() {
            return track;
        }

        public Collection<ChartPosition> getChartRun() {
            return chartRun;
        }
    }
}
