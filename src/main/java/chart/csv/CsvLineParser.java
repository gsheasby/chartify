package chart.csv;

import java.util.Optional;

// TODO handle invalid input (e.g. empty line)
public class CsvLineParser {
    public static CsvSimpleChartEntry parse(String line) {
        int numElements = 3;

        try {
            String[] split = getStrings(line, numElements);

            int pos = Integer.parseInt(split[0]);
            String title = split[1];
            String artist = split[2];
            return ImmutableCsvSimpleChartEntry.builder()
                                               .position(pos)
                                               .title(title)
                                               .artist(artist)
                                               .build();
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
            throw new RuntimeException("Failed to parse line: " + line, e);
        }
    }

    public static CsvChartEntry parseEntry(String line) {
        int numElements = 8;
        String[] split = getStrings(line, numElements);

        int pos = Integer.parseInt(split[0]);
        Optional<Integer> lastWeek = split[1].isEmpty() ? Optional.empty()
                : Optional.of(Integer.parseInt(split[1]));
        int weeks = Integer.parseInt(split[2]);
        String title = split[3];
        String artist = split[4];
        String id = split[5];
        String href = split[6];
        String uri = split[7];
        return ImmutableCsvChartEntry.builder()
                                  .position(pos)
                                  .lastPosition(lastWeek)
                                  .weeksOnChart(weeks)
                                  .title(title)
                                  .artist(artist)
                                  .id(emptyIfNull(id))
                                  .href(emptyIfNull(href))
                                  .uri(emptyIfNull(uri))
                                  .build();
    }

    private static String emptyIfNull(String str) {
        return str != null ? str : "";
    }

    //    @NotNull
    private static String[] getStrings(String line, int numElements) {
        String restOfLine = line;
        String[] split = new String[numElements];
        int i=0;
        while (!restOfLine.isEmpty()) {
            String next;
            int endIndex;
            if (restOfLine.startsWith("\"")) {
                int nextQuote = restOfLine.indexOf('"', 1);
                endIndex = restOfLine.indexOf(',', nextQuote);
                next = endIndex >= 0 ? restOfLine.substring(1, endIndex - 1)
                        : restOfLine.substring(1, nextQuote);
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
