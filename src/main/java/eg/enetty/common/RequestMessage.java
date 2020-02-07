package eg.enetty.common;

public class RequestMessage extends Message<Operation> {

    public RequestMessage() {}

    @Override
    public Class getMessageBodyDecodeClass(int opcode) {
        return OperationType.fromOpCode(opcode).getOperationClass();
    }
}
