package api.implementation.model.transfer;

import api.implementation.model.AccountTransfer;

import java.math.BigDecimal;
import java.util.Objects;

public class BetweenAccountsTransfer extends AccountTransfer {
    private long id;
    private Long accountFromId;

    public BetweenAccountsTransfer(Long accountFromId, Long accountToId, BigDecimal amount, Long time) {
        super(accountToId, amount, time);
        this.accountFromId = accountFromId;
    }

    public BetweenAccountsTransfer() {

    }

    public Long getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(Long accountFromId) {
        this.accountFromId = accountFromId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BetweenAccountsTransfer that = (BetweenAccountsTransfer) o;
        return id == that.id &&
                Objects.equals(accountFromId, that.accountFromId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, accountFromId);
    }

    @Override
    public String toString() {
        return "BetweenAccountsTransfer{" +
                "id=" + id +
                ", accountFromId=" + accountFromId +
                '}';
    }
}
