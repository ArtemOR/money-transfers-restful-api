package api.implementation.model;

import java.math.BigDecimal;
import java.util.Objects;


public class Account {

    private long id;
    private String passportId;
    private BigDecimal moneyBalance;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPassportId() {
        return passportId;
    }

    public void setPassportId(String passportId) {
        this.passportId = passportId;
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


    public Account() {
    }

    public Account(long id, String passportId, BigDecimal moneyBalance, AccountType accountType,
                   BigDecimal creditLimit) {
        this.id = id;
        this.passportId = passportId;
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
                Objects.equals(passportId, account.passportId) &&
                Objects.equals(moneyBalance, account.moneyBalance) &&
                accountType == account.accountType &&
                Objects.equals(creditLimit, account.creditLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, passportId, moneyBalance, accountType, creditLimit);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", passportId='" + passportId + '\'' +
                ", moneyBalance=" + moneyBalance +
                ", accountType=" + accountType +
                ", creditLimit=" + creditLimit +
                '}';
    }
}
