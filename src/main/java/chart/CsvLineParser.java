package chart;

public class CsvLineParser {
    public static SimpleChartEntry parse(String line) {
        String[] split = line.split(",");
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
