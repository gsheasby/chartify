package chart.tasks;

import chart.importer.AugmentingChartImporter;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.sql.SQLException;

public class ChartImporterTask {
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

        AugmentingChartImporter.idLookupImporter(TaskUtils.getConfig()).importCharts(fromWeek, toWeek);
    }

}
