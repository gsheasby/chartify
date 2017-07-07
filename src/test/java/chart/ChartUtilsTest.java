package chart;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

public class ChartUtilsTest {
    @Test
    public void getDate() {
        String example = "123-20170613.csv";
        DateTime expected = new DateTime(2017, 6, 13, 0, 0);
        DateTime actual = ChartUtils.getDate(example);
        assertEquals(expected, actual); // TODO assumes midnight
    }

}