package chart;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

public class CsvLineParser {
    public static SimpleChartEntry parse(String line) {
        int numElements = 3;
        String[] split = getStrings(line, numElements);

        int pos = Integer.parseInt(split[0]);
        String title = split[1];
        String artist = split[2];
        return ImmutableSimpleChartEntry.builder()
                                        .position(pos)
                                        .title(title)
                                        .artist(artist)
                                        .build();
    }

    public static ChartEntry parseEntry(String line) {
        int numElements = 5;
        String[] split = getStrings(line, numElements);

        int pos = Integer.parseInt(split[0]);
        Optional<Integer> lastWeek = split[1].isEmpty() ? Optional.empty()
                : Optional.of(Integer.parseInt(split[1]));
        int weeks = Integer.parseInt(split[2]);
        String title = split[3];
        String artist = split[4];
        return ImmutableChartEntry.builder()
                                  .position(pos)
                                  .lastPosition(lastWeek)
                                  .weeksOnChart(weeks)
                                  .title(title)
                                  .artist(artist)
                                  .build();
    }

    @NotNull
    private static String[] getStrings(String line, int numElements) {
        String restOfLine = line;
        String[] split = new String[numElements];
        int i=0;
        while (!restOfLine.isEmpty()) {
            String next;
            int endIndex;
            if (restOfLine.startsWith("\"")) {
                endIndex = restOfLine.indexOf('"', 1) + 1;
                next = restOfLine.substring(1, endIndex - 1);
            } else {
                endIndex = restOfLine.indexOf(',');
                next = endIndex >= 0 ? restOfLine.substring(0, endIndex) : restOfLine;
            }
            split[i] = next;
            i++;
            restOfLine = endIndex >= 0 ? restOfLine.substring(endIndex + 1) : "";
        }
        return split;
    }

}
