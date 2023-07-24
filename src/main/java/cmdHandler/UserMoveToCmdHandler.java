package cmdHandler;

import entities.User;
import entities.UserManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import message.GameMsgProtocol;
import utils.BroadCaster;

public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }
        Integer userID = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (null ==userID) return;

        User existUser = UserManager.getByUserID(userID);
        if(null == existUser) return;

        long nowTime = System.currentTimeMillis();

        existUser.state.fromX = cmd.getMoveFromPosX();
        existUser.state.fromY = cmd.getMoveFromPosY();
        existUser.state.toX = cmd.getMoveToPosX();
        existUser.state.toY = cmd.getMoveToPosY();
        existUser.state.startTime = nowTime;

        GameMsgProtocol.UserMoveToResult.Builder builder = GameMsgProtocol.UserMoveToResult.newBuilder();
        builder.setMoveUserId(userID);
        builder.setMoveToPosX(cmd.getMoveToPosX());
        builder.setMoveToPosY(cmd.getMoveToPosY());
        builder.setMoveFromPosX(cmd.getMoveToPosX());
        builder.setMoveFromPosY(cmd.getMoveToPosY());
        builder.setMoveStartTime(nowTime);

        //build the result and broadcast
        GameMsgProtocol.UserMoveToResult newResult = builder.build();
        BroadCaster.broadcast(newResult);
    }
}
