package utils;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public final class BroadCaster {
    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private BroadCaster(){
    }

    static public void addChannel(Channel ch) {
        if (null != ch) {
            _channelGroup.add(ch);
        }
    }

    static public void removeChannel(Channel ch) {
        if (null != ch) _channelGroup.remove(ch);
    }

    static public void broadcast(Object msg) {
        if (null != msg) _channelGroup.writeAndFlush(msg);
    }
}
