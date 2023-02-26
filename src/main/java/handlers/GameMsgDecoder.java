package handlers;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import message.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

        if (!(msg instanceof BinaryWebSocketFrame)) return;

        try {
            BinaryWebSocketFrame inputFrame = (BinaryWebSocketFrame) msg;
            ByteBuf byteBuf = inputFrame.content();

            byteBuf.readShort();
            int msgCode = byteBuf.readShort();

            byte[] msgBody = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(msgBody);

            GeneratedMessageV3 cmd = null;
            switch (msgCode) {
                case GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE:
                    cmd = GameMsgProtocol.UserEntryCmd.parseFrom(msgBody);
                    break;
                case GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE:
                    cmd = GameMsgProtocol.WhoElseIsHereCmd.parseFrom(msgBody);
                    break;
                default:
                    break;
            }

            if (null != cmd) {
                ctx.fireChannelRead(cmd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
