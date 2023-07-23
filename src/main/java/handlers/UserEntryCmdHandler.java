package handlers;

import Entities.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import loginService.DB.AccountInformation;
import message.GameMsgProtocol;
import utils.BroadCaster;
import Entities.UserManager;


// Handle the new user information
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        if(null == ctx || null == cmd) return;
        Integer userID = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if (null == userID) return;

        User byUserID = UserManager.getByUserID(userID);


        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userID);
        resultBuilder.setHeroAvatar(byUserID.heroAvatar);

        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        BroadCaster.broadcast(newResult);
    }
}
