package cmdHandler;

import entities.User;
import entities.UserManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import loginService.AsyncLoginService;
import loginService.DB.AccountInformation;
import loginService.LoginService;
import message.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {

    static private final Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (null == ctx || null == cmd) return;

        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        if (null == userName || null == password) return;

        //if there are no user information before, just create a new account with default information.
        AsyncLoginService.getInstance().userLogin(userName, password, (account)->{
            GameMsgProtocol.UserLoginResult.Builder resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();

            if (null == account) {
                resultBuilder.setUserId(-1);
                resultBuilder.setUserName("");
                resultBuilder.setHeroAvatar("");
            } else {
                User newUser = new User();
                newUser.userID = account.userId;
                newUser.userName = account.userName;
                newUser.heroAvatar = account.heroAvatar;
                newUser.currHP = 100;

                UserManager.addUser(newUser);

                ctx.channel().attr(AttributeKey.valueOf("userId")).set(newUser.userID);

                resultBuilder.setUserId(account.userId);
                resultBuilder.setUserName(account.userName);
                resultBuilder.setHeroAvatar(account.heroAvatar);
            }
            GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);
            return null;
        });


    }
}
