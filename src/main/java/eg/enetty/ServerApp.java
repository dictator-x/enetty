package eg.enetty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import eg.enetty.codec.*;
import eg.enetty.handler.OrderServerProcessHandler;

public class ServerApp
{
    public static void main( String[] args ) throws InterruptedException, java.util.concurrent.ExecutionException {
        System.out.println("This is Server");

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        // If NioEventLoopGroup does not specify Executor, It will get
        // ThreadPerTaskExecutor as default.
        serverBootstrap.group(new NioEventLoopGroup());

        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());
                pipeline.addLast(new OrderServerProcessHandler());
            }
        });

        // bind method will execute eventloop
        ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();
        channelFuture.channel().closeFuture().get();
    }
}
