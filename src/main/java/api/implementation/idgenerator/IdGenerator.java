package api.implementation.idgenerator;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private static volatile IdGenerator instance;
    private final static long START_ACCOUNT_ID_NUMBER = 10000000000000L;
    private final static long START_USER_NUMBER = 200000000000000L;
    private final static long START_CHARGING_ID_NUMBER = 30000000000000L;
    private final static long START_TRANSFER_ID_NUMBER = 40000000000000L;
    private static AtomicLong lastId = new AtomicLong(0L);

    public static synchronized IdGenerator getInstance() {
        if (instance == null) {
            synchronized (IdGenerator.class) {
                if (instance == null) {
                    instance = new IdGenerator();
                }
            }
        }
        return instance;
    }

    public long generateAccountId() {
        return START_ACCOUNT_ID_NUMBER + lastId.incrementAndGet();
    }

    public long generateBetweenTransferId() {
        return START_TRANSFER_ID_NUMBER + lastId.incrementAndGet();
    }

    public long generateInsideTransferId() {
        return START_CHARGING_ID_NUMBER + lastId.incrementAndGet();
    }

    public long generateUserId() {
        return START_USER_NUMBER + lastId.incrementAndGet();
    }

}
