package eg.enetty.simple_client.codec;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import eg.enetty.simple_server.common.ResponseMessage;

public class OrderProtocolEncoder extends MessageToMessageEncoder<ResponseMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseMessage responseMessage, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.alloc().buffer();
        responseMessage.encode(byteBuf);

        out.add(byteBuf);
    }
}
