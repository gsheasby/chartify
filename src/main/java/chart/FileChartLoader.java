package chart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileChartLoader {
    private String folder;

    public FileChartLoader(String folder) {
        this.folder = folder;
    }

    public Path findFileForWeek(int week) throws IOException {
        Path path = Paths.get(folder);
        String weekStr = Integer.toString(week);
        List<Path> chartFiles = Files.walk(path)
                                     .filter(file -> file.getFileName().toString().startsWith(weekStr))
                                     .collect(Collectors.toList());
        if (chartFiles.isEmpty()) {
            throw new IllegalArgumentException("Chart for week " + week + " not found!");
        }

        return chartFiles.get(0);
    }

    public Path findMostRecent() throws IOException {
        List<Path> chartFiles = Files.walk(Paths.get(folder), 1)
                                     .filter(path -> path.getFileName().toString().endsWith(".csv"))
                                     .collect(Collectors.toList());

        int latestWeek = -1;
        Path latest = null;
        for (Path path : chartFiles) {
            String fileName = path.getFileName().toString();
            int week = Integer.parseInt(fileName.split("[-.]")[0]);
            if (week > latestWeek) {
                latestWeek = week;
                latest = path;
            }
        }

        return latest;
    }
}
