package chart.csv;

public class CsvUtils {
    private CsvUtils() {}

    // Surround with quotes, if the string contains a comma.
    public static String sanitise(String str) {
        if (str.indexOf(',') < 0) {
            return str;
        } else {
            return "\"" + str + "\"";
        }
    }
}
