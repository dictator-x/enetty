package eg.enetty.simple_server.common.order;

import eg.enetty.simple_server.common.Operation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class OrderOperation extends Operation {

    private int tableId;
    private String dish;

    public OrderOperation(int tableId, String dish) {
        this.tableId = tableId;
        this.dish = dish;
    }

    @Override
    public OrderOperationResult execute() {
        log.info("order's executing with orderRequest: " + toString());
        log.info("order's executing complete");
        return new OrderOperationResult(tableId, dish, true);
    }
}
