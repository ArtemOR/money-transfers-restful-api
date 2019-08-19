package api.implementation.service;

import api.implementation.exception.ExceptionList;
import api.implementation.exception.MoneyTransfersException;
import api.implementation.helper.IdGenerator;
import api.implementation.model.Account;
import api.implementation.model.AccountType;
import api.implementation.model.User;
import api.implementation.model.transfer.AccountTransfer;
import api.implementation.model.transfer.BetweenAccountsTransfer;
import api.implementation.model.transfer.InsideAccountTransfer;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class RestService {

    private static Gson gson = new Gson();

    private static final String CONTENT_TYPE = "application/json";
    private static final String REQUEST = "request";
    private static final String PASSPORT_ID_PARAM = "passportId";
    private static final String NAME_PARAM = "name";
    private static final String MONEY_BALANCE_PARAM = "moneyBalance";
    private static final String AMOUNT_PARAM = "amount";
    private static final String CREDIT_LIMIT_PARAM = "creditLimit";
    private static final String ACCOUNT_ID_PARAM = "accountId";
    private static final String ACCOUNT_TO_ID_PARAM = "accountToId";
    private static final String ACCOUNT_FROM_ID_PARAM = "accountFromId";

    //store all created users
    private static Map<String, User> users = new HashMap<>();

    //store all created accounts
    private static Map<Long, Account> accounts = new HashMap<>();

    //store all transactions of transfers
    private static Map<Long, AccountTransfer> accountTransfers = new HashMap<>();

    public static String createUser(Request request, Response response) {
        response.type(CONTENT_TYPE);

        //read the body of request
        String json_user = request.body();
        User user = gson.fromJson(json_user, User.class);

        //check if it is possible to create an object from json
        if (user == null) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, REQUEST);
        }

        String passportId = user.getPassportId();

        //check for mandatory params
        if (passportId == null) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, PASSPORT_ID_PARAM);
        }
        if (user.getName() == null) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, NAME_PARAM);
        }

        //check is this object already exist
        if (users.containsKey(passportId)) {
            throw new MoneyTransfersException(ExceptionList.USER_ALREADY_EXIST, passportId);
        }

        //generate unique id for user
        long userId = getIdGenerator().generateUserId();
        user.setId(userId);

        users.put(user.getPassportId(), user);

        //generate the response
        response.status(HttpStatus.OK_200);
        response.body(gson.toJson(users.get(passportId)));

        return response.body();
    }

    public static String createAccount(Request request, Response response) {
        response.type(CONTENT_TYPE);

        String json_account = request.body();
        Account account = gson.fromJson(json_account, Account.class);

        //check if it is possible to create an object from json
        if (account == null) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, REQUEST);
        }

        String passportId = account.getPassportId();

        //check for mandatory params
        if (passportId == null) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, PASSPORT_ID_PARAM);
        }

        //check that user exist
        if (!users.containsKey(passportId)) {
            throw new MoneyTransfersException(ExceptionList.USER_NOT_FOUND, passportId);
        }

        //check that creditLimit and money balance not negative
        if (account.getCreditLimit() != null) {
            if (account.getCreditLimit().longValue() < 0)
                throw new MoneyTransfersException(ExceptionList.MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE, CREDIT_LIMIT_PARAM);
        } else {
            account.setCreditLimit(BigDecimal.ZERO);
        }

        if (account.getMoneyBalance() != null) {
            if (account.getMoneyBalance().longValue() < 0)
                throw new MoneyTransfersException(ExceptionList.MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE, MONEY_BALANCE_PARAM);
        } else {
            account.setMoneyBalance(BigDecimal.ZERO);
        }

        //generate unique id for account
        long accountId = getIdGenerator().generateAccountId();
        account.setId(accountId);
        account.setAccountType(account.getCreditLimit().longValue() > 0 ? AccountType.CREDIT : AccountType.DEBIT);

        accounts.put(account.getId(), account);

        //generate the response
        response.status(HttpStatus.OK_200);
        response.body(gson.toJson(accounts.get(accountId)));

        return response.body();
    }

    public static String getAllUsers(Request request, Response response) {
        response.type(CONTENT_TYPE);
        return gson.toJson(users.values());
    }

    public static String getUserByPassportId(Request request, Response response) {
        response.type(CONTENT_TYPE);

        String passportId = request.params(PASSPORT_ID_PARAM);

        //check if user exist
        if (!users.containsKey(passportId)) {
            throw new MoneyTransfersException(ExceptionList.USER_NOT_FOUND, passportId);
        }
        return gson.toJson(users.get(passportId));
    }

    public static String getAllAccounts(Request request, Response response) {
        response.type(CONTENT_TYPE);

        return gson.toJson(accounts.values());
    }

    public static String getAccountByAccountId(Request request, Response response) {
        response.type(CONTENT_TYPE);

        Long accountId = Long.valueOf(request.params(ACCOUNT_ID_PARAM));

        //check if account exist
        if (!accounts.containsKey(accountId)) {
            throw new MoneyTransfersException(ExceptionList.ACCOUNT_NOT_FOUND, accountId.toString());
        }

        return gson.toJson(accounts.get(accountId));
    }

    public static String getTransfersHistory(Request request, Response response) {
        response.type(CONTENT_TYPE);

        List<AccountTransfer> transfers;
        String accountId;

        //account/transfers?accountToId={?}
        if (request.queryParams(ACCOUNT_TO_ID_PARAM) != null) {
            accountId = request.queryParams(ACCOUNT_TO_ID_PARAM);
            transfers = accountTransfers.values().stream().filter(at -> at.getAccountToId().equals(Long.valueOf(accountId))).collect(Collectors.toList());
        }

        //account/transfers?accountFromId={?}
        else if (request.queryParams(ACCOUNT_FROM_ID_PARAM) != null) {
            accountId = request.queryParams(ACCOUNT_FROM_ID_PARAM);
            transfers = accountTransfers.values().stream().filter(at -> at.getClass().equals(BetweenAccountsTransfer.class))
                    .filter(at -> ((BetweenAccountsTransfer) at).getAccountFromId().equals(Long.valueOf(accountId)))
                    .collect(Collectors.toList());
        }

        //account/transfers --All transfers
        else {
            return gson.toJson(accountTransfers.values());
        }

        //check if transfers for this account exist
        if (transfers.isEmpty()) {
            throw new MoneyTransfersException(ExceptionList.TRANSFER_NOT_FOUND, accountId);
        }

        return gson.toJson(transfers);
    }

    public static String addMoneyToAccount(Request request, Response response) {
        response.type(CONTENT_TYPE);

        String json_account = request.body();

        InsideAccountTransfer balanceRecharge = gson.fromJson(json_account, InsideAccountTransfer.class);

        //check if it is possible to create an object from json
        if (balanceRecharge == null) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, REQUEST);
        }

        Long accountId = balanceRecharge.getAccountToId();

        //check for mandatory params
        if (accountId == null) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, ACCOUNT_ID_PARAM);
        }

        //check if the object exist
        if (!accounts.containsKey(accountId)) {
            throw new MoneyTransfersException(ExceptionList.ACCOUNT_NOT_FOUND, accountId.toString());
        }
        //check that amount of money is positive
        if (Objects.isNull(balanceRecharge.getAmount())) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, AMOUNT_PARAM);
        }
        if (balanceRecharge.getAmount().longValue() < 0) {
            throw new MoneyTransfersException(ExceptionList.MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE, AMOUNT_PARAM);
        }

        Account account = accounts.get(accountId);
        BigDecimal value = balanceRecharge.getAmount();

        //add money to balance
        account.setMoneyBalance(account.getMoneyBalance().add(value));

        //create object for transfers history
        long transferId = getIdGenerator().generateChargingId();
        balanceRecharge.setId(transferId);
        balanceRecharge.setTime(System.currentTimeMillis());
        accountTransfers.put(transferId, balanceRecharge);

        response.status(HttpStatus.OK_200);
        response.body(gson.toJson(accounts.get(accountId)));

        return gson.toJson(accountTransfers.get(transferId));
    }

    public static String transferMoneyBetweenAccounts(Request request, Response response) {
        response.type(CONTENT_TYPE);

        String json_account = request.body();

        BetweenAccountsTransfer betweenAccountsTransfer = gson.fromJson(json_account, BetweenAccountsTransfer.class);
        //check if it is possible to create an object from json
        if (betweenAccountsTransfer == null) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, REQUEST);
        }

        //check for mandatory parameters an that all accounts exist
        Long accountFromId = betweenAccountsTransfer.getAccountFromId();
        if (accountFromId == null) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, ACCOUNT_FROM_ID_PARAM);
        }
        if (!accounts.containsKey(accountFromId)) {
            throw new MoneyTransfersException(ExceptionList.ACCOUNT_NOT_FOUND, accountFromId.toString());
        }
        Long accountToId = betweenAccountsTransfer.getAccountToId();
        if (accountToId == null) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, ACCOUNT_TO_ID_PARAM);
        }
        if (!accounts.containsKey(accountToId)) {
            throw new MoneyTransfersException(ExceptionList.ACCOUNT_NOT_FOUND, accountToId.toString());
        }

        Account accountFrom = accounts.get(accountFromId);
        Account accountTo = accounts.get(accountToId);
        BigDecimal value = betweenAccountsTransfer.getAmount();

        //check that value is positive
        if (value.longValue() < 0) {
            throw new MoneyTransfersException(ExceptionList.MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE, AMOUNT_PARAM);
        }

        //check that there is enough money
        if (accountFrom.getMoneyBalance().longValue() + accountFrom.getCreditLimit().longValue() < value.longValue()) {
            throw new MoneyTransfersException(ExceptionList.NOT_ENOUGH_MONEY);
        }

        //updates balances for both accounts
        accountFrom.setMoneyBalance(accountFrom.getMoneyBalance().add(value.negate()));
        accountTo.setMoneyBalance(accountTo.getMoneyBalance().add(value));

        //create object for transfers history
        betweenAccountsTransfer.setTime(System.currentTimeMillis());
        long transferId = getIdGenerator().generateTransferId();
        betweenAccountsTransfer.setId(transferId);
        accountTransfers.put(transferId, betweenAccountsTransfer);

        response.status(HttpStatus.OK_200);
        response.body(gson.toJson(Arrays.asList(accountFrom, accountTo)));

        return response.body();
    }

    //TODO: it is better not to remove users and its binded accounts, but make it NOT ACTIVE. For further improvement
    public static String deleteUserByPassportId(Request request, Response response) {
        response.type(CONTENT_TYPE);

        String passportId = request.params(PASSPORT_ID_PARAM);

        //check if collection of users contains such key
        if (!users.containsKey(passportId)) {
            throw new MoneyTransfersException(ExceptionList.USER_NOT_FOUND, passportId);
        }
        users.remove(passportId);
        response.status(HttpStatus.NO_CONTENT_204);
        response.body();

        //also necessary to delete all accounts that belongs to user
        List<Long> accountsIdToDelete = accounts.values().stream()
                .filter(account -> Objects.equals(account.getPassportId(), passportId)).map(Account::getId).collect(Collectors.toList());
        accountsIdToDelete.forEach(a -> accounts.remove(a));

        return gson.toJson(users.get(passportId));
    }

    //TODO: it is better not to remove account, but make it NOT ACTIVE. For further improvement
    public static String deleteAccountByAccountId(Request request, Response response) {
        response.type(CONTENT_TYPE);

        Long accountId = Long.valueOf(request.params(ACCOUNT_ID_PARAM));

        //check if the necessary object exist
        if (!accounts.containsKey(accountId)) {
            throw new MoneyTransfersException(ExceptionList.ACCOUNT_NOT_FOUND, accountId.toString());
        }

        accounts.remove(accountId);
        response.status(HttpStatus.NO_CONTENT_204);

        return gson.toJson(accounts.get(accountId));
    }

    public static void createSomeTestObjects() {
        User john = new User(1L, "John", "111");
        users.put(john.getPassportId(), john);

        User mike = new User(2L, "Fred", "222");
        users.put(mike.getPassportId(), mike);

        Account johnsFirst = new Account(11L, "111", new BigDecimal(1000), AccountType.DEBIT, new BigDecimal(0));
        accounts.put(johnsFirst.getId(), johnsFirst);

        Account johnsSecond = new Account(12L, "111", new BigDecimal(0), AccountType.CREDIT, new BigDecimal(1000));
        accounts.put(johnsSecond.getId(), johnsSecond);

        Account mikes = new Account(13L, "222", new BigDecimal(3000), AccountType.DEBIT, new BigDecimal(0));
        accounts.put(mikes.getId(), mikes);
    }

    private static IdGenerator getIdGenerator() {
        return IdGenerator.getInstance();
    }

    public static <T extends Exception> void generateException(MoneyTransfersException moneyTransfersException, Request request, Response response) {
        response.status(moneyTransfersException.getHttpResponseCode());
        response.type(CONTENT_TYPE);
        response.body(gson.toJson(moneyTransfersException));
    }

    public static String handleInternalServerError(Request request, Response response) {
        response.status(HttpStatus.BAD_REQUEST_400);
        response.type(CONTENT_TYPE);
        return gson.toJson(new MoneyTransfersException(ExceptionList.BAD_REQUEST));
    }
}
