package chart.tasks;

import chart.ChartConfig;
import chart.importer.AugmentingChartImporter;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.sql.SQLException;

public class ChartImporterTask {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        if (args.length < 2) {
            System.out.println("Usage: ChartImporterTask <fromWeek> <toWeek>");
            System.out.println("To use postgres search: ChartImporterTask <fromWeek> <toWeek> postgres");
            System.exit(1);
        }
        int fromWeek = Integer.parseInt(args[0]);
        int toWeek = Integer.parseInt(args[1]);
        boolean useTrackSearcher = shouldUseTrackSearcher(args);

        Preconditions.checkArgument(fromWeek > 0, "fromWeek must be a positive integer");
        Preconditions.checkArgument(toWeek > 0, "toWeek must be a positive integer");
        Preconditions.checkArgument(fromWeek <= toWeek, "toWeek must be at least fromWeek");

        ChartConfig config = TaskUtils.getConfig();
        AugmentingChartImporter importer = useTrackSearcher
                ? AugmentingChartImporter.trackSearchingImporter(config)
                : AugmentingChartImporter.idLookupImporter(config);
        importer.importCharts(fromWeek, toWeek);
    }

    private static boolean shouldUseTrackSearcher(String[] args) {
        if (args.length < 3) {
            System.out.println("Defaulting to IdLookupImporter");
            return false;
        }

        String importerToUse = args[2];
        if (importerToUse.equalsIgnoreCase("postgres")) {
            System.out.println("Using TrackSearchingImporter");
            return true;
        } else {
            System.out.println(String.format(
                    "Couldn't match importer to %s - defaulting to IdLookupImporter",
                    importerToUse));
            return false;
        }
    }

}
