package eg.enetty.simple_server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.buffer.ByteBuf;

import lombok.extern.slf4j.Slf4j;

import eg.enetty.simple_server.common.*;

@Slf4j
public class OrderServerProcessHandler extends SimpleChannelInboundHandler<RequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();

        Operation operation = msg.getMessageBody();
        OperationResult operationResult = operation.execute();

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessageHeader(msg.getMessageHeader());
        responseMessage.setMessageBody(operationResult);

        if ( ctx.channel().isActive() && ctx.channel().isWritable() ) {
            ctx.writeAndFlush(responseMessage);
        } else {
            log.error("message dropped");
        }
    }

}
