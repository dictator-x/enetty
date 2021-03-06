package eg.enetty.simple_client.common;

public class ResponseMessage extends Message<OperationResult> {

    public ResponseMessage() {}

    @Override
    public Class getMessageBodyDecodeClass(int opcode) {
        return OperationType.fromOpCode(opcode).getOperationResultClass();
    }
}
