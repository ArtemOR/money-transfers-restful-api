package api.model.transfer;

import java.math.BigDecimal;
import java.util.Objects;

public class BetweenAccountsTransfer extends AccountTransfer {
    private Long accountFromId;

    public BetweenAccountsTransfer(Long accountToId, BigDecimal amount, Long time, Long accountFromId, Long transferId) {
        super(accountToId, amount, time, transferId);
        this.accountFromId = accountFromId;
    }

    public Long getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(Long accountFromId) {
        this.accountFromId = accountFromId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BetweenAccountsTransfer that = (BetweenAccountsTransfer) o;
        return Objects.equals(accountFromId, that.accountFromId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), accountFromId);
    }

    @Override
    public String toString() {
        return "BetweenAccountsTransfer{" +
                "accountFromId=" + accountFromId +
                "id=" + id +
                ", accountToId=" + accountToId +
                ", amount=" + amount +
                ", time=" + time +
                '}';
    }
}
