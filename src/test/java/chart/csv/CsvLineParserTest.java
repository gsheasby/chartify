package chart.csv;

import chart.ChartEntry;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class CsvLineParserTest {
    @Test
    public void parseSimpleChartLine() {
        CsvSimpleChartEntry actual = CsvLineParser.parse("1,title,artist");
        assertEquals(1, actual.position());
        assertEquals("title", actual.title());
        assertEquals("artist", actual.artist());
    }

    @Test
    public void parseLineWithComma() {
        CsvSimpleChartEntry actual = CsvLineParser.parse("1,\"title, with comma\",artist");
        assertEquals(1, actual.position());
        assertEquals("title, with comma", actual.title());
        assertEquals("artist", actual.artist());
    }

    @Test
    public void parseLineWithCommaInLastPart() {
        CsvSimpleChartEntry actual = CsvLineParser.parse("1,title,\"artist, friend\"");
        assertEquals(1, actual.position());
        assertEquals("title", actual.title());
        assertEquals("artist, friend", actual.artist());
    }

    @Test
    public void parseChartLine() {
        ChartEntry actual = CsvLineParser.parseEntry("1,2,3,title,artist");
        assertEquals(Integer.valueOf(1), actual.position());
        assertEquals(Optional.of(2), actual.lastPosition());
        assertEquals(Integer.valueOf(3), actual.weeksOnChart());
        assertEquals("title", actual.title());
        assertEquals("artist", actual.artist());
    }

    @Test
    public void parseNewEntry() {
        ChartEntry actual = CsvLineParser.parseEntry("1,,1,title,artist");
        assertEquals(Integer.valueOf(1), actual.position());
        assertEquals(Optional.empty(), actual.lastPosition());
        assertEquals(Integer.valueOf(1), actual.weeksOnChart());
        assertEquals("title", actual.title());
        assertEquals("artist", actual.artist());
    }

    @Test
    public void parseChartLineWithSpotifyInfo() {
        ChartEntry actual = CsvLineParser.parseEntry("1,,1,title,artist,id,href,uri");
        assertEquals(Integer.valueOf(1), actual.position());
        assertEquals(Optional.empty(), actual.lastPosition());
        assertEquals(Integer.valueOf(1), actual.weeksOnChart());
        assertEquals("title", actual.title());
        assertEquals("artist", actual.artist());
        assertEquals("id", actual.id());
        assertEquals("href", actual.href());
        assertEquals("uri", actual.uri());
    }
}
