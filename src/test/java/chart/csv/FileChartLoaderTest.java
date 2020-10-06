package chart.csv;

import chart.FileReference;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class FileChartLoaderTest {
    private static String FOLDER = "src/test/resources/charts";
    private FileChartLoader loader;

    @Before
    public void setUp() {
        loader = new FileChartLoader(FOLDER);
    }

    @Test
    public void canLoadSpecificChart() throws IOException {
        Path expected = Paths.get(FOLDER, "1.csv");

        Path path = loader.findFileForWeek(1);
        assertEquals(expected, path);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotLoadNonExistentChart() throws IOException {
        loader.findFileForWeek(1337);
    }

    @Test
    public void canLoadMostRecentChart() throws IOException {
        Path expected = Paths.get(FOLDER, "1.csv");

        FileReference latest = loader.findMostRecent();
        assertEquals(expected, latest.path());
    }
}