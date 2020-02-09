package eg.enetty.simple_server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.logging.LogLevel;

import io.netty.util.concurrent.DefaultThreadFactory;

import eg.enetty.simple_server.codec.*;
import eg.enetty.simple_server.handler.OrderServerProcessHandler;
import eg.enetty.simple_server.handler.PipelineExecuteSequenceTestHandler;

import eg.enetty.common.codec.OrderFrameEncoder;
import eg.enetty.common.codec.OrderFrameDecoder;

public class ServerApp
{
    public static void main( String[] args ) throws InterruptedException, java.util.concurrent.ExecutionException {
        System.out.println("This is Server");

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);

        // Add log support
        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
        // If NioEventLoopGroup does not specify Executor, It will get
        // ThreadPerTaskExecutor as default.
        // Jdk selector is instanced at this point.
        serverBootstrap.group(
            new NioEventLoopGroup(0, new DefaultThreadFactory("Boss")),
            new NioEventLoopGroup(0, new DefaultThreadFactory("worker"))
        );

        serverBootstrap.childOption(NioChannelOption.TCP_NODELAY, true);
        serverBootstrap.option(NioChannelOption.SO_BACKLOG, 1024);

        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                // Decode request.
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderProtocolDecoder());
                // Encode response.
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
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
        ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();
        channelFuture.channel().closeFuture().get();

    }
}
