

import handlers.GameMsgDecoder;
import handlers.GameMsgEncoder;
import handlers.GameMsgHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    static private final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.group(boss, worker);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpServerCodec(),
                new HttpObjectAggregator(65535),
                new WebSocketServerProtocolHandler("/websocket"), //处理握手信息
                new GameMsgEncoder(),
                new GameMsgDecoder(),
                new GameMsgHandler());
            }
        });

        try{
            ChannelFuture f = serverBootstrap.bind(12345).sync();
            if (f.isSuccess()) {
                LOGGER.info("success!");

            }
            f.channel().closeFuture().sync();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}
