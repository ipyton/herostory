

import cmdHandler.CmdHandlerFactory;
import com.sun.corba.se.impl.activation.ServerMain;
import handlersInPipeline.GameMsgDecoder;
import handlersInPipeline.GameMsgEncoder;
import handlersInPipeline.GameMsgHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GameMessageRecognizer;
import utils.MySQLFactory;
import utils.RedisUtil;

public class Application {

    static private final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        PropertyConfigurator.configure( Application.class.getResource("log4j.properties"));
        init();
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.group(boss, worker);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpServerCodec(), // handle get method
                new HttpObjectAggregator(65535), // handle the post method
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

    public static void init(){
        RedisUtil.init();
        GameMessageRecognizer.init();
        MySQLFactory.init();
        CmdHandlerFactory.init();
        System.out.println("init done!!!");
    }
}
