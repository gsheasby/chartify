package chart.csv;

import java.util.List;

import org.immutables.value.Value;

import chart.SimpleChart;

@Value.Immutable
public abstract class CsvSimpleChart implements SimpleChart {
    @Override
    public abstract List<CsvSimpleChartEntry> entries();
}
