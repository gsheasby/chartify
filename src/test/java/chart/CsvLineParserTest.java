package chart;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

public class CsvLineParserTest {
    @Test
    public void parseSimpleChartLine() {
        SimpleChartEntry actual = CsvLineParser.parse("1,title,artist");
        assertEquals(1, actual.position());
        assertEquals("title", actual.title());
        assertEquals("artist", actual.artist());
    }

    @Test
    public void parseLineWithComma() {
        SimpleChartEntry actual = CsvLineParser.parse("1,\"title, with comma\",artist");
        assertEquals(1, actual.position());
        assertEquals("title, with comma", actual.title());
        assertEquals("artist", actual.artist());
    }

    @Test
    public void parseLineWithCommaInLastPart() {
        SimpleChartEntry actual = CsvLineParser.parse("1,title,\"artist, friend\"");
        assertEquals(1, actual.position());
        assertEquals("title", actual.title());
        assertEquals("artist, friend", actual.artist());
    }

    @Test
    public void parseChartLine() {
        ChartEntry actual = CsvLineParser.parseEntry("1,2,3,title,artist");
        assertEquals(new Integer(1), actual.position());
        assertEquals(Optional.of(2), actual.lastPosition());
        assertEquals(new Integer(3), actual.weeksOnChart());
        assertEquals("title", actual.title());
        assertEquals("artist", actual.artist());
    }

    @Test
    public void parseNewEntry() {
        ChartEntry actual = CsvLineParser.parseEntry("1,,1,title,artist");
        assertEquals(new Integer(1), actual.position());
        assertEquals(Optional.empty(), actual.lastPosition());
        assertEquals(new Integer(1), actual.weeksOnChart());
        assertEquals("title", actual.title());
        assertEquals("artist", actual.artist());
    }

    @Test
    public void parseChartLineWithSpotifyInfo() {
        ChartEntry actual = CsvLineParser.parseEntry("1,,1,title,artist,id,href,uri");
        assertEquals(new Integer(1), actual.position());
        assertEquals(Optional.empty(), actual.lastPosition());
        assertEquals(new Integer(1), actual.weeksOnChart());
        assertEquals("title", actual.title());
        assertEquals("artist", actual.artist());
        assertEquals("id", actual.id());
        assertEquals("href", actual.href());
        assertEquals("uri", actual.uri());
    }
}
