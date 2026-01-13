package core.test.engine.util;

import java.util.UUID;

public class UUIDUtil {

    private static ThreadLocal<UUID> uuid = new ThreadLocal<UUID>();

    public static void generateUUID(){
            uuid.set(UUID.randomUUID());
    }

    public static UUID getUUID(){
        UUID returnValue = uuid.get();
        if(returnValue == null){
            returnValue = UUID.randomUUID();
            uuid.set(returnValue);
        }
        return uuid.get();
    }

    public static void clearUUID(){
        uuid.remove();
    }


}
