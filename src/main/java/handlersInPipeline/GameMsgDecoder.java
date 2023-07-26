package handlersInPipeline;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.GameMessageRecognizer;

//this class has potential bugs
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

            int kkk = byteBuf.readShort();//读取消息长度
            //LOGGER.error(kkk+ " ---------------bdsifgubaesrilugbaeswrg");
            int msgCode = byteBuf.readShort();
            //LOGGER.error(msgCode+ "-- " + byteBuf.writerIndex() + "--" + byteBuf.readerIndex());
            byte[] msgBody = new byte[byteBuf.readableBytes()]; // this place will cause bug
            byteBuf.readBytes(msgBody);

            //消息构造器
            Message.Builder builder = GameMessageRecognizer.getBuilderByMsgCode(msgCode);
            builder.clear();
            builder.mergeFrom(msgBody);
            //System.out.println(msgBody);

            //构造消息实体
            Message cmd = builder.build();

            System.out.println(cmd);

            if (null != cmd) {
                ctx.fireChannelRead(cmd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
