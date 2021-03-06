package eg.enetty.simple_server.common;

import io.netty.buffer.ByteBuf;
import java.nio.charset.Charset;
import eg.enetty.simple_server.util.JsonUtil;

import lombok.Data;

@Data
public abstract class Message<T extends MessageBody> {

    private MessageHeader messageHeader;
    private T messageBody;

    public void encode(ByteBuf byteBuf) {
        byteBuf.writeInt(messageHeader.getVersion());
        byteBuf.writeLong(messageHeader.getStreamId());
        byteBuf.writeInt(messageHeader.getOpCode());
        byteBuf.writeBytes(JsonUtil.toJson(messageBody).getBytes());
    }

    public abstract Class<T> getMessageBodyDecodeClass(int opcode);

    public void decode(ByteBuf byteBuf) {

        // Order sensitive.
        int version   = byteBuf.readInt();
        long streamId = byteBuf.readLong();
        int opCode    = byteBuf.readInt();

        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setVersion(version);
        messageHeader.setStreamId(streamId);
        messageHeader.setOpCode(opCode);
        this.messageHeader = messageHeader;

        Class<T> bodyClazz = getMessageBodyDecodeClass(opCode);
        T body = JsonUtil.fromJson(byteBuf.toString(Charset.forName("UTF-8")), bodyClazz);
        this.messageBody = body;
    }
}
