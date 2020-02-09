package eg.enetty.simple_server.common.order;

import eg.enetty.simple_server.common.OperationResult;
import lombok.Data;

@Data
public class OrderOperationResult extends OperationResult {

    private final int tableId;
    private final String dish;
    private final boolean complete;

}
