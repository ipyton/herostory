package handlers;

import entities.User;
import io.netty.channel.ChannelHandlerContext;
import entities.UserManager;

import message.*;

import java.util.Collection;

//it contains movement information to show the movement of others when querying.
public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd>{

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        if (null == ctx || null == cmd) {
            return;
        }
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        Collection<User> userList = UserManager.listUser();

        for (User currUser : userList) {
            if (null == currUser) {
                continue;
            }

            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder builder = GameMsgProtocol.WhoElseIsHereResult.
                    UserInfo.newBuilder();
            builder.setUserId(currUser.userID);
            builder.setHeroAvatar(currUser.heroAvatar);



            //construct state of movement, start time + start point + end point.
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.Builder mvStateBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.MoveState.newBuilder();
            mvStateBuilder.setFromPosX(currUser.state.fromX);
            mvStateBuilder.setFromPosY(currUser.state.fromY);
            mvStateBuilder.setToPosX(currUser.state.toX);
            mvStateBuilder.setToPosY(currUser.state.toY);
            mvStateBuilder.setStartTime(currUser.state.startTime);

            builder.setMoveState(mvStateBuilder);
            resultBuilder.addUserInfo(builder);
        }

        GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
