import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class GameMessageHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("收到消息"+o);
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) o;
        ByteBuf byteBuf = frame.content();
        byte[] arr = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(arr);
        for(byte b: arr){
            System.out.print(b);
            System.out.print(",");
        }
    }

}
