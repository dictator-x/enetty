package eg.enetty.simple_server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import eg.enetty.simple_server.common.Operation;
import eg.enetty.simple_server.common.RequestMessage;
import eg.enetty.simple_server.common.auth.AuthOperation;
import eg.enetty.simple_server.common.auth.AuthOperationResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<RequestMessage> {

    public void channelRead0(ChannelHandlerContext ctx, RequestMessage requestMessage) throws Exception {
        try {
            Operation operation = requestMessage.getMessageBody();
            if ( operation instanceof AuthOperation ) {
                AuthOperation authOperation = AuthOperation.class.cast(operation);
                AuthOperationResult authOperationResult = authOperation.execute();
                if ( authOperationResult.isPassAuth() ) {
                    log.info("pass auth");
                } else {
                    log.error("Fail to auth");
                    ctx.close();
                }
            } else {
                log.error("Auth fail");
                ctx.close();
            }
        } catch ( Exception e ) {
            log.error("exception catch");
            ctx.close();
        } finally {
            ctx.pipeline().remove(this);
        }

    }
}
