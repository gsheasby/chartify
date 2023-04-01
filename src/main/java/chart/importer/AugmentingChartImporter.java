package chart.importer;

import chart.ChartConfig;
import chart.csv.CsvChart;
import chart.csv.FileChartReader;
import chart.postgres.PostgresChartSaver;
import chart.postgres.PostgresConnection;
import chart.postgres.PostgresConnectionManager;
import chart.spotify.IdLookupAugmentor;
import chart.spotify.SpotifyApi;
import chart.spotify.SpotifyAugmentor;
import chart.spotify.SpotifyChart;
import chart.spotify.TrackSearchingAugmentor;

import java.io.IOException;
import java.sql.SQLException;

public class AugmentingChartImporter {
    private final FileChartReader reader;
    private final PostgresChartSaver saver;
    private final SpotifyAugmentor augmentor;
    private final PostgresConnection connection;

    private AugmentingChartImporter(FileChartReader reader, PostgresChartSaver saver, SpotifyAugmentor augmentor,
                                    PostgresConnection connection) {
        this.reader = reader;
        this.saver = saver;
        this.augmentor = augmentor;
        this.connection = connection;
    }

    public static AugmentingChartImporter idLookupImporter(ChartConfig config)
            throws SQLException, ClassNotFoundException {
        FileChartReader reader = new FileChartReader(config.csvDestination());
        PostgresChartSaver saver = PostgresChartSaver.create(config.postgresConfig());
        SpotifyAugmentor augmentor = IdLookupAugmentor.create(config.spotifyConfig());

        PostgresConnectionManager manager = PostgresConnectionManager.create(config.postgresConfig());
        PostgresConnection connection = new PostgresConnection(manager);

        return new AugmentingChartImporter(reader, saver, augmentor, connection);
    }

    public static AugmentingChartImporter trackSearchingImporter(ChartConfig config)
            throws SQLException, ClassNotFoundException {
        FileChartReader reader = new FileChartReader(config.csvDestination());
        PostgresChartSaver saver = PostgresChartSaver.create(config.postgresConfig());
        SpotifyApi api = SpotifyApi.create(config.spotifyConfig());
        PostgresConnectionManager manager = PostgresConnectionManager.create(config.postgresConfig());
        PostgresConnection connection = new PostgresConnection(manager);
        SpotifyAugmentor augmentor = TrackSearchingAugmentor.create(api, connection);
        return new AugmentingChartImporter(reader, saver, augmentor, connection);
    }

    public void importCharts(int fromWeek, int toWeek) throws IOException {
        for (int week = toWeek; week >= fromWeek; week--) {
            if (connection.chartExists(week)) {
                System.out.println("Skipping week " + week + " as a chart already exists.");
                continue;
            }

            System.out.println("Importing chart " + week);
            CsvChart chart = reader.findDerivedChart(week);
            SpotifyChart spotifyChart = SpotifyChart.augment(chart, augmentor);
            System.out.println(String.format("Found chart with %d entries",
                    spotifyChart.entries().size()));
            System.out.println("Debug - not saving");
//            saver.saveChart(spotifyChart);
        }
    }
}
