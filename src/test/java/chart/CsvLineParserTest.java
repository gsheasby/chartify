package chart;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CsvLineParserTest {
    @Test
    public void parseSimpleChartLine() {
        SimpleChartEntry actual = CsvLineParser.parse("1,title,artist");
        assertEquals(1, actual.position());
        assertEquals("title", actual.title());
        assertEquals("artist", actual.artist());
    }
}
