package chart;

import org.immutables.value.Value;

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
