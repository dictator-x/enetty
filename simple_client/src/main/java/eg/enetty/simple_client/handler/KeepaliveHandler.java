package eg.enetty.simple_client.handler;

import io.netty.handler.timeout.IdleStateEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;

import eg.enetty.simple_client.common.keepalive.KeepaliveOperation;
import eg.enetty.simple_client.common.RequestMessage;

import eg.enetty.simple_client.util.IdUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class KeepaliveHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if ( evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT ) {
            log.info("write idle happen, so need to send keepalive operation");
            KeepaliveOperation keepaliveOperation = new KeepaliveOperation();
            RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), keepaliveOperation);
            ctx.writeAndFlush(requestMessage);
        }

        super.userEventTriggered(ctx, evt);
    }
}
