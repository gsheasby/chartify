package chart.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class FileChartLoader {
    private String folder;

    public FileChartLoader(String folder) {
        this.folder = folder;
    }

    public List<Pair<Integer, Path>> findFiles() throws IOException {
        Path path = Paths.get(folder);
        List<Path> chartFiles = Files.walk(path)
                                     .filter(file -> file.getFileName().toString().endsWith(".csv"))
                                     .collect(Collectors.toList());
        return chartFiles.stream().map(file -> new Pair<>(getWeek(file), file)).collect(Collectors.toList());
    }

    private int getWeek(Path path) {
        return Integer.parseInt(path.getFileName().toString().split("[-.]")[0]);
    }

    public Path findFileForWeek(int week) throws IOException {
        List<Path> chartFiles = Files.walk(Paths.get(folder))
                                     .filter(file -> file.getFileName().toString().endsWith(".csv")
                                             && getWeek(file) == week)
                                     .collect(Collectors.toList());
        if (chartFiles.isEmpty()) {
            throw new IllegalArgumentException("Chart for week " + week + " not found!");
        }

        return chartFiles.get(0);
    }

    public Pair<Integer, Path> findMostRecent() throws IOException {
        List<Path> chartFiles = Files.walk(Paths.get(folder), 1)
                                     .filter(path -> path.getFileName().toString().endsWith(".csv"))
                                     .collect(Collectors.toList());

        Integer latestWeek = -1;
        Path latest = null;
        for (Path path : chartFiles) {
            String fileName = path.getFileName().toString();
            Integer week = Integer.parseInt(fileName.split("[-.]")[0]);
            if (week > latestWeek) {
                latestWeek = week;
                latest = path;
            }
        }

        return new Pair<>(latestWeek, latest);
    }
}
