package api.model;

import java.math.BigDecimal;
import java.util.Objects;


public class Account {

    private long id;
    private String userPassportId;
    private BigDecimal moneyBalance;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserPassportId() {
        return userPassportId;
    }

    public void setUserPassportId(String userPassportId) {
        this.userPassportId = userPassportId;
    }

    public BigDecimal getMoneyBalance() {
        return moneyBalance;
    }

    public void setMoneyBalance(BigDecimal moneyBalance) {
        this.moneyBalance = moneyBalance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    private AccountType accountType;
    private BigDecimal creditLimit;


    public Account(long id, String userPassportId, BigDecimal moneyBalance, AccountType accountType,
                   BigDecimal creditLimit) {
        this.id = id;
        this.userPassportId = userPassportId;
        this.moneyBalance = moneyBalance;
        this.accountType = accountType;
        this.creditLimit = AccountType.CREDIT.equals(accountType) ? creditLimit : BigDecimal.ZERO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id &&
                Objects.equals(userPassportId, account.userPassportId) &&
                Objects.equals(moneyBalance, account.moneyBalance) &&
                accountType == account.accountType &&
                Objects.equals(creditLimit, account.creditLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userPassportId, moneyBalance, accountType, creditLimit);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userPassportId='" + userPassportId + '\'' +
                ", moneyBalance=" + moneyBalance +
                ", accountType=" + accountType +
                ", creditLimit=" + creditLimit +
                '}';
    }
}
