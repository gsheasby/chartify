package chart.csv;

import org.immutables.value.Value;

import chart.SimpleChartEntry;

@Value.Immutable
public abstract class CsvSimpleChartEntry implements SimpleChartEntry {

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
