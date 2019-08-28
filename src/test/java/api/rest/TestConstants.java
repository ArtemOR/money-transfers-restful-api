package api.rest;


public class TestConstants {

    public static String ID_PARAM = "id";
    public static String NAME_PARAM = "name";
    public static String PASSPORT_ID_PARAM = "passportId";
    public static String MONEY_BALANCE_PARAM = "moneyBalance";
    public static String ACCOUNT_TO_ID_PARAM = "accountToId";
    public static String ACCOUNT_FROM_ID_PARAM = "accountFromId";
    public static String AMOUNT_PARAM = "amount";
    public static String ACCOUNT_TYPE_PARAM = "accountType";
    public static String CREDIT_LIMIT_PARAM = "creditLimit";
    public static String ACCOUNTS_PARAM = "accounts";

    public static String DETAIL_MESSAGE_REQUEST_PARAM = "detailMessage";

    public static String USER_NAME1 = "testName1";
    public static String PASSPORT_ID1 = "testPassportId1";
    public static String USER_NAME2 = "testName2";
    public static String PASSPORT_ID2 = "testPassportId2";
    public static String PASSPORT_ID_NON_EXIST = "nonExistPassportId";
    public static String ID_NON_EXIST = "1";
    public static String MONEY_VALUE = "1000";
    public static String SMALL_MONEY_VALUE = "100";
    public static String BIG_MONEY_VALUE = "1000000";
    public static String ZERO_MONEY_VALUE = "0";
    public static String NEGATIVE_MONEY_VALUE = "-100";
    public static String INVALID_MONEY_VALUE = "100.0.1";
    public static String DEBIT_VALUE = "DEBIT";
    public static String CREDIT_VALUE = "CREDIT";

    public static String MISSING_MANDATORY_PARAMETERS_EXCEPTION = "Missing mandatory parameters: ";
    public static String USER_ALREADY_EXIST_EXCEPTION = "User with provided passportId is already exist: passportId=";
    public static String MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE_EXCEPTION = "Money parameter should contains positive value: parameter=";
    public static String NOT_ENOUGH_MONEY_EXCEPTION = "Not enough money to complete the operation";
    public static String USER_NOT_FOUND_EXCEPTION = "User with provided passportId does not exist: passportId=";
    public static String TRANSFER_NOT_FOUND_EXCEPTION = "Transfer with provided transferId does not exist: transferId=";
    public static String ACCOUNT_NOT_FOUND_EXCEPTION = "Account with provided accountId does not exist: accountId=";
    public static String TRANSFERS_NOT_FOUND_EXCEPTION = "Transfers not found for account: accountId=";
    public static String WRONG_NUMBER_FORMAT_EXCEPTION = "Bad Request. Cannot cast string to number value";
    public static String CHOSE_DIFFERENT_ACCOUNT_IDS_EXCEPTION = "accountFromId and accountToId parameters are equal. Please specify different.";
}
