package handlers;

import handlers.Entities.User;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import message.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    static private final Map<Integer, User> _userMap = new HashMap<>();


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (null == ctx) {
            return;
        }

        try {
            super.channelActive(ctx);
            _channelGroup.add(ctx.channel());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

        LOGGER.info(msg.getClass().getSimpleName(),msg);
        try {
          if (msg instanceof GameMsgProtocol.UserEntryCmd) {
              GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
              int userID = cmd.getUserId();
              String heroAvatar = cmd.getHeroAvatar();

              User newUser = new User();
              newUser.userID = userID;
              newUser.heroAvatar = heroAvatar;
              _userMap.putIfAbsent(userID, newUser);

              GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
              resultBuilder.setUserId(userID);
              resultBuilder.setHeroAvatar(heroAvatar);

              GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
              _channelGroup.writeAndFlush(newResult);
          } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
              GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

              for (User currUser: _userMap.values()) {
                    if (null == currUser) {
                        continue;
                    }

                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                    userInfoBuilder.setUserId(currUser.userID);
                    userInfoBuilder.setHeroAvatar(currUser.heroAvatar);
                    resultBuilder.addUserInfo(userInfoBuilder);
              }
              GameMsgProtocol.WhoElseIsHereResult newResult = resultBuilder.build();
              ctx.writeAndFlush(newResult);
          }

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }


    }
}
