package chart.postgres.raw;

import chart.spotify.ChartPosition;
import chart.spotify.ImmutableChartPosition;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class ChartEntryRecord {
    public abstract int chart_week();
    public abstract int position();
    public abstract String track_id();

    public static Multimap<String, ChartPosition> toChartRuns(List<ChartEntryRecord> chartEntries) {
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
}
