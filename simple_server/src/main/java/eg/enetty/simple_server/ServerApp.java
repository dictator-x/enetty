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
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;

import io.netty.util.Version;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import io.netty.util.concurrent.DefaultThreadFactory;

import eg.enetty.simple_server.codec.*;

import eg.enetty.simple_server.handler.AuthHandler;
import eg.enetty.simple_server.handler.MetricHandler;
import eg.enetty.simple_server.handler.OrderServerProcessHandler;
import eg.enetty.simple_server.handler.ServerIdleCheckHandler;

import eg.enetty.common.codec.OrderFrameEncoder;
import eg.enetty.common.codec.OrderFrameDecoder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerApp
{
    public static void main( String[] args ) throws InterruptedException, java.util.concurrent.ExecutionException {
        System.out.println("This is Server");
        log.info("Using Netty Version: " + Version.identify().entrySet());

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

        MetricHandler metricHandler = new MetricHandler();
        UnorderedThreadPoolEventExecutor businessEventExecutor = new UnorderedThreadPoolEventExecutor(10, new DefaultThreadFactory("business"));
        GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(new NioEventLoopGroup(), 100*1024*1024, 100*1024*1024);
        RuleBasedIpFilter ruleBasedIpFilter = new RuleBasedIpFilter(new IpSubnetFilterRule("127.1.0.1", 16, IpFilterRuleType.REJECT));
        AuthHandler authHandler = new AuthHandler();

        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                // Ip filer
                pipeline.addLast("IPFilter", ruleBasedIpFilter);
                // Traffic Shaping handler.
                pipeline.addLast("TSHandler", globalTrafficShapingHandler);
                // Idle check Handler
                pipeline.addLast("IdleCheckHandler", new ServerIdleCheckHandler());
                // Decode request.
                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderProtocolDecoder());
                // Encode response.
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                // print log in bytes.
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                // Metric.
                pipeline.addLast("metricHandler", metricHandler);
                // Auth Handler.
                pipeline.addLast("authHandler", authHandler);
                // flushEnhance
                pipeline.addLast("flushEnhance", new FlushConsolidationHandler(5, true));
                // User App.
                pipeline.addLast(businessEventExecutor, new OrderServerProcessHandler());
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
