package eg.enetty.simple_client.common.keepalive;

import eg.enetty.simple_client.common.OperationResult;
import lombok.Data;

@Data
public class KeepaliveOperationResult extends OperationResult {

    private final long time;

}
