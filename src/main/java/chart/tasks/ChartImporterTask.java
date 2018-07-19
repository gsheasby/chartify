package chart.tasks;

import java.io.IOException;
import java.sql.SQLException;

import com.google.common.base.Preconditions;

import chart.ChartConfig;
import chart.csv.CsvChart;
import chart.csv.FileChartReader;
import chart.postgres.PostgresChartSaver;
import chart.spotify.SpotifyAugmentor;
import chart.spotify.SpotifyChart;

public class ChartImporterTask {
    // TODO - augment youtube-based tracks
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        if (args.length < 2) {
            System.out.println("Usage: ChartImporterTask <fromWeek> <toWeek>");
            System.exit(1);
        }
        int fromWeek = Integer.parseInt(args[0]);
        int toWeek = Integer.parseInt(args[1]);
        Preconditions.checkArgument(fromWeek > 0, "fromWeek must be a positive integer");
        Preconditions.checkArgument(toWeek > 0, "toWeek must be a positive integer");
        Preconditions.checkArgument(fromWeek <= toWeek, "toWeek must be at least fromWeek");

        ChartConfig config = TaskUtils.getConfig();
        FileChartReader reader = new FileChartReader(config.csvDestination());
        PostgresChartSaver saver = PostgresChartSaver.create(config.postgresConfig());
        SpotifyAugmentor augmentor = SpotifyAugmentor.create(config.spotifyConfig());

        for (int week = fromWeek; week <= toWeek; week++) {
            System.out.println("Importing chart " + week);
            CsvChart chart = reader.findDerivedChart(week);
            SpotifyChart spotifyChart = SpotifyChart.augment(chart, augmentor);
            saver.saveChart(spotifyChart);
        }
    }
}
