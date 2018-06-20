package chart.csv;

import org.immutables.value.Value;

import chart.ChartEntry;

@Value.Immutable
public abstract class CsvChartEntry implements ChartEntry {

    @Override
    @Value.Default
    public String id() {
        return "";
    }

    @Override
    @Value.Default
    public String href() {
        return "";
    }

    @Override
    @Value.Default
    public String uri() {
        return "";
    }
}
