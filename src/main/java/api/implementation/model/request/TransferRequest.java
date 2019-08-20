package api.implementation.model.request;

import java.util.Objects;

public class TransferRequest {

    private String accountFromId;
    private String accountToId;
    private String amount;

    public String getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(String accountFromId) {
        this.accountFromId = accountFromId;
    }

    public String getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(String accountToId) {
        this.accountToId = accountToId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TransferRequest that = (TransferRequest) o;
        return Objects.equals(accountFromId, that.accountFromId) && Objects.equals(accountToId, that.accountToId) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {

        return Objects.hash(accountFromId, accountToId, amount);
    }

    @Override
    public String toString() {
        return "TransferRequest{" + "accountFromId='" + accountFromId + '\'' + ", accountToId='" + accountToId + '\'' + ", amount='" + amount + '\'' + '}';
    }
}
