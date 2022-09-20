import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(null==msg||!(msg instanceof )){

        }
        super.write(ctx, msg, promise);

        if(msg instanceof  GameMsgProtocol){

        }
        else{

        }
        byte[] bytes = ((GeneratedMessageV3) msg).toByteArray();

    }
}
