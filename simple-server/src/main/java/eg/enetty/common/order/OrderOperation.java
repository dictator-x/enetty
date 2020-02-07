package eg.enetty.common.order;

import eg.enetty.common.Operation;
import lombok.Data;

@Data
public class OrderOperation extends Operation {

    private int tableId;
    private String dish;

    public OrderOperation(int tableId, String dish) {
        this.tableId = tableId;
        this.dish = dish;
    }

    @Override
    public OrderOperationResult execute() {
        System.out.println("order's executing with orderRequest: " + toString());
        System.out.println("order's executing complete");
        return new OrderOperationResult(tableId, dish, true);
    }
}
