package starter.users.credendials;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Slf4j
public class CredentialsProviderFileImpl implements CredentialsProvider
{
    private static final ObjectMapper JSON_PARSER = new ObjectMapper();

    @Override
    public Credentials getCredentials(Path credentialsPath) throws IOException {
        return getCredentials(credentialsPath.toString());
    }

    @Override
    public Credentials getCredentials(String credentialsPath) throws IOException {
        Credentials credentials;
        InputStream credentialsFileInput = getClass().getClassLoader().getResourceAsStream(credentialsPath);
        if (credentialsFileInput == null) {
            log.error("Provided path to credentials {} does not exist.", credentialsPath);
            throw new IllegalArgumentException(String.format("File not found: '%s'", credentialsPath));
        }
        credentials = JSON_PARSER.readValue(credentialsFileInput, Credentials.class);
        return credentials;
    }
}
