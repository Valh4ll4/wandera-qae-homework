package starter.users.credendials;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Value
public class Credentials
{
    @NonNull String username;
    @NonNull String password;

}
