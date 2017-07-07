package chart;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ChartUtils {
    public static DateTime getDate(String chartFile) {
        if (chartFile.length() < 12) {
            return DateTime.now();
        }

        // Handle only 3-digit charts
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
        return DateTime.parse(chartFile.substring(4, 12), formatter);
    }
}
