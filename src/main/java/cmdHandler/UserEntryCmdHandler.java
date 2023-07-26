package cmdHandler;

import entities.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import message.GameMsgProtocol;
import utils.BroadCaster;
import entities.UserManager;


// Handle the new user information
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        if(null == ctx || null == cmd) return;
        Integer userID = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        System.out.println("CMD-=-=-=============" + userID);

        cmd.getDefaultInstanceForType();
        if (null == userID) {
            System.out.println("can not find the user ID");
            return;
        }

        User byUserID = UserManager.getByUserID(userID);


        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();

        System.out.println(byUserID.userName + "________________________");
        resultBuilder.setUserName(byUserID.userName);
        resultBuilder.setUserId(userID);
        resultBuilder.setHeroAvatar(byUserID.heroAvatar);

        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        BroadCaster.broadcast(newResult);
    }
}
