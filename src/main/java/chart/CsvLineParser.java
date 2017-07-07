package chart;

public class CsvLineParser {
    public static SimpleChartEntry parse(String line) {
        String restOfLine = line;
        String[] split = new String[3];
        int i=0;
        while (!restOfLine.isEmpty()) {
            String next;
            int endIndex;
            if (restOfLine.startsWith("\"")) {
                endIndex = restOfLine.indexOf('"', 1) + 1;
                next = restOfLine.substring(1, endIndex - 1);
            } else {
                endIndex = restOfLine.indexOf(',');
                next = endIndex > 0 ? restOfLine.substring(0, endIndex) : restOfLine;
            }
            split[i] = next;
            i++;
            restOfLine = endIndex > 0 ? restOfLine.substring(endIndex + 1) : "";
        }

        int pos = Integer.parseInt(split[0]);
        String title = split[1];
        String artist = split[2];
        return ImmutableSimpleChartEntry.builder()
                                        .position(pos)
                                        .title(title)
                                        .artist(artist)
                                        .build();
    }
}
