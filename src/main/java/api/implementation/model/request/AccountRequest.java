package api.implementation.model.request;

import java.util.Objects;

public class AccountRequest {
    String passportId;
    String moneyBalance;
    String creditLimit;

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
    }

    public String getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(String moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public AccountRequest(String passportId, String moneyBalance, String creditLimit) {
        this.passportId = passportId;
        this.moneyBalance = moneyBalance;
        this.creditLimit = creditLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountRequest that = (AccountRequest) o;
        return Objects.equals(passportId, that.passportId) &&
                Objects.equals(moneyBalance, that.moneyBalance) &&
                Objects.equals(creditLimit, that.creditLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passportId, moneyBalance, creditLimit);
    }

    @Override
    public String toString() {
        return "AccountRequest{" +
                "passportId='" + passportId + '\'' +
                ", moneyBalance='" + moneyBalance + '\'' +
                ", creditLimit='" + creditLimit + '\'' +
                '}';
    }

    public AccountRequest() {
    }
}
