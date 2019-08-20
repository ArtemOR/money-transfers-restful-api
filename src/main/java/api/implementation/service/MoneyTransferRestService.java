package api.implementation.service;

import api.implementation.exception.ExceptionList;
import api.implementation.exception.MoneyTransferException;
import api.implementation.model.Account;
import api.implementation.model.AccountTransfer;
import api.implementation.model.AccountType;
import api.implementation.model.User;
import api.implementation.model.request.AccountRequest;
import api.implementation.model.request.TransferRequest;
import api.implementation.model.request.UserRequest;
import api.implementation.model.transfer.BetweenAccountsTransfer;
import api.implementation.model.transfer.InsideAccountTransfer;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static api.implementation.service.MoneyTransferModelConverver.*;
import static api.implementation.service.MoneyTransferRequestValidator.*;
import static api.implementation.service.StringConstants.*;

public class MoneyTransferRestService {

    private static Gson gson = new Gson();



    //store all created users
    static Map<String, User> users = new HashMap<>();

    //store all created accounts
    static Map<Long, Account> accounts = new HashMap<>();

    //store all transactions of transfers
    private static Map<Long, AccountTransfer> accountTransfers = new HashMap<>();

    public static String createUser(Request request, Response response) {
        response.type(CONTENT_TYPE);

        //read the body of request
        String requestBody = request.body();
        UserRequest userRequest = gson.fromJson(requestBody, UserRequest.class);
        assertNotNull(userRequest);
        validateUser(userRequest);
        User user = convertUserRequest(userRequest);
        users.put(user.getPassportId(), user);

        //generate the response
        response.status(HttpStatus.OK_200);
        response.body(gson.toJson(users.get(user.getPassportId())));

        return response.body();
    }

    public static String createAccount(Request request, Response response) {
        response.type(CONTENT_TYPE);

        String requestBody = request.body();
        AccountRequest accountRequest = gson.fromJson(requestBody, AccountRequest.class);

        //check if it is possible to create an object from json
        assertNotNull(accountRequest);
        validateAccount(accountRequest);
        Account account = convertAccountRequest(accountRequest);
        accounts.put(account.getId(), account);

        //generate the response
        response.status(HttpStatus.OK_200);
        response.body(gson.toJson(accounts.get(account.getId())));

        return response.body();
    }

    public static String getAllUsers(Request request, Response response) {
        response.type(CONTENT_TYPE);
        response.status(HttpStatus.OK_200);
        return gson.toJson(users.values());
    }

    public static String getUserByPassportId(Request request, Response response) {
        response.type(CONTENT_TYPE);
        String passportId = request.params(PASSPORT_ID_PARAM);
        assertUserExist(passportId);
        response.status(HttpStatus.OK_200);
        User userResponse = User.newInstance(users.get(passportId));
        userResponse.setAccounts(accounts.values().stream().filter(acc->acc.getPassportId().equals(passportId)).collect(Collectors.toList()));
        return gson.toJson(userResponse);
    }

    public static String getAllAccounts(Request request, Response response) {
        response.type(CONTENT_TYPE);
        response.status(HttpStatus.OK_200);
        return gson.toJson(accounts.values());
    }

    public static String getAccountByAccountId(Request request, Response response) {
        response.type(CONTENT_TYPE);
        Long accountId = Long.valueOf(request.params(ACCOUNT_ID_PARAM));
        assertAccountExist(accountId);
        response.status(HttpStatus.OK_200);
        return gson.toJson(accounts.get(accountId));
    }

    public static String getTransfersHistory(Request request, Response response) {
        response.type(CONTENT_TYPE);

        List<AccountTransfer> transfers;
        String accountId;

        // using param accountToId
        if (Objects.nonNull(request.queryParams(ACCOUNT_TO_ID_PARAM))) {
            accountId = request.queryParams(ACCOUNT_TO_ID_PARAM);
            transfers = accountTransfers.values().stream().filter(at -> at.getAccountToId().equals(Long.valueOf(accountId))).collect(Collectors.toList());
        }

        //using param accountFromId
        else if (Objects.nonNull(request.queryParams(ACCOUNT_FROM_ID_PARAM))) {
            accountId = request.queryParams(ACCOUNT_FROM_ID_PARAM);
            transfers = accountTransfers.values()
                    .stream()
                    .filter(at -> at.getClass().equals(BetweenAccountsTransfer.class))
                    .filter(at -> ((BetweenAccountsTransfer) at).getAccountFromId().equals(Long.valueOf(accountId)))
                    .collect(Collectors.toList());
        }

        //no params: All transfers
        else {
            response.status(HttpStatus.OK_200);
            return gson.toJson(accountTransfers.values());
        }

        //check if transfers for this account exist
        assertTransfersExist(transfers, accountId);
        response.status(HttpStatus.OK_200);
        return gson.toJson(transfers);
    }

    public static String addMoneyToAccount(Request request, Response response) {
        response.type(CONTENT_TYPE);

        String requestBody = request.body();
        TransferRequest transferRequest = gson.fromJson(requestBody, TransferRequest.class);

        assertNotNull(transferRequest);
        validateRecharging(transferRequest);
        InsideAccountTransfer transfer = convertTransferRequest(transferRequest);

        Account account = accounts.get(transfer.getAccountToId());

        //add money to balance
        account.setMoneyBalance(account.getMoneyBalance().add(transfer.getAmount()));

        //add object for transfers history
        accountTransfers.put(transfer.getId(), transfer);

        response.status(HttpStatus.OK_200);

        return gson.toJson(transfer);
    }

    public static String transferMoneyBetweenAccounts(Request request, Response response) {
        response.type(CONTENT_TYPE);

        String requestBody = request.body();
        TransferRequest transferRequest = gson.fromJson(requestBody, TransferRequest.class);

        assertNotNull(transferRequest);
        validateTransferBetweenAccounts(transferRequest);
        BetweenAccountsTransfer betweenAccountsTransfer = convertBetweenAccountsTransferRequest(transferRequest);

        //updates balances for both accounts
        Account accountFrom = accounts.get(betweenAccountsTransfer.getAccountFromId());
        Account accountTo = accounts.get(betweenAccountsTransfer.getAccountToId());
        BigDecimal value = betweenAccountsTransfer.getAmount();
        accountFrom.setMoneyBalance(accountFrom.getMoneyBalance().add(value.negate()));
        accountTo.setMoneyBalance(accountTo.getMoneyBalance().add(value));

        //create object for transfers history
        accountTransfers.put(betweenAccountsTransfer.getId(), betweenAccountsTransfer);

        response.status(HttpStatus.OK_200);
        response.body(gson.toJson(Arrays.asList(accountFrom, accountTo)));

        return response.body();
    }

    //TODO: it is better not to remove users and its binded accounts, but make it NOT ACTIVE. For further improvement
    public static String deleteUserByPassportId(Request request, Response response) {
        response.type(CONTENT_TYPE);

        String passportId = request.params(PASSPORT_ID_PARAM);

        assertUserExist(passportId);

        users.remove(passportId);
        response.status(HttpStatus.NO_CONTENT_204);
        response.body();

        //also necessary to delete all accounts that belongs to user
        List<Long> accountsIdToDelete = accounts.values()
                .stream()
                .filter(account -> Objects.equals(account.getPassportId(), passportId))
                .map(Account::getId)
                .collect(Collectors.toList());
        accountsIdToDelete.forEach(a -> accounts.remove(a));

        return gson.toJson(users.get(passportId));
    }

    //TODO: it is better not to remove account, but make it NOT ACTIVE. For further improvement
    public static String deleteAccountByAccountId(Request request, Response response) {
        response.type(CONTENT_TYPE);

        Long accountId = Long.valueOf(request.params(ACCOUNT_ID_PARAM));

        assertAccountExist(accountId);

        accounts.remove(accountId);
        response.status(HttpStatus.NO_CONTENT_204);

        return gson.toJson(accounts.get(accountId));
    }

    public static void generateException(MoneyTransferException moneyTransferException, Request request, Response response) {
        response.status(moneyTransferException.getHttpResponseCode());
        response.type(CONTENT_TYPE);
        response.body(gson.toJson(moneyTransferException));
    }

    public static String handleInternalServerError(Request request, Response response) {
        response.status(HttpStatus.BAD_REQUEST_400);
        response.type(CONTENT_TYPE);
        return gson.toJson(new MoneyTransferException(ExceptionList.BAD_REQUEST));
    }

    public static void generateException(NumberFormatException e, Request request, Response response) {
        MoneyTransferException moneyTransferException = new MoneyTransferException(ExceptionList.WRONG_NUMBER_FORMAT);
        response.status(moneyTransferException.getHttpResponseCode());
        response.type(CONTENT_TYPE);
        response.body(gson.toJson(moneyTransferException));
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

}
