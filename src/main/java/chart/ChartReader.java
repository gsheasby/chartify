package chart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChartReader {
    public static final String FOLDER = "src/main/resources";
    private final int week;

    public ChartReader(int week) {
        this.week = week;
    }

    public boolean findChart() throws IOException {
        Path folder = Paths.get(FOLDER);
        String weekStr = Integer.toString(week);
        List<Path> chartFiles = Files.walk(folder)
                                  .filter(file -> file.getFileName().toString().startsWith(weekStr))
                                  .collect(Collectors.toList());
        if (chartFiles.isEmpty()) {
            throw new IllegalArgumentException("Chart for week " + week + " not found!");
        }

        Stream<String> lines = Files.lines(chartFiles.get(0));
        lines.forEach(System.out::println);
        return chartFiles.size() > 0;
    }
}
