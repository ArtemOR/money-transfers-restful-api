package api.implementation.exception;

import org.eclipse.jetty.http.HttpStatus;

public enum ExceptionList {
    BAD_REQUEST("id0", "Bad Request: check the request body", HttpStatus.BAD_REQUEST_400),
    MISSING_MANDATORY_PARAMETERS("id1", "Missing mandatory parameters: ", HttpStatus.BAD_REQUEST_400),
    USER_ALREADY_EXIST("id2", "User with provided passportId is already exist: passportId=", HttpStatus.BAD_REQUEST_400),
    MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE("id3", "Money parameter should contains positive value: parameter=", HttpStatus.BAD_REQUEST_400),
    NOT_ENOUGH_MONEY("id4", "Not enough money to complete the operation", HttpStatus.BAD_REQUEST_400),
    USER_NOT_FOUND("id5", "User with provided passportId does not exist: passportId=", HttpStatus.NOT_FOUND_404),
    TRANSFER_NOT_FOUND("id6", "Transfer with provided transferId does not exist: transferId=", HttpStatus.NOT_FOUND_404),
    ACCOUNT_NOT_FOUND("id7", "Account with provided accountId does not exist: accountId=", HttpStatus.NOT_FOUND_404),
    TRANSFERS_NOT_FOUND("id8", "Transfers not found for account: accountId=", HttpStatus.NOT_FOUND_404),
    WRONG_NUMBER_FORMAT("id9", "Bad Request. Cannot cast string to number value", HttpStatus.BAD_REQUEST_400),
    CHOSE_DIFFERENT_ACCOUNT_IDS("id10", "accountFromId and accountToId parameters are equal. Please specify different.", HttpStatus.BAD_REQUEST_400);

    public String exceptionId;
    public String message;
    public int httpStatus;

    ExceptionList(String exceptionId, String message, int httpStatus) {
        this.exceptionId = exceptionId;
        this.httpStatus = httpStatus;
        this.message = message;
    }

}



