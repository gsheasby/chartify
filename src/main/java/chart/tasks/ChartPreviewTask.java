package chart.tasks;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import chart.Chart;
import chart.ChartCompiler;
import chart.ChartConfig;
import chart.ChartPrinter;
import chart.ChartReader;
import chart.FileChartReader;
import chart.SpotifyChartReader;

public class ChartPreviewTask {
    public static void main(String[] args) throws IOException {

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ChartConfig config = mapper.readValue(
                new File("src/main/resources/conf/config.yml"),
                ChartConfig.class);

        ChartReader reader = new SpotifyChartReader(config);
        ChartReader derivedReader = new FileChartReader(config.csvDestination());
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart();

        ChartPrinter.print(chart);
    }
}
