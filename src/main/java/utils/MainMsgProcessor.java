package utils;


import com.google.protobuf.GeneratedMessageV3;
import handlers.ICmdHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//This is a single thread processor which is called by different handlers.
public final class MainMsgProcessor {
    static private final Logger LOGGER = LoggerFactory.getLogger(MainMsgProcessor.class);
    static private final MainMsgProcessor _instance = new MainMsgProcessor();

    private final ExecutorService _es = Executors.newSingleThreadExecutor(
            (newRunnable) -> {
                Thread newThread = new Thread(newRunnable);
                newThread.setName("MainThreadProcessor");
                return newThread;
            }
    );

    private MainMsgProcessor(){}

    static public MainMsgProcessor getInstance(){
        return _instance;
    }

    public void process(ChannelHandlerContext ctx, Object msg) {
        if (null == ctx || null == msg) return;

        final Class<?> msgClass = msg.getClass();
        LOGGER.info("msgClass={}, msgObj = {}", msgClass.getSimpleName(), msg);
        _es.submit(()-> {
            try{
                ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msgClass);//return a handler by class
                if (null != cmdHandler) {
                    cmdHandler.handle(ctx, cast(msg));
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        });
    }

    static private <T extends GeneratedMessageV3> T cast(Object msg){
        if (!(msg instanceof GeneratedMessageV3)) {
            return null;
        } else {
            return (T) msg;
        }
    }








}
