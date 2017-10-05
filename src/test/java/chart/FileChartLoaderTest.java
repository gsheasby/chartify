package chart;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class FileChartLoaderTest {
    private static String FOLDER = "src/test/resources";
    private FileChartLoader loader;

    @Before
    public void setUp() throws Exception {
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

}