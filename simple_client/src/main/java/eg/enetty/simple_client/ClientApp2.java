package eg.enetty.simple_client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.logging.LogLevel;

import eg.enetty.simple_client.codec.*;

import eg.enetty.common.codec.OrderFrameEncoder;
import eg.enetty.common.codec.OrderFrameDecoder;

import eg.enetty.simple_client.common.RequestMessage;
import eg.enetty.simple_client.common.order.OrderOperation;

import eg.enetty.simple_client.util.IdUtil;

import eg.enetty.simple_client.dispatcher.OperationResultFuture;
import eg.enetty.simple_client.dispatcher.RequestPendingCenter;
import eg.enetty.simple_client.dispatcher.ResponseDispatcherHandler;

import java.util.concurrent.ExecutionException;

public class ClientApp2
{
    public static void main( String[] args ) throws InterruptedException, ExecutionException {
        System.out.println("This is ClientApp2");

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);

        bootstrap.group(new NioEventLoopGroup());

        bootstrap.option(NioChannelOption.CONNECT_TIMEOUT_MILLIS, 10*1000);

        RequestPendingCenter requestPendingCenter = new RequestPendingCenter();

        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());

                pipeline.addLast(new ResponseDispatcherHandler(requestPendingCenter));
                pipeline.addLast(new OperationToRequestMessageEncoder());

                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
            }
        });


        long streamId = IdUtil.nextId();
        OrderOperation operation =  new OrderOperation(1001, "lala");
        RequestMessage requestMessage = new RequestMessage(streamId, operation);

        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);
        channelFuture.sync();

        // Add into RequestPendingCenter.
        OperationResultFuture future = new OperationResultFuture();
        requestPendingCenter.add(streamId, future);

        channelFuture.channel().writeAndFlush(requestMessage);

        System.out.println("result:" + future.get());

        channelFuture.channel().closeFuture().get();
    }
}
