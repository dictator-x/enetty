package eg.enetty.simple_server.common.keepalive;

import eg.enetty.simple_server.common.OperationResult;
import lombok.Data;

@Data
public class KeepaliveOperationResult extends OperationResult {

    private final long time;

}
