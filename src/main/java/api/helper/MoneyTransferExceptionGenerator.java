package api.helper;

import api.exception.ExceptionConstants;
import api.exception.MoneyTransfersException;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import spark.Response;


public class MoneyTransferExceptionGenerator {
    private static Gson gson = new Gson();

    public static String generateMissingMandatoryParametersException(Response response, String parameter) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.MISSING_MANDATORY_PARAMETERS_ID, ExceptionConstants.MISSING_MANDATORY_PARAMETERS_MESSAGE + parameter);
        return setBody(HttpStatus.BAD_REQUEST_400, response, exception);
    }

    public static String generateUserAlreadyExistException(Response response, String userPassportId) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.USER_ALREADY_EXIST_ID, ExceptionConstants.USER_ALREADY_EXIST_MESSAGE + userPassportId);
        return setBody(HttpStatus.BAD_REQUEST_400, response, exception);
    }

    public static String generateUserNotFoundException(Response response, String userPassportId) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.USER_NOT_FOUND_ID, ExceptionConstants.USER_NOT_FOUND_MESSAGE + userPassportId);
        return setBody(HttpStatus.NOT_FOUND_404, response, exception);
    }

    public static String generateAccountNotFoundException(Response response, String accountID) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.ACCOUNT_NOT_FOUND_ID, ExceptionConstants.ACCOUNT_NOT_FOUND_MESSAGE + accountID);
        return setBody(HttpStatus.NOT_FOUND_404, response, exception);
    }

    public static String generateMoneyAmountShouldBePositiveException(Response response, String amount) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE_ID, ExceptionConstants.MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE_MESSAGE + amount);
        return setBody(HttpStatus.BAD_REQUEST_400, response, exception);
    }

    public static String generateNotEnoughMoneyException(Response response) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.NOT_ENOUGH_MONEY_ID, ExceptionConstants.NOT_ENOUGH_MONEY_MESSAGE);
        return setBody(HttpStatus.BAD_REQUEST_400, response, exception);
    }

    public static String generateTransfersNotFoundException(Response response, String accountId) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.TRANSFER_NOT_FOUND_ID, ExceptionConstants.TRANSFER_NOT_FOUND_MESSAGE + accountId);
        return setBody(HttpStatus.NOT_FOUND_404, response, exception);
    }

    public static String generateTransfersNotFoundException(Response response) {
        MoneyTransfersException exception = new MoneyTransfersException(ExceptionConstants.TRANSFER_NOT_FOUND_ID, ExceptionConstants.TRANSFERS_NOT_FOUND_MESSAGE);
        return setBody(HttpStatus.NOT_FOUND_404, response, exception);
    }

    private static String setBody(int status, Response response, MoneyTransfersException exception) {
        response.status(status);
        response.body(gson.toJson(exception));
        return response.body();
    }

}
