package cmdHandler;

import com.google.protobuf.GeneratedMessageV3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.PackageUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


//class () <-> object (handler to process specific class)
public class CmdHandlerFactory {

    static private Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> _handlerMap = new HashMap<>();

    static private final Logger LOGGER = LoggerFactory.getLogger(CmdHandlerFactory.class);

    private CmdHandlerFactory(){}


    static public void init() {
        final String packageName = CmdHandlerFactory.class.getPackage().getName();
        Set<Class<?>> classSet = PackageUtil.listSubClass(packageName, true, ICmdHandler.class);

        for (Class<?> handlerClass : classSet) {
            if (null == handlerClass || 0 != (handlerClass.getModifiers() & Modifier.ABSTRACT)) continue;

            Method[] methods = handlerClass.getDeclaredMethods();

            Class<?> cmdClass = null;

            for (Method curMethod: methods) {
                if (null == curMethod || !curMethod.getName().equals("handle")) continue;

                Class<?>[] paramTypeArray = curMethod.getParameterTypes();

                if (paramTypeArray.length < 2 || paramTypeArray[1] == GeneratedMessageV3.class ||
                        !GeneratedMessageV3.class.isAssignableFrom(paramTypeArray[1])) {
                    continue;
                }
                cmdClass = paramTypeArray[1];
                break;
            }

            if (null == cmdClass) continue;

            try {
                ICmdHandler<?> newHandler = (ICmdHandler<?>) handlerClass.newInstance();
                _handlerMap.put(cmdClass, newHandler);

                LOGGER.info("{} <-> {}", cmdClass.getName(), handlerClass.getName());
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    static public ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz) {
        if (null == msgClazz) {
            return null;
        }
        return _handlerMap.get(msgClazz);
    }
}
