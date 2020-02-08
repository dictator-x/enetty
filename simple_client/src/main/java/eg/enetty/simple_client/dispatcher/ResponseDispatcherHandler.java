package eg.enetty.simple_client.dispatcher;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import eg.enetty.simple_client.common.ResponseMessage;

public class ResponseDispatcherHandler
    extends SimpleChannelInboundHandler<ResponseMessage>
{

    RequestPendingCenter center;

    @Override
    protected void channelRead0(
        ChannelHandlerContext ctx,
        ResponseMessage responseMessage
    ) throws Exception {

        center.set(
            responseMessage.getMessageHeader().getStreamId(),
            responseMessage.getMessageBody()
        );
    }
}
