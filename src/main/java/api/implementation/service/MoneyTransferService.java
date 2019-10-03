package api.implementation.service;

import api.implementation.exception.ExceptionList;
import api.implementation.exception.MoneyTransferException;
import api.implementation.idgenerator.IdGenerator;
import api.implementation.model.Account;
import api.implementation.model.AccountTransfer;
import api.implementation.model.User;
import api.implementation.model.transfer.BetweenAccountsTransfer;
import api.implementation.model.transfer.InsideAccountTransfer;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class MoneyTransferService implements IMoneyTransferService {

    //store all created users
    private final Map<String, User> users = new ConcurrentHashMap<>();

    //store all created accounts
    private final Map<Long, Account> accounts = new ConcurrentHashMap<>();

    //store all transactions of transfers
    private final Map<Long, AccountTransfer> transfers = new ConcurrentHashMap<>();

    private Lock lock = new ReentrantLock();

    @Override
    public User createUser(final User user) {
        assertNotNull(user);

        //check if this object already exist
        String passportId = user.getPassportId();
        user.setId(getIdGenerator().generateUserId());
        User created = users.putIfAbsent(passportId, user);
        if (Objects.nonNull(created)) {
            throw new MoneyTransferException(ExceptionList.USER_ALREADY_EXIST, passportId);
        }
        return user;
    }

    @Override
    public Account createAccount(final Account account) {
        assertNotNull(account);

        lock.lock();
        try {
            //check if user exist
            if (!users.containsKey(account.getPassportId())) {
                throw new MoneyTransferException(ExceptionList.USER_NOT_FOUND, account.getPassportId());
            }
            account.setId(getIdGenerator().generateAccountId());
            accounts.put(account.getId(), account);
        } finally {
            lock.unlock();
        }
        return account;
    }

    @Override
    public Collection<User> getAllUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    @Override
    public User getUserByPassportId(final String passportId) {
        User userResponse;
        lock.lock();
        try {
            User userFromMap = users.get(passportId);
            //check if user exist
            if (Objects.isNull(userFromMap)) {
                throw new MoneyTransferException(ExceptionList.USER_NOT_FOUND, passportId);
            }
            userResponse = User.newInstance(userFromMap);
            userResponse.setAccounts(accounts.values().stream().
                    filter(acc -> acc.getPassportId().equals(passportId)).
                    collect(Collectors.toList()));
        } finally {
            lock.unlock();
        }

        return userResponse;
    }

    @Override
    public Collection<Account> getAllAccounts() {
        return Collections.unmodifiableCollection(accounts.values());
    }

    @Override
    public Account getAccountByAccountId(final Long accountId) {
        Account account = accounts.get(accountId);
        //check if account exist
        if (Objects.isNull(account)) {
            throw new MoneyTransferException(ExceptionList.ACCOUNT_NOT_FOUND, accountId.toString());
        }
        return account;
    }

    @Override
    public Collection<AccountTransfer> getAllTransfersHistory() {
        return Collections.unmodifiableCollection(transfers.values());
    }

    @Override
    public Collection<AccountTransfer> getTransfersHistoryBySenderId(final Long accountId) {
        assertAccountExist(accountId);

        return transfers.values().stream()
                .filter(at -> at.getClass().equals(BetweenAccountsTransfer.class))
                .filter(at -> ((BetweenAccountsTransfer) at).getAccountFromId().equals(accountId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<AccountTransfer> getTransfersHistoryByReceiverId(final Long accountId) {
        assertAccountExist(accountId);

        return transfers.values().stream().
                filter(at -> at.getAccountToId().equals(accountId)).
                collect(Collectors.toList());
    }

    @Override
    public void addMoneyToAccount(final InsideAccountTransfer transfer) {
        assertNotNull(transfer);

        Long accountId = transfer.getAccountToId();

        lock.lock();

        try {
            Account account = accounts.get(transfer.getAccountToId());
            //check if account exist
            if (Objects.isNull(account)) {
                throw new MoneyTransferException(ExceptionList.ACCOUNT_NOT_FOUND, accountId.toString());
            }

            //add money to balance
            account.setMoneyBalance(account.getMoneyBalance().add(transfer.getAmount()));
        } finally {
            lock.unlock();
        }
        //add object for transfers history
        transfer.setId(getIdGenerator().generateInsideTransferId());
        transfers.put(transfer.getId(), transfer);
    }

    @Override
    public void transferMoneyBetweenAccounts(final BetweenAccountsTransfer transfer) {
        assertNotNull(transfer);

        //updates balances for both accounts
        final BigDecimal value = transfer.getAmount();
        final Long accountFromId = transfer.getAccountFromId();
        final Long accountToId = transfer.getAccountToId();

        //for the practice of avoiding the deadlock
        if (accountFromId < accountToId) {
            Account accountFrom = accounts.get(accountFromId);
            if (Objects.isNull(accountFrom)) {
                throw new MoneyTransferException(ExceptionList.ACCOUNT_NOT_FOUND, accountFromId.toString());
            }
            synchronized (accountFrom) {
                //check that there is enough money
                if (accountFrom.getMoneyBalance().add(accountFrom.getCreditLimit()).compareTo(transfer.getAmount()) < 0) {
                    throw new MoneyTransferException(ExceptionList.NOT_ENOUGH_MONEY);
                }

                Account accountTo = accounts.get(accountToId);
                if (Objects.isNull(accountTo)) {
                    throw new MoneyTransferException(ExceptionList.ACCOUNT_NOT_FOUND, accountToId.toString());
                }
                synchronized (accountTo) {
                    accountFrom.setMoneyBalance(accountFrom.getMoneyBalance().add(value.negate()));
                    accountTo.setMoneyBalance(accountTo.getMoneyBalance().add(value));
                }
            }
        } else {
            Account accountTo = accounts.get(accountToId);
            if (Objects.isNull(accountTo)) {
                throw new MoneyTransferException(ExceptionList.ACCOUNT_NOT_FOUND, accountToId.toString());
            }
            synchronized (accountTo) {

                Account accountFrom = accounts.get(accountFromId);
                if (Objects.isNull(accountFrom)) {
                    throw new MoneyTransferException(ExceptionList.ACCOUNT_NOT_FOUND, accountFromId.toString());
                }
                synchronized (accountFrom) {
                    //check that there is enough money
                    if (accountFrom.getMoneyBalance().add(accountFrom.getCreditLimit()).compareTo(transfer.getAmount()) < 0) {
                        throw new MoneyTransferException(ExceptionList.NOT_ENOUGH_MONEY);
                    }
                    accountFrom.setMoneyBalance(accountFrom.getMoneyBalance().add(value.negate()));
                    accountTo.setMoneyBalance(accountTo.getMoneyBalance().add(value));
                }
            }
        }

        //create object for transfers history
        transfer.setId(getIdGenerator().generateBetweenTransferId());
        transfers.put(transfer.getId(), transfer);
    }

    @Override
    public void deleteUserByPassportId(final String passportId) {
        lock.lock();
        try {
            User user = users.remove(passportId);
            if (Objects.isNull(user)) {
                throw new MoneyTransferException(ExceptionList.USER_NOT_FOUND, passportId);
            }
            //also necessary to delete all accounts that belongs to user
            List<Long> accountsIdToDelete = accounts.values()
                    .stream()
                    .filter(account -> Objects.equals(account.getPassportId(), passportId))
                    .map(Account::getId)
                    .collect(Collectors.toList());
            accountsIdToDelete.forEach(accounts::remove);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteAccountByAccountId(final Long accountId) {
        Account account = accounts.remove(accountId);
        if (Objects.isNull(account)) {
            throw new MoneyTransferException(ExceptionList.ACCOUNT_NOT_FOUND, accountId.toString());
        }
    }

    @Override
    public void deleteTransferByTransferId(final Long transferId) {
        AccountTransfer transfer = transfers.remove(transferId);
        if (Objects.isNull(transfer)) {
            throw new MoneyTransferException(ExceptionList.TRANSFER_NOT_FOUND, transferId.toString());
        }
    }

    private void assertAccountExist(final Long accountId) {
        //check if account exist
        if (!accounts.containsKey(accountId)) {
            throw new MoneyTransferException(ExceptionList.ACCOUNT_NOT_FOUND, accountId.toString());
        }
    }

    private void assertNotNull(final Object obj) {
        if(Objects.isNull(obj)){
            throw new MoneyTransferException(ExceptionList.OBJECT_IS_NULL);
        }
    }

    private IdGenerator getIdGenerator() {
        return IdGenerator.getInstance();
    }

}
