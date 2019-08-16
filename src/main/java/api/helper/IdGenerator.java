package api.helper;

public class IdGenerator {
    private final static long START_ACCOUNT_ID_NUMBER = 10000000000000L;
    private final static long START_USER_NUMBER = 500000000000000L;

    public static long generateAccountId() {
        return START_ACCOUNT_ID_NUMBER + System.currentTimeMillis();
    }

    public static long generateUserId() {
        return START_USER_NUMBER + System.currentTimeMillis();
    }


}
