package chart;

import java.io.IOException;
import java.util.function.Consumer;

import chart.csv.CsvChartSaver;
import chart.postgres.PostgresChartSaver;
import chart.spotify.SpotifyChart;

public class DualChartSaver implements ChartSaver<SpotifyChart> {
    private final CsvChartSaver csvSaver;
    private final PostgresChartSaver postgresSaver;

    public DualChartSaver(CsvChartSaver csvSaver, PostgresChartSaver postgresSaver) {
        this.csvSaver = csvSaver;
        this.postgresSaver = postgresSaver;
    }

    @Override
    public void saveChart(SpotifyChart chart) {
        saveChart(chart, "CSV", saveCsvChartThrowingUnchecked());
        saveChart(chart, "Postgres", postgresSaver::saveChart);
    }

    private Consumer<SpotifyChart> saveCsvChartThrowingUnchecked() {
        return chart -> {
                try {
                    csvSaver.saveChart(chart);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
    }

    private void saveChart(SpotifyChart chart, String type, Consumer<SpotifyChart> chartConsumer) {
        try {
            System.out.println("Saving chart to " + type + "...");
            chartConsumer.accept(chart);
            System.out.println("Finished saving to " + type + ".");
        } catch (Exception e) {
            System.err.println(type + " Chart saving failed! " + e.getMessage());
            e.printStackTrace();
        }
    }
}
