package api.implementation.exception;

import java.util.List;

public class MoneyTransferException extends RuntimeException {

    private String errorCode;
    private transient int httpResponseCode;
    private List<MoneyTransferExceptionDetailResult<?>> results;

    public MoneyTransferException(String errorCode, String message) {
        super(message, null, false, false);
        this.errorCode = errorCode;
    }

    public MoneyTransferException(ExceptionList constant, String parameter) {
        super(constant.message + parameter, null, false, false);
        this.errorCode = constant.exceptionId;
        this.httpResponseCode = constant.httpStatus;
    }

    public MoneyTransferException(ExceptionList constant) {
        super(constant.message, null, false, false);
        this.errorCode = constant.exceptionId;
        this.httpResponseCode = constant.httpStatus;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public List<MoneyTransferExceptionDetailResult<?>> getResults() {
        return results;
    }

    public void setResults(List<MoneyTransferExceptionDetailResult<?>> results) {
        this.results = results;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    public void setHttpResponseCode(int httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }
}