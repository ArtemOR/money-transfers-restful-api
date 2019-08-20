package api.implementation.model;

import java.math.BigDecimal;
import java.util.Objects;

public class AccountTransfer {
    private Long accountToId;
    private BigDecimal amount;
    private Long time;

    public AccountTransfer(Long accountToId, BigDecimal amount, Long time) {
        this.accountToId = accountToId;
        this.amount = amount;
        this.time = time;
    }

    public AccountTransfer() {
    }

    public Long getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(Long accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "AccountTransfer{" +
                "accountToId=" + accountToId +
                ", amount=" + amount +
                ", time=" + time +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountTransfer that = (AccountTransfer) o;
        return Objects.equals(accountToId, that.accountToId) &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountToId, amount, time);
    }

}
