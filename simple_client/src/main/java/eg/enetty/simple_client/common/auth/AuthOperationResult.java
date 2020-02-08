package eg.enetty.simple_client.common.auth;

import eg.enetty.simple_client.common.OperationResult;
import lombok.Data;

@Data
public class AuthOperationResult extends OperationResult {

    private final boolean passAuth;

}
