package utils;

import com.google.protobuf.GeneratedMessageV3;
import handlers.ICmdHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//this is a thread to process
public class MainThreadProcessor {
    // log object

    static private final Logger LOGGER = LoggerFactory.getLogger(MainThreadProcessor.class);

    //singleton
    static private final MainThreadProcessor _instance = new MainThreadProcessor();

    //create a thread pool

    private final ExecutorService _es = Executors.newSingleThreadExecutor((r) -> {
        Thread newThread = new Thread(r);
        newThread.setName("MainMsgProcessor");
        return newThread;
    });

    private MainThreadProcessor(){}

    static public MainThreadProcessor getInstance(){
        return _instance;
    }

    public void process(Runnable r) {
        if (null!= r) _es.submit(r);
    }

    public void process(ChannelHandlerContext ctx, Object msg) {
        if (null == ctx || null == msg) return;
        final Class<?> msgClass = msg.getClass();

        LOGGER.info("get information from users msgClass = {}, msgObject = {}", msgClass.getSimpleName(), msg);

        _es.submit(()->{
           try {
               ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msgClass);
               if (null != cmdHandler) cmdHandler.handle(ctx, cast(msg));
           } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
           }
        });
    }

    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (!(msg instanceof GeneratedMessageV3)) {
            return null;
        } else {
            return (TCmd) msg;
        }
    }

}
