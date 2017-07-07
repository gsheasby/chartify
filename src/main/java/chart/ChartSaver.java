package chart;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChartSaver {
    private final Path path;
    private final String folder;

    public ChartSaver(String folder) throws IOException {
        this.folder = folder;
        this.path = Paths.get(folder);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    public void saveChart(Chart chart) throws IOException {
        String chartFile = String.format("%03d-%04d%02d%02d.csv", chart.week(),
                                         chart.date().getYear(),
                                         chart.date().getMonthOfYear(),
                                         chart.date().getDayOfMonth());
        Path file = Files.createFile(Paths.get(folder, chartFile));
        OutputStream fileStream = Files.newOutputStream(file);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileStream));

        for (ChartEntry entry : chart.entries()) {
            String entryStr = getLine(entry);
            writer.write(entryStr);
            writer.newLine();
        }
        writer.close();
    }

    private String getLine(ChartEntry entry) {
        if (entry.lastPosition().isPresent()) {
            return String.format("%d,%d,%d,%s,%s",
                                 entry.position(),
                                 entry.lastPosition().get(),
                                 entry.weeksOnChart(), entry.title(), entry.artist());
        } else {
            return String.format("%d,,%d,%s,%s",
                                 entry.position(),
                                 entry.weeksOnChart(), entry.title(), entry.artist());
        }
    }
}
