package chart;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChartReader {
    private final int week;

    public ChartReader(int week) {
        this.week = week;
        System.out.println("It's week " + week + "!");
    }

    public boolean findChart() {
        Path path = Paths.get("src/main/resources", week + "-20170625.csv");
        return Files.exists(path);
    }
}
