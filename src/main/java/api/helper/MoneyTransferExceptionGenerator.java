package api.helper;

import api.exception.ExceptionConstants;
import api.exception.MoneyTransfersException;
import com.google.gson.Gson;
import spark.Response;

public class MoneyTransferExceptionGenerator {
    private static Gson gson = new Gson();

    public static String generateMissingMandatoryParametersException(Response response, String parameter) {
        MoneyTransfersException exception =  new MoneyTransfersException(ExceptionConstants.MISSING_MANDATORY_PARAMETERS_ID, ExceptionConstants.MISSING_MANDATORY_PARAMETERS_MESSAGE+parameter);
        return setBody(response,exception);
    }

    public static String generateUserNotFoundException(Response response, String userPassportId) {
        MoneyTransfersException exception =  new MoneyTransfersException(ExceptionConstants.USER_NOT_FOUND_ID, ExceptionConstants.USER_NOT_FOUND_MESSAGE+userPassportId);
        return setBody(response,exception);
    }

    public static String generateTransfersNotFoundException(Response response, String accountToId) {
        MoneyTransfersException exception =  new MoneyTransfersException(ExceptionConstants.TRANSFER_NOT_FOUND_ID, ExceptionConstants.TRANSFER_NOT_FOUND_MESSAGE+accountToId);
        return setBody(response,exception);
    }

    public static String generateUserAlreadyExistException(Response response, String userPassportId) {
        MoneyTransfersException exception =  new MoneyTransfersException(ExceptionConstants.USER_ALREADY_EXIST_ID, ExceptionConstants.USER_ALREADY_EXIST_MESSAGE+userPassportId);
        return setBody(response,exception);
    }

    private static String setBody(Response response, MoneyTransfersException exception) {
        response.status(400);
        response.body(gson.toJson(exception));
        return response.body();
    }

    public static String generateAccountNotFoundException(Response response, String accountID) {
        MoneyTransfersException exception =  new MoneyTransfersException(ExceptionConstants.ACCOUNT_NOT_FOUND_ID, ExceptionConstants.ACCOUNT_NOT_FOUND_MESSAGE+accountID);
        return setBody(response,exception);
    }

    public static String generateMoneyAmountShouldBePositiveException(Response response, String amount) {
        MoneyTransfersException exception =  new MoneyTransfersException(ExceptionConstants.MONEY_AMOUNT_SHOULD_BE_POSITIVE_ID, ExceptionConstants.MONEY_AMOUNT_SHOULD_BE_POSITIVE_MESSAGE+amount);
        return setBody(response,exception);
    }

    public static String generateNotEnoughMoneyException(Response response) {
        MoneyTransfersException exception =  new MoneyTransfersException(ExceptionConstants.NOT_ENOUGH_MONEY_ID, ExceptionConstants.NOT_ENOUGH_MONEY_MESSAGE);
        return setBody(response,exception);
    }
}
