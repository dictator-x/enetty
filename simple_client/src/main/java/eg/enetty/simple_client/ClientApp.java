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

public class ClientApp
{
    public static void main( String[] args ) throws InterruptedException, java.util.concurrent.ExecutionException {
        System.out.println("This is Server");

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);

        // Add log support
        bootstrap.handler(new LoggingHandler(LogLevel.INFO));
        // If NioEventLoopGroup does not specify Executor, It will get
        // ThreadPerTaskExecutor as default.
        // Jdk selector is instanced at this point.
        bootstrap.group(new NioEventLoopGroup());

        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());
                pipeline.addLast(new OrderServerProcessHandler());
            }
        });

        // bind method will execute eventloop
        // register ServerBootstrapAcceptor into channel pipeline
        // This will handle new Connection.
        // Flow: SingleThreadEventExecutor.doStartThread()
        //        -> SingleThreadEventExecutor.sub.run()
        //            => NioEventLoop.run()
        //        -> NioEventLoop.processSelectedKeys()
        ChannelFuture channelFuture = bootstrap.bind(8090).sync();
        channelFuture.channel().closeFuture().get();

    }
}
