package api.exception;

public class ExceptionConstants {

    public static final String MISSING_MANDATORY_PARAMETERS_ID = "id1";
    public static final String USER_ALREADY_EXIST_ID = "id2";
    public static final String USER_NOT_FOUND_ID = "id3";
    public static final String ACCOUNT_NOT_FOUND_ID = "id4";
    public static final String MONEY_AMOUNT_SHOULD_BE_POSITIVE_ID = "id5";
    public static final String NOT_ENOUGH_MONEY_ID = "id6";
    public static final String TRANSFER_NOT_FOUND_ID = "id6";

    public static final String MISSING_MANDATORY_PARAMETERS_MESSAGE = "Missing mandatory parameters: ";
    public static final String USER_ALREADY_EXIST_MESSAGE = "User with provider passportId is already exist: passportId=";
    public static final String USER_NOT_FOUND_MESSAGE = "User with provider passportId does not exist: passportId=";
    public static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account with provider accountId does not exist: accountId=";
    public static final String MONEY_AMOUNT_SHOULD_BE_POSITIVE_MESSAGE = "Money amount should be positive value: amount=";
    public static final String NOT_ENOUGH_MONEY_MESSAGE = "Not enough money to complete the operation";
    public static final String TRANSFER_NOT_FOUND_MESSAGE = "Transfers not found for account: accountToId=";

}
