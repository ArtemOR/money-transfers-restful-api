package api.implementation.idgenerator;

public class IdGenerator {

    private static IdGenerator instance;
    private final static long START_ACCOUNT_ID_NUMBER = 10000000000000L;
    private final static long START_CHARGING_ID_NUMBER = 20000000000000L;
    private final static long START_TRANSFER_ID_NUMBER = 30000000000000L;
    private final static long START_USER_NUMBER = 500000000000000L;

    public static synchronized IdGenerator getInstance() {
        if (instance == null) {
            synchronized (IdGenerator.class) {
                if (instance == null) {
                    instance = new IdGenerator();
                }
            }
            instance = new IdGenerator();
        }
        return instance;
    }

    public long generateAccountId() {
        return START_ACCOUNT_ID_NUMBER + System.currentTimeMillis();
    }

    public long generateTransferId() {
        return START_TRANSFER_ID_NUMBER + System.currentTimeMillis();
    }

    public long generateChargingId() {
        return START_CHARGING_ID_NUMBER + System.currentTimeMillis();
    }

    public long generateUserId() {
        return START_USER_NUMBER + System.currentTimeMillis();
    }

}
