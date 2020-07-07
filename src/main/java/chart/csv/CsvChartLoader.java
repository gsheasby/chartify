package chart.csv;

import chart.Chart;
import chart.FileReference;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

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
        List<FileReference> files = loader.findFiles();

        List<Chart> charts = Lists.newArrayList();
        for (FileReference chartRef : files) {
            charts.add(reader.findDerivedChart(chartRef.week()));
        }

        return charts;
    }
}
