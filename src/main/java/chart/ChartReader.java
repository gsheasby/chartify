package chart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChartReader {
    public static final String FOLDER = "src/main/resources";
    private final int week;

    public ChartReader(int week) {
        this.week = week;
    }

    public boolean findChart() throws IOException {
        Path folder = Paths.get(FOLDER);
        String weekStr = Integer.toString(week);
        long count = Files.walk(folder)
                          .filter(file -> file.getFileName().toString().startsWith(weekStr))
                          .count();
        return count > 0;
    }
}
