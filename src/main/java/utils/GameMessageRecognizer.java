package utils;


import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import message.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


// a class which provides mappings to class <-> messageID <-> messageObject
public final class GameMessageRecognizer {
    /**
     * 消息编号 -> 消息对象字典
     */
    static private final Map<Integer, GeneratedMessageV3> _msgCodeAndMsgObjMap = new HashMap<>();

    /**
     * 消息类 -> 消息编号字典
     */
    static private final Map<Class<?>, Integer> _clazzAndMsgCodeMap = new HashMap<>();

    /**
     * 私有化类默认构造器
     */
    private GameMessageRecognizer() {
    }

    static private final Logger LOGGER = LoggerFactory.getLogger(GameMessageRecognizer.class);


    /**
     * 初始化
     */
    static public void init() {

        //利用反射修改该类
//        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE, GameMsgProtocol.UserEntryCmd.getDefaultInstance());
//        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE, GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
//        _msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE, GameMsgProtocol.UserMoveToCmd.getDefaultInstance());
//
//        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserEntryResult.class, GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE);
//        _clazzAndMsgCodeMap.put(GameMsgProtocol.WhoElseIsHereResult.class, GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE);
//        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserMoveToResult.class, GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE);
//        _clazzAndMsgCodeMap.put(GameMsgProtocol.UserQuitResult.class, GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE);

        Class<?>[] innerClassArray = GameMsgProtocol.class.getDeclaredClasses();
        for (Class<?> innerClass : innerClassArray) {
            // inner class inherits from the GeneratedMessageV3
            if (null == innerClass || !GeneratedMessageV3.class.isAssignableFrom(innerClass)) {
                continue;
            }

            String className = innerClass.getSimpleName();
            className = className.toLowerCase();

            for (GameMsgProtocol.MsgCode msgCode : GameMsgProtocol.MsgCode.values()) {
                if (null == msgCode) {
                    continue;
                }

                try {
                    Object returnObj = innerClass.getDeclaredMethod("getDefaultInstance").invoke(innerClass);
                    LOGGER.info("{} <----> {}", innerClass.getName(), msgCode.getNumber());

                    _msgCodeAndMsgObjMap.put(msgCode.getNumber(), (GeneratedMessageV3) returnObj);

                    _clazzAndMsgCodeMap.put(innerClass, msgCode.getNumber());


                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 根据消息编号获取消息构建器
     *
     * @param msgCode
     * @return
     */
    static public Message.Builder getBuilderByMsgCode(int msgCode) {
        if (msgCode < 0) {
            return null;
        }

        GeneratedMessageV3 defaultMsg = _msgCodeAndMsgObjMap.get(msgCode);

        if (null == defaultMsg) {
            return null;
        } else {
            return defaultMsg.newBuilderForType();
        }
    }

    /**
     * 根据消息类获取消息编号
     *
     * @param msgClazz
     * @return
     */
    static public int getMsgCodeByClazz(Class<?> msgClazz) {
        if (null == msgClazz) {
            return -1;
        }

        Integer msgCode = _clazzAndMsgCodeMap.get(msgClazz);

        if (null == msgCode) {
            return -1;
        } else {
            return msgCode.intValue();
        }
    }
}
