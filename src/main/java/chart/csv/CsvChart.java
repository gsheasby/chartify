package chart.csv;

import java.util.List;

import org.immutables.value.Value;

import chart.Chart;

@Value.Immutable
public abstract class CsvChart implements Chart {
    @Override
    public abstract List<CsvChartEntry> entries();

    @Override
    public abstract List<CsvChartEntry> dropouts();
}
