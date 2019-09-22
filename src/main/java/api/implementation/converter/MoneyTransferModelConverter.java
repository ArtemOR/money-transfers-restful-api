package api.implementation.converter;

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

    public User convertUserRequest(final UserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setPassportId(userRequest.getPassportId());

        return user;
    }

    public Account convertAccountRequest(final AccountRequest accountRequest) {
        Account account = new Account();
        account.setPassportId(accountRequest.getPassportId());
        account.setMoneyBalance(Objects.isNull(accountRequest.getMoneyBalance()) ? BigDecimal.ZERO : new BigDecimal(accountRequest.getMoneyBalance()));
        account.setCreditLimit(Objects.isNull(accountRequest.getCreditLimit()) ? BigDecimal.ZERO : new BigDecimal(accountRequest.getCreditLimit()));
        account.setAccountType(BigDecimal.ZERO.equals(account.getCreditLimit()) ? AccountType.DEBIT : AccountType.CREDIT);

        return account;
    }

    public InsideAccountTransfer convertTransferRequest(final TransferRequest transferRequest) {
        InsideAccountTransfer insideAccountTransfer = new InsideAccountTransfer();
        insideAccountTransfer.setAccountToId(Long.valueOf(transferRequest.getAccountToId()));
        insideAccountTransfer.setAmount(new BigDecimal(transferRequest.getAmount()));
        insideAccountTransfer.setTime(System.currentTimeMillis());

        return insideAccountTransfer;
    }

    public BetweenAccountsTransfer convertBetweenAccountsTransferRequest(final TransferRequest transferRequest) {
        BetweenAccountsTransfer betweenAccountsTransfer = new BetweenAccountsTransfer();
        betweenAccountsTransfer.setAccountToId(Long.valueOf(transferRequest.getAccountToId()));
        betweenAccountsTransfer.setAccountFromId(Long.valueOf(transferRequest.getAccountFromId()));
        betweenAccountsTransfer.setAmount(new BigDecimal(transferRequest.getAmount()));
        betweenAccountsTransfer.setTime(System.currentTimeMillis());

        return betweenAccountsTransfer;
    }

}
