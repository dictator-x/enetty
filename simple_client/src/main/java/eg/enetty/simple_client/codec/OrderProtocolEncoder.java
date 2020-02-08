package eg.enetty.simple_client.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import eg.enetty.simple_client.common.RequestMessage;

public class OrderProtocolEncoder extends MessageToMessageEncoder<RequestMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RequestMessage requestMessage, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.alloc().buffer();
        requestMessage.encode(byteBuf);

        out.add(byteBuf);
    }
}
