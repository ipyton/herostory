package utils;

import com.google.protobuf.GeneratedMessageV3;
import handlers.ICmdHandler;
import handlers.UserEntryCmdHandler;
import handlers.UserMoveToCmdHandler;
import handlers.WhoElseIsHereCmdHandler;
import message.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;


//class () <-> object (handler to process specific class)
public class CmdHandlerFactory {

    static private Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap<>();

    private CmdHandlerFactory(){}


    static public void init() {


    }

    static public ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz) {
        if (null == msgClazz) {
            return null;
        }
        return _handlerMap.get(msgClazz);
    }
}
