package eg.enetty.common.keepalive;

import eg.enetty.common.OperationResult;
import lombok.Data;

@Data
public class KeepaliveOperationResult extends OperationResult {

    private final long time;

}
