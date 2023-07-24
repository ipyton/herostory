package handlers;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import message.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.BroadCaster;
import entities.UserManager;
import utils.MainMsgProcessor;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (null == ctx) {
            return;
        }

        try {
            super.channelActive(ctx);
            BroadCaster.addChannel(ctx.channel());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (ctx == null) return;
        try {
            super.handlerRemoved(ctx);
            Integer userID = (Integer) ctx.channel().attr(AttributeKey.valueOf("userID")).get();
            if (null == userID) return;
            UserManager.removeByUserId(userID);
            BroadCaster.removeChannel(ctx.channel());

            GameMsgProtocol.UserQuitResult.Builder builder = GameMsgProtocol.UserQuitResult.newBuilder();
            builder.setQuitUserId(userID);

            GameMsgProtocol.UserQuitResult result = builder.build();
            BroadCaster.broadcast(result);

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    //previous version handler and
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

//        LOGGER.info(msg.getClass().getSimpleName(),msg);
//        try {
//            ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msg.getClass());
//
//            if(null != cmdHandler) {
//                cmdHandler.handle(ctx, cast(msg));
//            }
//        } catch (Exception ex) {
//            LOGGER.error(ex.getMessage(), ex);
//        }
        MainMsgProcessor.getInstance().process(ctx, msg);
    }
    // the first T stands for type of T, the second T means return T
    static private <T extends GeneratedMessageV3> T cast(Object msg){
        if (null == msg) {
            return null;
        } else {
            return (T) msg;
        }
    }
}
