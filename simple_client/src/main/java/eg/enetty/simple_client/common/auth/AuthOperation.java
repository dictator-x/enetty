package eg.enetty.simple_client.common.auth;

import eg.enetty.simple_client.common.Operation;
import lombok.Data;

@Data
public class AuthOperation extends Operation {

    private final String userName;
    private final String password;

    @Override
    public AuthOperationResult execute() {
        if ( "admin".equalsIgnoreCase(this.userName) ) {
            AuthOperationResult authResponse = new AuthOperationResult(true);
            return authResponse;
        }

        return new AuthOperationResult(false);
    }
}
