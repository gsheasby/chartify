package chart.tasks;

import chart.ChartConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class TaskUtils {
    public static ChartConfig getConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(
                new File("src/main/resources/conf/config.yml"),
                ChartConfig.class);
    }
}
