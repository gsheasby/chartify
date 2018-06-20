package chart.csv;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;

import chart.Chart;
import javafx.util.Pair;

public class CsvChartLoader {
    private final String folder;

    public CsvChartLoader(String folder) {
        this.folder = folder;
    }

    public List<Chart> loadAll() throws IOException {
        // TODO these things should probably be injected
        FileChartLoader loader = new FileChartLoader(folder);
        FileChartReader reader = new FileChartReader(folder);

        // TODO this is a bit weird - we get all the paths but only use the week numbers
        List<Pair<Integer, Path>> files = loader.findFiles();

        List<Chart> charts = Lists.newArrayList();
        for (Pair<Integer, Path> chartRef : files) {
            charts.add(reader.findDerivedChart(chartRef.getKey()));
        }

        return charts;
    }
}
