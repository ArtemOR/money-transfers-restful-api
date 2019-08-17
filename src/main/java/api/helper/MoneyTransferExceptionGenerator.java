package api.helper;

import api.exception.ExceptionConstants;
import api.exception.MoneyTransfersException;
import com.google.gson.Gson;
import spark.Response;

public class MoneyTransferExceptionGenerator {
    private static Gson gson = new Gson();
    private static int STATUS_BAD_REQUEST = 400;
    private static int STATUS_NOT_FOUND = 404;


    public static String generateMissingMandatoryParametersException(Response response, String parameter) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.MISSING_MANDATORY_PARAMETERS_ID, ExceptionConstants.MISSING_MANDATORY_PARAMETERS_MESSAGE + parameter);
        return setBody(STATUS_BAD_REQUEST, response, exception);
    }

    public static String generateUserAlreadyExistException(Response response, String userPassportId) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.USER_ALREADY_EXIST_ID, ExceptionConstants.USER_ALREADY_EXIST_MESSAGE + userPassportId);
        return setBody(STATUS_BAD_REQUEST, response, exception);
    }

    public static String generateUserNotFoundException(Response response, String userPassportId) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.USER_NOT_FOUND_ID, ExceptionConstants.USER_NOT_FOUND_MESSAGE + userPassportId);
        return setBody(STATUS_NOT_FOUND, response, exception);
    }

    public static String generateAccountNotFoundException(Response response, String accountID) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.ACCOUNT_NOT_FOUND_ID, ExceptionConstants.ACCOUNT_NOT_FOUND_MESSAGE + accountID);
        return setBody(STATUS_NOT_FOUND, response, exception);
    }

    public static String generateMoneyAmountShouldBePositiveException(Response response, String amount) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.MONEY_AMOUNT_SHOULD_BE_POSITIVE_ID, ExceptionConstants.MONEY_AMOUNT_SHOULD_BE_POSITIVE_MESSAGE + amount);
        return setBody(STATUS_BAD_REQUEST, response, exception);
    }

    public static String generateNotEnoughMoneyException(Response response) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.NOT_ENOUGH_MONEY_ID, ExceptionConstants.NOT_ENOUGH_MONEY_MESSAGE);
        return setBody(STATUS_BAD_REQUEST, response, exception);
    }

    public static String generateTransfersNotFoundException(Response response, String accountId) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.TRANSFER_NOT_FOUND_ID, ExceptionConstants.TRANSFER_NOT_FOUND_MESSAGE + accountId);
        return setBody(STATUS_NOT_FOUND, response, exception);
    }

    private static String setBody(int status, Response response, MoneyTransfersException exception) {
        response.status(status);
        response.body(gson.toJson(exception));
        return response.body();
    }

}
