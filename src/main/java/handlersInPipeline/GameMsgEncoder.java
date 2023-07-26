package handlersInPipeline;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import message.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GameMessageRecognizer;

public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);

    //
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

        try {
            if (!(msg instanceof GeneratedMessageV3)) {
                super.write(ctx, msg, promise);
                return;
            }

            int msgCode = GameMessageRecognizer.getMsgCodeByClazz(msg.getClass());

            if (-1 == msgCode) {
                LOGGER.error("can not get the right message" + msg.getClass().getSimpleName());
                super.write(ctx, msg, promise);
                return;
            }
            byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

            ByteBuf byteBuf = ctx.alloc().buffer();
            byteBuf.writeShort((short) msgBody.length);
            byteBuf.writeShort((short) msgCode);
            byteBuf.writeBytes(msgBody);

            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(byteBuf);
            super.write(ctx, outputFrame, promise);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }


}
