package eg.enetty.simple_client.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import eg.enetty.simple_client.common.RequestMessage;
import eg.enetty.simple_client.common.Operation;
import eg.enetty.simple_client.util.IdUtil;

public class OperationToRequestMessageEncoder
    extends MessageToMessageEncoder<Operation>
{
    @Override
    protected void encode(
        ChannelHandlerContext ctx,
        Operation operation,
        List<Object> out
    ) throws Exception {

        RequestMessage requestMessage = new RequestMessage(
                                            IdUtil.nextId(),
                                            operation);

        out.add(requestMessage);
    }
}
