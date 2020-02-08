package eg.enetty.simple_client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.logging.LogLevel;

import eg.enetty.simple_client.codec.*;
import eg.enetty.simple_client.handler.OrderServerProcessHandler;

import eg.enetty.common.codec.OrderFrameEncoder;
import eg.enetty.common.codec.OrderFrameDecoder;

import eg.enetty.simple_client.common.RequestMessage;
import eg.enetty.simple_client.util.IdUtil;
import eg.enetty.simple_client.common.order.OrderOperation;

import java.util.concurrent.ExecutionException;

public class ClientApp1
{
    public static void main( String[] args ) throws InterruptedException, ExecutionException {
        System.out.println("This is ClientApp1");

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);

        bootstrap.group(new NioEventLoopGroup());

        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());
                pipeline.addLast(new OperationToRequestMessageEncoder());
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
            }
        });


        OrderOperation operation =  new OrderOperation(1001, "lala");
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);
        channelFuture.sync();

        channelFuture.channel().writeAndFlush(operation);
        channelFuture.channel().closeFuture().get();
    }
}
