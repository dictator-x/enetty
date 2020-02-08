package eg.enetty.simple_server.common;

public class ResponseMessage extends Message<OperationResult> {

    public ResponseMessage() {}

    @Override
    public Class getMessageBodyDecodeClass(int opcode) {
        return OperationType.fromOpCode(opcode).getOperationClass();
    }
}
