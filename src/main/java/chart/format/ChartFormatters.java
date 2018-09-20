package chart.format;

public class ChartFormatters {
    private ChartFormatters() {
        // factory class
    }

    public static ChartFormatter preview() {
        return new PlainTextChartFormatter(true);
    }

    public static ChartFormatter forum() {
        return new BbCodeChartFormatter(new PlainTextChartFormatter(false));
    }
}
