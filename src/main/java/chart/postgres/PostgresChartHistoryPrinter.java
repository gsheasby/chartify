package chart.postgres;

import chart.postgres.raw.ChartEntryRecord;
import chart.postgres.raw.TrackRecord;
import chart.spotify.ChartPosition;
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

        Multimap<String, ChartPosition> chartRunsByTrackId = ChartEntryRecord.toChartRuns(entries);

        List<ChartHistoryItem> history = chartRunsByTrackId.asMap().entrySet()
                .stream()
                .map(entry -> ChartHistoryItem.builder()
                        .track(tracksById.get(entry.getKey()))
                        .chartRun(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        List<ChartHistoryItem> sortedHistory = history.stream()
                .sorted(Comparator.comparingInt(ChartHistoryItem::getEntryWeek))
                .collect(Collectors.toList());

        System.out.println("Chart history for " + artist.getName());
        sortedHistory.forEach(this::print);
        System.out.println();
    }

    private void print(ChartHistoryItem item) {
        Collection<ChartPosition> chartRun = item.chartRun();

        String title = item.track().name();
        DateTime entryDate = connection.getChartDate(item.getEntryWeek());
        int weeks = chartRun.size();
        String weekStr = weeks == 1 ? "week" : "weeks";
        int peak = chartRun.stream().map(ChartPosition::position).sorted().findFirst().orElse(-1);
        System.out.println(String.format("%s, %s, %d %s, PP #%d",
                title, FORMATTER.print(entryDate), weeks, weekStr, peak));
    }

}
