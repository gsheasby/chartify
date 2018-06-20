package chart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;

import chart.csv.CsvLineParser;
import chart.csv.CsvSimpleChart;
import chart.csv.CsvSimpleChartEntry;
import chart.csv.ImmutableCsvSimpleChart;
import javafx.util.Pair;

public class FileChartReader implements ChartReader<CsvChart, CsvSimpleChart> {
    private final FileChartLoader fileChartLoader;

    public FileChartReader(String folder) {
        fileChartLoader = new FileChartLoader(folder);
    }

    @Override
    public CsvChart findLatestChart() throws IOException {
        Pair<Integer, Path> mostRecent = fileChartLoader.findMostRecent();
        int week = mostRecent.getKey();
        Path chartPath = mostRecent.getValue();
        return findDerivedChart(week, chartPath);
    }

    @Override
    public CsvChart findDerivedChart(int week) throws IOException {
        Path chartPath = fileChartLoader.findFileForWeek(week);
        return findDerivedChart(week, chartPath);
    }

    private CsvChart findDerivedChart(int week, Path chartPath) throws IOException {
        DateTime chartDate = ChartUtils.getDate(chartPath.getFileName().toString());
        Stream<String> lines = Files.lines(chartPath);
        List<CsvChartEntry> entries = lines.map(CsvLineParser::parseEntry).collect(Collectors.toList());
        return ImmutableCsvChart.builder()
                             .week(week)
                             .date(chartDate)
                             .entries(entries)
                             .build();
    }

    @Override
    public CsvSimpleChart findChart(int week) throws IOException {
        Path chartPath = fileChartLoader.findFileForWeek(week);
        DateTime chartDate = ChartUtils.getDate(chartPath.getFileName().toString());
        Stream<String> lines = Files.lines(chartPath);
        List<CsvSimpleChartEntry> entries = lines.map(CsvLineParser::parse).collect(Collectors.toList());
        return ImmutableCsvSimpleChart.builder()
                                      .week(week)
                                      .date(chartDate)
                                      .entries(entries)
                                      .build();
    }
}
