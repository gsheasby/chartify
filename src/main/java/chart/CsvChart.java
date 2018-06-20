package chart;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public abstract class CsvChart implements Chart {
    @Override
    public abstract List<CsvChartEntry> entries();

    @Override
    public abstract List<CsvChartEntry> dropouts();
}
