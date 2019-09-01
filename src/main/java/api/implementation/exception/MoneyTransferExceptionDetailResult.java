package api.implementation.exception;

public class MoneyTransferExceptionDetailResult<T> {

    private T success;
    private MoneyTransferException error;

    public MoneyTransferExceptionDetailResult(T detail) {
        this.success = detail;
    }

    public MoneyTransferExceptionDetailResult(MoneyTransferException detail) {
        this.error = detail;
    }

    public T getSuccess() {
        return this.success;
    }

    public MoneyTransferException getError() {
        return this.error;
    }
}