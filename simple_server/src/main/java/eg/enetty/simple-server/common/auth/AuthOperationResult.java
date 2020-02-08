package eg.enetty.simple_server.common.auth;

import eg.enetty.simple_server.common.OperationResult;
import lombok.Data;

@Data
public class AuthOperationResult extends OperationResult {

    private final boolean passAuth;

}
