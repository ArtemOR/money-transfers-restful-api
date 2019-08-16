package api.exception;

import com.google.gson.Gson;
import spark.Response;



public class MoneyTransferExceptionGenerator {
    private static Gson gson = new Gson();

    public static String generateMissingMandatoryParametersException(Response response) {
        MoneyTransfersException exception =  new MoneyTransfersException(ExceptionConstants.MISSING_MANDATORY_PARAMETERS_ID, ExceptionConstants.MISSING_MANDATORY_PARAMETERS_MESSAGE);
        return setBody(response,exception);

    }

    private static String setBody(Response response, MoneyTransfersException exception) {
        response.status(400);
        response.body(gson.toJson(exception));
        return response.body();
    }
}
