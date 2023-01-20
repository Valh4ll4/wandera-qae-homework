package starter.starter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ConfigProvider
{
    private static final ObjectMapper JSON_PARSER = new ObjectMapper();

    public Configuration getConfiguration(String configPath) throws IOException {
        Configuration config;
        InputStream configFileInput = getClass().getClassLoader().getResourceAsStream(configPath);
        if (configFileInput == null) {
            log.error("Provided path to config {} does not exist.", configPath);
            throw new IllegalArgumentException(String.format("File not found: '%s'", configPath));
        }
        config = JSON_PARSER.readValue(configFileInput, Configuration.class);
        return config;
    }
}
