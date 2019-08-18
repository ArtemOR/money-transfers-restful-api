package api.service;

import api.helper.IdGenerator;
import api.model.Account;
import api.model.AccountType;
import api.model.User;
import api.model.transfer.AccountTransfer;
import api.model.transfer.BetweenAccountsTransfer;
import api.model.transfer.InsideAccountTransfer;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static api.helper.MoneyTransferExceptionGenerator.*;

public class RestService {

    private static Gson gson = new Gson();

    private static final String CONTENT_TYPE = "application/json";
    private static final String PASSPORT_ID_PARAM = "passportId";
    private static final String USER_PASSPORT_ID_PARAM = "userPassportId";
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

        //read the body of request
        String json_user = request.body();
        User user = gson.fromJson(json_user, User.class);

        //check if it is possible to create an object from json
        if (user == null) {
            return generateMissingMandatoryParametersException(response, request.body());
        }

        String passportId = user.getPassportId();

        //check for mandatory params
        if (passportId == null) {
            return generateMissingMandatoryParametersException(response, PASSPORT_ID_PARAM);
        }
        if (user.getName() == null) {
            return generateMissingMandatoryParametersException(response, "name");
        }

        //check is this object already exist
        if (users.containsKey(passportId)) {
            return generateUserAlreadyExistException(response, passportId);
        }

        //generate unique id for user
        long userId = getIdGenerator().generateUserId();
        user.setId(userId);

        users.put(user.getPassportId(), user);

        //generate the response
        response.status(200);
        response.body(gson.toJson(users.get(passportId)));
        response.type(CONTENT_TYPE);

        return response.body();
    }

    public static String createAccount(Request request, Response response) {
        String json_account = request.body();
        Account account = gson.fromJson(json_account, Account.class);

        //check if it is possible to create an object from json
        if (account == null) {
            return generateMissingMandatoryParametersException(response, "request");
        }

        String userPassportId = account.getUserPassportId();

        //check for mandatory params
        if (userPassportId == null) {
            return generateMissingMandatoryParametersException(response, USER_PASSPORT_ID_PARAM);
        }

        //generate unique id for account
        long accountId = getIdGenerator().generateAccountId();
        account.setId(accountId);

        accounts.put(account.getId(), account);

        //generate the response
        response.type(CONTENT_TYPE);
        response.status(200);
        response.body(gson.toJson(accounts.get(accountId)));

        return response.body();
    }

    public static String getAllUsers(Request request, Response response) {
        response.type(CONTENT_TYPE);
        return gson.toJson(users.values());
    }

    public static String getUserByPassportId(Request request, Response response) {
        String passportId = request.params(PASSPORT_ID_PARAM);

        //check if user exist
        if (!users.containsKey(passportId)) {
            return generateUserNotFoundException(response, passportId);
        }
        response.type(CONTENT_TYPE);
        return gson.toJson(users.get(passportId));
    }

    public static String getAllAccounts(Request request, Response response) {
        response.type(CONTENT_TYPE);
        return gson.toJson(accounts.values());
    }

    public static String getAccountByAccountId(Request request, Response response) {
        Long accountId = Long.valueOf(request.params(ACCOUNT_ID_PARAM));

        //check if account exist
        if (!accounts.containsKey(accountId)) {
            return generateAccountNotFoundException(response, accountId.toString());
        }

        response.type(CONTENT_TYPE);
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
            //check if transfers exist
            if (!accountTransfers.isEmpty()) {
                return gson.toJson(accountTransfers.values());
            }
            return generateTransfersNotFoundException(response);
        }

        //check if transfers for this account exist
        if (transfers.isEmpty()) {
            return generateTransfersNotFoundException(response, accountId);
        }
        return gson.toJson(transfers);
    }

    public static String addMoneyToAccount(Request request, Response response) {
        response.type(CONTENT_TYPE);
        String json_account = request.body();

        InsideAccountTransfer balanceRecharge = gson.fromJson(json_account, InsideAccountTransfer.class);

        //check if it is possible to create an object from json
        if (balanceRecharge == null) {
            return generateMissingMandatoryParametersException(response, "request");
        }

        Long accountId = balanceRecharge.getAccountToId();

        //check for mandatory params
        if (accountId == null) {
            return generateMissingMandatoryParametersException(response, ACCOUNT_ID_PARAM);
        }

        //check if the object exist
        if (!accounts.containsKey(accountId)) {
            return generateAccountNotFoundException(response, accountId.toString());
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

        response.status(200);
        response.body(gson.toJson(accounts.get(accountId)));
        return gson.toJson(accountTransfers.get(transferId));
    }

    public static String transferMoneyBetweenAccounts(Request request, Response response) {
        response.type(CONTENT_TYPE);
        String json_account = request.body();

        BetweenAccountsTransfer betweenAccountsTransfer = gson.fromJson(json_account, BetweenAccountsTransfer.class);
        //check if it is possible to create an object from json
        if (betweenAccountsTransfer == null) {
            return generateMissingMandatoryParametersException(response, "request");
        }

        //check for mandatory parameters an that all accounts exist
        Long accountFromId = betweenAccountsTransfer.getAccountFromId();
        if (accountFromId == null) {
            return generateMissingMandatoryParametersException(response, ACCOUNT_FROM_ID_PARAM);
        }
        if (!accounts.containsKey(accountFromId)) {
            return generateAccountNotFoundException(response, accountFromId.toString());
        }
        Long accountToId = betweenAccountsTransfer.getAccountToId();
        if (accountToId == null) {
            return generateMissingMandatoryParametersException(response, ACCOUNT_TO_ID_PARAM);
        }
        if (!accounts.containsKey(accountToId)) {
            return generateAccountNotFoundException(response, accountToId.toString());
        }

        Account accountFrom = accounts.get(accountFromId);
        Account accountTo = accounts.get(accountToId);
        BigDecimal value = betweenAccountsTransfer.getAmount();

        //check that value is positive
        if (value.longValue() < 0) {
            return generateMoneyAmountShouldBePositiveException(response, value.toString());
        }

        //check that there is enough money
        if (accountFrom.getMoneyBalance().longValue() + accountFrom.getCreditLimit().longValue() < value.longValue()) {
            return generateNotEnoughMoneyException(response);
        }

        //updates balances for both accounts
        accountFrom.setMoneyBalance(accountFrom.getMoneyBalance().add(value.negate()));
        accountTo.setMoneyBalance(accountTo.getMoneyBalance().add(value));

        //create object for transfers history
        betweenAccountsTransfer.setTime(System.currentTimeMillis());
        long transferId = getIdGenerator().generateTransferId();
        betweenAccountsTransfer.setId(transferId);
        accountTransfers.put(transferId, betweenAccountsTransfer);

        response.status(200);
        response.body(gson.toJson(Arrays.asList(accountFrom, accountTo)));
        return response.body();
    }

    //TODO: it is better not to remove users and its binded accounts, but make it NOT ACTIVE. For further improvement
    public static String deleteUserByPassportId(Request request, Response response) {
        response.type(CONTENT_TYPE);
        String passportId = request.params(PASSPORT_ID_PARAM);

        //check if collection of users contains such key
        if (!users.containsKey(passportId)) {
            return generateUserNotFoundException(response, passportId);
        }
        users.remove(passportId);
        response.status(204);
        response.body();

        //also necessary to delete all accounts that belongs to user
        List<Long> accountsIdToDelete = accounts.values().stream()
                .filter(account -> Objects.equals(account.getUserPassportId(), passportId)).map(Account::getId).collect(Collectors.toList());
        accountsIdToDelete.forEach(a -> accounts.remove(a));

        return gson.toJson(users.get(passportId));
    }

    //TODO: it is better not to remove account, but make it NOT ACTIVE. For further improvement
    public static String deleteAccountByAccountId(Request request, Response response) {
        response.type(CONTENT_TYPE);
        Long accountId = Long.valueOf(request.params(ACCOUNT_ID_PARAM));

        //check if the necessary object exist
        if (!accounts.containsKey(accountId)) {
            return generateAccountNotFoundException(response, accountId.toString());
        }

        accounts.remove(accountId);
        response.status(204);
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
}
