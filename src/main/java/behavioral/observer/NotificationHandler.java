package behavioral.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class NotificationHandler {
    private static final Logger log = LoggerFactory.getLogger(NotificationHandler.class);
    private static final Map<String, List<Object>> channels = new HashMap<>();

    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_MAP = new HashMap<>();

    static {
        WRAPPER_TO_PRIMITIVE_MAP.put(Boolean.class, boolean.class);
        WRAPPER_TO_PRIMITIVE_MAP.put(Byte.class, byte.class);
        WRAPPER_TO_PRIMITIVE_MAP.put(Character.class, char.class);
        WRAPPER_TO_PRIMITIVE_MAP.put(Short.class, short.class);
        WRAPPER_TO_PRIMITIVE_MAP.put(Integer.class, int.class);
        WRAPPER_TO_PRIMITIVE_MAP.put(Long.class, long.class);
        WRAPPER_TO_PRIMITIVE_MAP.put(Float.class, float.class);
        WRAPPER_TO_PRIMITIVE_MAP.put(Double.class, double.class);
    }

    public static void subscribe(String channelName, Object obj) {
        if (!channels.containsKey(channelName)) {
            channels.put(channelName, new ArrayList<>());
        }

        for (Object lobj : channels.get(channelName)) {
            if (lobj.equals(obj)) {
                log.warn("The Object {} Is already subscribe to {}", obj, channelName);
                return;
            }
        }

        channels.get(channelName).add(obj);
    }

    public static void unsubscribe(String channelName, Object obj) {
        if (!channels.containsKey(channelName)) {
            log.warn("The Object {} Is not subscribed to {}", obj, channelName);
            return;
        }
        channels.get(channelName).remove(obj);
    }

    /**
     * Sends a message to a specific channel, attempting to call a method
     * on subscribed objects that match the specified function name and inferred parameter types.
     * <p>
     * This method iterates through all objects subscribed to the given {@code channelName}.
     * For each subscriber, it attempts to dynamically invoke a method identified by {@code functionName}.
     * The method's parameter types are inferred from the runtime types of the {@code args} provided.
     * Primitive wrapper types (e.g., `Integer`) are first tryed as is and then
     * automatically mapped to their primitive equivalents
     * (e.g., `int`) to correctly match method signatures.
     * NB: If a signatures has mixed Types (e.g Integer, int) It WON'T WORK
     * </p>
     * <p>
     * If a channel does not exist or has no subscribers, a warning is logged.
     * If a subscribed object does not have a method matching the inferred signature, it is skipped with a log message.
     * Errors during method invocation (e.g., access issues, exceptions thrown by the invoked method,
     * or argument mismatches) are caught and logged as errors.
     * </p>
     *
     * @param channelName The name of the channel to send the message to.
     * @param functionName The name of the method to be invoked on each subscribed object.
     * @param args A variable-length argument list (varargs) representing the
     * arguments to be passed to the invoked method. The runtime types
     * of these arguments are used to infer the method's signature.
     */
    public static void send(String channelName, String functionName, Object... args) {
        if (!channels.containsKey(channelName)) {
            log.warn("Channel {} doesn't exist", channelName);
        }
        for (Object lobj : channels.get(channelName)) {
            sendWithWrapperTypes(lobj, functionName, args);
        }
    }

    private static void sendWithWrapperTypes(Object lobj, String functionName, Object... args) {
        try {
            List<Class<?>> types = new ArrayList<>();
            for (Object o : args) {
                types.add(o.getClass());
            }
            Method method = lobj.getClass().getDeclaredMethod(functionName, types.toArray(new Class<?>[0]));
            method.invoke(lobj, args);
        } catch (Exception fault) {
            sendWithBasicTypes(lobj, functionName, args);
        }
    }

    private static void sendWithBasicTypes(Object lobj, String functionName, Object... args) {
        try {
            List<Class<?>> types = new ArrayList<>();
            for (Object o : args) {
                types.add(toBasicType(o.getClass()));
            }
            Method method = lobj.getClass().getDeclaredMethod(functionName, types.toArray(new Class<?>[0]));
            method.invoke(lobj, args);
        } catch (NoSuchMethodException fault) {
            log.warn("The object {} doesn't contain method {}", lobj, functionName);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> toBasicType(Class<?> clazz) {
        return WRAPPER_TO_PRIMITIVE_MAP.getOrDefault(clazz, clazz);
    }
}

