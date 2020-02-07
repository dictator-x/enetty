package eg.enetty.common;

import eg.enetty.common.order.OrderOperation;
import eg.enetty.common.order.OrderOperationResult;
import eg.enetty.common.keepalive.KeepaliveOperation;
import eg.enetty.common.keepalive.KeepaliveOperationResult;
import eg.enetty.common.auth.AuthOperation;
import eg.enetty.common.auth.AuthOperationResult;

import java.util.function.Predicate;

public enum OperationType {

    AUTH(1, AuthOperation.class, AuthOperationResult.class),
    KEEPALIVE(2, KeepaliveOperation.class, KeepaliveOperationResult.class),
    ORDER(3, OrderOperation.class, OrderOperationResult.class);

    private int opCode;
    private Class<? extends Operation> operationClass;
    private Class<? extends OperationResult> operationResultClass;

    OperationType(
        int opCode,
        Class<? extends Operation> operationClass,
        Class<? extends OperationResult> operationResultClass
    ) {
        this.opCode               = opCode;
        this.operationClass       = operationClass;
        this.operationResultClass = operationResultClass;
    }

    public int getOpCode() {
        return opCode;
    }

    public Class<? extends Operation> getOperationClass() {
        return operationClass;
    }

    public Class<? extends OperationResult> getOperationResultClas() {
        return operationResultClass;
    }

    public static OperationType fromOpCode(int type) {
        return getOperationType(requestType -> requestType.opCode == type);
    }

    public static OperationType fromOperation(Operation operation) {
        return getOperationType(
            requestType -> requestType.operationClass == operation.getClass()
        );
    }

    public static OperationType getOperationType(Predicate<OperationType> predicate) {
        OperationType[] values = values();
        for ( OperationType operationType : values ) {
            if ( predicate.test(operationType) ) {
                return operationType;
            }
        }

        throw new AssertionError("no found type");
    }
}
