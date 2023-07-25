package cmdHandler;

import io.netty.channel.ChannelHandlerContext;
import message.GameMsgProtocol;

public class GetRankCmdHandler implements ICmdHandler<GameMsgProtocol.GetRankCmd> {


    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.GetRankCmd cmd) {

    }
}
