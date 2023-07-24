package handlers;

import entities.User;
import entities.UserManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import message.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.BroadCaster;

public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd>{


    static private final Logger LOGGER = LoggerFactory.getLogger(UserAttkCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if (null == ctx || null == cmd) return;

        Integer attkUserID = (Integer) ctx.channel().attr(AttributeKey.valueOf("userID")).get();

        if (null == attkUserID) return;

        int targetUserID = cmd.getTargetUserId();

        User targetUser = UserManager.getByUserID(targetUserID);
        LOGGER.info("currentThread = {}", Thread.currentThread().getName());

        if (null == targetUser) {
            broadcastAttkResult(attkUserID, -1);
            return;
        }

        final int dmgPoint = 10;
        targetUser.currHP = targetUser.currHP - dmgPoint;

        broadcastAttkResult(attkUserID, targetUserID);
        broadcastSubtractHPResult(targetUserID, dmgPoint);

        if (targetUser.currHP <=0 ) {
            broadcastDieResult(targetUserID);
        }
    }

    static private void broadcastAttkResult(int attkUserID, int targetUserID) {
        if (attkUserID <= 0) return;
        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserID);
        resultBuilder.setTargetUserId(targetUserID);

        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        BroadCaster.broadcast(newResult);
    }

    static private void broadcastSubtractHPResult(int targetUserID, int damagePoint){
        if (targetUserID <= 0 || damagePoint <= 0) return;

        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserID);
        resultBuilder.setSubtractHp(damagePoint);

        GameMsgProtocol.UserSubtractHpResult newResult = resultBuilder.build();
        BroadCaster.broadcast(newResult);
    }

    static private void broadcastDieResult(int targetUserID) {
        if (targetUserID <= 0) {
            return;
        }
        GameMsgProtocol.UserDieResult.Builder resultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserID);

        GameMsgProtocol.UserDieResult newResult = resultBuilder.build();
        BroadCaster.broadcast(newResult);
    }
}
