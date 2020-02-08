package eg.enetty.simple_client.common.keepalive;

import eg.enetty.simple_client.common.Operation;
import lombok.Data;

@Data
public class KeepaliveOperation extends Operation {

    private long time;

    public KeepaliveOperation() {
        this.time = System.nanoTime();
    }

    @Override
    public KeepaliveOperationResult execute() {
        return new KeepaliveOperationResult(time);
    }
}
