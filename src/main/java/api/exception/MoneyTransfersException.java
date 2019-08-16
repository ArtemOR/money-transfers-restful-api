package api.exception;

import com.google.gson.Gson;
import spark.Response;

public class MoneyTransfersException {

    private String errorCode;
    private String message;

    public MoneyTransfersException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }





}