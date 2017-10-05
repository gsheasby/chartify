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
import chart.ChartSaver;
import chart.FileChartReader;
import chart.SimpleChartReader;
import chart.SpotifyChartReader;

public class ChartCompilationTask {

    public static void main(String[] args) throws IOException {
        int week = Integer.parseInt(args[0]);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ChartConfig config = mapper.readValue(
                new File("src/main/resources/conf/config.yml"),
                ChartConfig.class);

        SimpleChartReader reader = new SpotifyChartReader(config);
        ChartReader derivedReader = new FileChartReader(config.csvDestination());
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart(week);

        new ChartSaver(config.csvDestination()).saveChart(chart);

        ChartPrinter.print(chart);
    }
}
