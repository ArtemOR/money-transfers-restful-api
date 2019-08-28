package api.implementation.service;

import api.implementation.idgenerator.IdGenerator;
import api.implementation.model.Account;
import api.implementation.model.AccountType;
import api.implementation.model.User;
import api.implementation.model.request.AccountRequest;
import api.implementation.model.request.TransferRequest;
import api.implementation.model.request.UserRequest;
import api.implementation.model.transfer.BetweenAccountsTransfer;
import api.implementation.model.transfer.InsideAccountTransfer;

import java.math.BigDecimal;
import java.util.Objects;

public class MoneyTransferModelConverter {

    static User convertUserRequest(UserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setPassportId(userRequest.getPassportId());
        user.setId(getIdGenerator().generateUserId());

        return user;
    }

    static Account convertAccountRequest(AccountRequest accountRequest) {
        Account account = new Account();
        long accountId = getIdGenerator().generateAccountId();
        account.setId(accountId);
        account.setPassportId(accountRequest.getPassportId());
        account.setMoneyBalance(Objects.isNull(accountRequest.getMoneyBalance()) ? BigDecimal.ZERO : new BigDecimal(accountRequest.getMoneyBalance()));
        account.setCreditLimit(Objects.isNull(accountRequest.getCreditLimit()) ? BigDecimal.ZERO : new BigDecimal(accountRequest.getCreditLimit()));
        account.setAccountType(BigDecimal.ZERO.equals(account.getCreditLimit()) ? AccountType.DEBIT : AccountType.CREDIT);

        return account;
    }

    static InsideAccountTransfer convertTransferRequest(TransferRequest transferRequest) {
        InsideAccountTransfer insideAccountTransfer = new InsideAccountTransfer();
        insideAccountTransfer.setAccountToId(Long.valueOf(transferRequest.getAccountToId()));
        insideAccountTransfer.setAmount(new BigDecimal(transferRequest.getAmount()));
        insideAccountTransfer.setId(getIdGenerator().generateChargingId());
        insideAccountTransfer.setTime(System.currentTimeMillis());

        return insideAccountTransfer;
    }

    static BetweenAccountsTransfer convertBetweenAccountsTransferRequest(TransferRequest transferRequest) {
        BetweenAccountsTransfer betweenAccountsTransfer = new BetweenAccountsTransfer();
        betweenAccountsTransfer.setAccountToId(Long.valueOf(transferRequest.getAccountToId()));
        betweenAccountsTransfer.setAccountFromId(Long.valueOf(transferRequest.getAccountFromId()));
        betweenAccountsTransfer.setAmount(new BigDecimal(transferRequest.getAmount()));
        betweenAccountsTransfer.setId(getIdGenerator().generateTransferId());
        betweenAccountsTransfer.setTime(System.currentTimeMillis());

        return betweenAccountsTransfer;
    }

    private static IdGenerator getIdGenerator() {
        return IdGenerator.getInstance();
    }
}
