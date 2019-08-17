package api.rest;

import api.helper.IdGenerator;
import api.model.*;
import api.model.transfer.AccountTransfer;
import api.model.transfer.BetweenAccountsTransfer;
import api.model.transfer.InsideAccountTransfer;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static api.helper.MoneyTransferExceptionGenerator.*;
import static spark.Spark.*;

public class MoneyTransfersRorRest {
    private static final String ACCEPT_TYPE = "application/json";
    private static final String CONTENT_TYPE = "application/json";
    private static final String PASSPORT_ID_PARAM = "passportId";
    private static final String USER_PASSPORT_ID_PARAM = "userPassportId";
    private static final String ACCOUNT_ID_PARAM = "accountId";
    private static final String ACCOUNT_TO_ID_PARAM = "accountToId";
    private static final String ACCOUNT_FROM_ID_PARAM = "accountFromId";
    private static final String AMOUNT_PARAM = "amount";

    static Logger logger = LoggerFactory.getLogger(MoneyTransfersRorRest.class);

    //store all created users
    private static Map<String, User> users = new HashMap<>();

    //store all created accounts
    private static Map<Long, Account> accounts = new HashMap<>();

    //store all transactions of transfers
    private static Map<Long, AccountTransfer> accountTransfers = new HashMap<>();

    private static Gson gson = new Gson();

    public static void main(String[] args) {
        createSomeTestObjects();

        port(8082);

        post("/user", ACCEPT_TYPE, (request, response) -> {
            response.type(CONTENT_TYPE);
            String json_user = request.body();
            User user = gson.fromJson(json_user, User.class);
            if (user == null) {
                return generateMissingMandatoryParametersException(response, request.body());
            }
            String passportId = user.getPassportId();
            if (passportId == null) {
                return generateMissingMandatoryParametersException(response, PASSPORT_ID_PARAM);
            }
            if (user.getName() == null) {
                return generateMissingMandatoryParametersException(response, "name");
            }
            if (users.containsKey(passportId)) {
                return generateUserAlreadyExistException(response, passportId);
            }

            long userId = getIdGenerator().generateUserId();
            user.setId(userId);
            users.put(user.getPassportId(), user);
            response.status(200);
            response.body(gson.toJson(users.get(passportId)));

            return response.body();
        });

        get("/users", ACCEPT_TYPE, (request, response) -> {
            response.type(CONTENT_TYPE);
            return gson.toJson(users.values());
        });

        get("/user/:passportId", ACCEPT_TYPE, (request, response) -> {
            response.type(CONTENT_TYPE);
            String passportId = request.params(PASSPORT_ID_PARAM);
            if (!users.containsKey(passportId)) {
                return generateUserNotFoundException(response, passportId);
            }
            return gson.toJson(users.get(passportId));
        });

        delete("/user/:passportId", (request, response) -> {
            response.type(CONTENT_TYPE);
            String passportId = request.params(PASSPORT_ID_PARAM);
            if (!users.containsKey(passportId)) {
                return generateUserNotFoundException(response, passportId);
            }
            users.remove(passportId);
            response.status(204);
            return response;
        });

        post("/account", ACCEPT_TYPE, (request, response) -> {
            response.type(CONTENT_TYPE);
            String json_account = request.body();
            Account account = gson.fromJson(json_account, Account.class);
            if (account == null) {
                return generateMissingMandatoryParametersException(response, "request");
            }
            String userPassportId = account.getUserPassportId();
            if (userPassportId == null) {
                return generateMissingMandatoryParametersException(response, USER_PASSPORT_ID_PARAM);
            }

            long accountId = getIdGenerator().generateAccountId();
            account.setId(accountId);
            accounts.put(account.getId(), account);
            response.status(200);
            response.body(gson.toJson(accounts.get(accountId)));

            return response.body();
        });

        get("/accounts", ACCEPT_TYPE, (request, response) -> {
            response.type(CONTENT_TYPE);
            return gson.toJson(accounts.values());

        });

        get("/account/:accountId", ACCEPT_TYPE, (request, response) -> {
            response.type(CONTENT_TYPE);
            Long accountId = Long.valueOf(request.params(ACCOUNT_ID_PARAM));
            if (!accounts.containsKey(accountId)) {
                return generateAccountNotFoundException(response, accountId.toString());
            }
            return gson.toJson(accounts.get(accountId));
        });

        delete("/account/:accountId", (request, response) -> {
            response.type(CONTENT_TYPE);
            Long accountId = Long.valueOf(request.params(ACCOUNT_ID_PARAM));
            if (!accounts.containsKey(accountId)) {
                return generateUserNotFoundException(response, accountId.toString());
            }
            accounts.remove(accountId);
            response.status(204);
            return response;
        });

        put("/account/recharge", ACCEPT_TYPE, (request, response) -> {
            response.type(CONTENT_TYPE);
            String json_account = request.body();

            InsideAccountTransfer balanceRecharge = gson.fromJson(json_account, InsideAccountTransfer.class);
            if (balanceRecharge == null) {
                return generateMissingMandatoryParametersException(response, "request");
            }

            Long accountId = balanceRecharge.getAccountToId();
            if (accountId == null) {
                return generateMissingMandatoryParametersException(response, ACCOUNT_ID_PARAM);
            }
            if (!accounts.containsKey(accountId)) {
                return generateAccountNotFoundException(response, accountId.toString());
            }
            Account account = accounts.get(accountId);
            BigDecimal value = balanceRecharge.getAmount();
            account.setMoneyBalance(account.getMoneyBalance().add(value));

            response.status(200);
            response.body(gson.toJson(accounts.get(accountId)));
            balanceRecharge.setTime(System.currentTimeMillis());
            long transferId = getIdGenerator().generateChargingId();
            balanceRecharge.setId(transferId);
            accountTransfers.put(transferId, balanceRecharge);
            return gson.toJson(accountTransfers.get(transferId));
        });

        put("/accounts/transfer", ACCEPT_TYPE, (request, response) -> {
            response.type(CONTENT_TYPE);
            String json_account = request.body();

            BetweenAccountsTransfer betweenAccountsTransfer = gson.fromJson(json_account, BetweenAccountsTransfer.class);
            if (betweenAccountsTransfer == null) {
                return generateMissingMandatoryParametersException(response, "request");
            }

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
            if (value.longValue() < 0) {
                return generateMoneyAmountShouldBePositiveException(response, value.toString());
            }
            if (accountFrom.getMoneyBalance().longValue() + accountFrom.getCreditLimit().longValue() < value.longValue()) {
                return generateNotEnoughMoneyException(response);
            }
            accountFrom.setMoneyBalance(accountFrom.getMoneyBalance().add(value.negate()));
            accountTo.setMoneyBalance(accountTo.getMoneyBalance().add(value));

            betweenAccountsTransfer.setTime(System.currentTimeMillis());
            long transferId = getIdGenerator().generateTransferId();
            betweenAccountsTransfer.setId(transferId);
            accountTransfers.put(transferId, betweenAccountsTransfer);

            response.status(200);
            response.body(gson.toJson(Arrays.asList(accountFrom, accountTo)));
            return response.body();
        });

        get("/accounts/transfers", ACCEPT_TYPE, (request, response) -> {
            response.type(CONTENT_TYPE);
            List<AccountTransfer> transfers;
            String accountId;

            if (request.queryParams(ACCOUNT_TO_ID_PARAM) != null) {
                accountId = request.queryParams(ACCOUNT_TO_ID_PARAM);
                transfers = accountTransfers.values().stream().filter(at -> at.getAccountToId().equals(Long.valueOf(accountId))).collect(Collectors.toList());
            } else if (request.queryParams(ACCOUNT_FROM_ID_PARAM) != null) {
                accountId = request.queryParams(ACCOUNT_FROM_ID_PARAM);
                transfers = accountTransfers.values().stream().filter(at -> at.getClass().equals(BetweenAccountsTransfer.class))
                        .filter(at -> ((BetweenAccountsTransfer) at).getAccountFromId().equals(Long.valueOf(accountId)))
                        .collect(Collectors.toList());
            } else {
                return gson.toJson(accountTransfers.values());
            }

            if (transfers.isEmpty()) {
                return generateTransfersNotFoundException(response, accountId);
            }
            return gson.toJson(transfers);
        });

    }

    private static IdGenerator getIdGenerator() {
        return IdGenerator.getInstance();
    }

    private static void createSomeTestObjects() {
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
