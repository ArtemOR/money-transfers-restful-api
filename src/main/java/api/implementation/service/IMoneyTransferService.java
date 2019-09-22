package api.implementation.service;

import api.implementation.model.Account;
import api.implementation.model.AccountTransfer;
import api.implementation.model.User;
import api.implementation.model.transfer.BetweenAccountsTransfer;
import api.implementation.model.transfer.InsideAccountTransfer;

import java.util.Collection;

public interface IMoneyTransferService {

    User createUser(User user);

    Account createAccount(Account account);

    Collection<User> getAllUsers();

    User getUserByPassportId(String passportId);

    Collection<Account> getAllAccounts();

    Account getAccountByAccountId(Long accountId);

    Collection<AccountTransfer> getAllTransfersHistory();

    Collection<AccountTransfer> getTransfersHistoryBySenderId(Long accountId);

    Collection<AccountTransfer> getTransfersHistoryByReceiverId(Long accountId);

    void addMoneyToAccount(InsideAccountTransfer transfer);

    void transferMoneyBetweenAccounts(BetweenAccountsTransfer transfer);

    void deleteUserByPassportId(String passportId);

    void deleteAccountByAccountId(Long accountId);

    void deleteTransferByTransferId(Long transferId);

}
