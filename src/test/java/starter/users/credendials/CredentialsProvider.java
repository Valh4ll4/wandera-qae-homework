package starter.users.credendials;

import java.io.IOException;
import java.nio.file.Path;

public interface CredentialsProvider
{
    Credentials getCredentials(Path credentialsPath) throws IOException;

    Credentials getCredentials(String credentialsPath) throws IOException;
}
