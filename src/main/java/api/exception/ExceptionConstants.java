package api.exception;

public class ExceptionConstants {

    public static final String MISSING_MANDATORY_PARAMETERS_ID = "id1";
    public static final String USER_ALREADY_EXIST_ID = "id2";
    public static final String USER_NOT_FOUND_ID = "id3";
    public static final String ACCOUNT_NOT_FOUND_ID = "id4";
    public static final String MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE_ID = "id5";
    public static final String NOT_ENOUGH_MONEY_ID = "id6";
    public static final String TRANSFER_NOT_FOUND_ID = "id7";

    public static final String MISSING_MANDATORY_PARAMETERS_MESSAGE = "Missing mandatory parameters: ";
    public static final String USER_ALREADY_EXIST_MESSAGE = "User with provided passportId is already exist: passportId=";
    public static final String USER_NOT_FOUND_MESSAGE = "User with provided passportId does not exist: passportId=";
    public static final String ACCOUNT_NOT_FOUND_MESSAGE = "Account with provided accountId does not exist: accountId=";
    public static final String MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE_MESSAGE = "Money parameter should contains positive value: parameter=";
    public static final String NOT_ENOUGH_MONEY_MESSAGE = "Not enough money to complete the operation";
    public static final String TRANSFER_NOT_FOUND_MESSAGE = "Transfers not found for account: accountId=";
    public static final String TRANSFERS_NOT_FOUND_MESSAGE = "Transfers not found";

}
