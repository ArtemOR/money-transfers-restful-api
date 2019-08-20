package api.implementation.exception;

public class MoneyTransferException extends RuntimeException {

    private String errorCode;
    private transient int httpResponseCode;

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