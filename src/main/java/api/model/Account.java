package api.model;

import java.math.BigDecimal;

import static api.helper.IdGenerator.generateAccountId;

public class Account {

    private long id;
    private long userId;
    private BigDecimal moneyBalance;

    public Account(long userId) {
        this.id = generateId();
        this.userId = userId;
        this.moneyBalance = BigDecimal.ZERO;
    }

    public Account(long id, long userId, BigDecimal moneyBalance) {
        this.id = id;
        this.userId = userId;
        this.moneyBalance = moneyBalance;
    }

    private long generateId() {
        return generateAccountId();
    }

}
