package api.implementation.controller;

import api.implementation.converter.MoneyTransferModelConverter;
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
import api.implementation.service.IMoneyTransferService;
import api.implementation.service.MoneyTransferService;
import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Response;

import java.math.BigDecimal;
import java.util.Collection;

import static api.implementation.constants.StringConstants.*;

public class MoneyTransferRestController {

    private static Gson gson = new Gson();
    private static IMoneyTransferService moneyTransferService = new MoneyTransferService();
    private static MoneyTransferModelConverter moneyTransferModelConverter = new MoneyTransferModelConverter();
    private static MoneyTransferRequestValidator moneyTransferRequestValidator = new MoneyTransferRequestValidator();

    public static String createUser(Request request, Response response) {
        //read the body of request
        String requestBody = request.body();
        UserRequest userRequest = gson.fromJson(requestBody, UserRequest.class);
        moneyTransferRequestValidator.validateUser(userRequest);

        //convert dto
        User user = moneyTransferModelConverter.convertUserRequest(userRequest);
        User createdUser = moneyTransferService.createUser(user);

        //generate the response
        response.status(HttpStatus.OK_200);
        response.body(gson.toJson(createdUser));

        return response.body();
    }

    public static String createAccount(Request request, Response response) {
        String requestBody = request.body();
        AccountRequest accountRequest = gson.fromJson(requestBody, AccountRequest.class);

        //check if it is possible to create an object from json
        moneyTransferRequestValidator.assertObjectNotNull(accountRequest);
        moneyTransferRequestValidator.validateAccount(accountRequest);

        //convert dto
        Account account = moneyTransferModelConverter.convertAccountRequest(accountRequest);
        Account createdAccount = moneyTransferService.createAccount(account);

        //generate the response
        response.status(HttpStatus.OK_200);
        response.body(gson.toJson(createdAccount));

        return response.body();
    }

    public static String getAllUsers(Request request, Response response) {
        Collection<User> users = moneyTransferService.getAllUsers();
        response.status(HttpStatus.OK_200);
        return gson.toJson(users);
    }

    public static String getUserByPassportId(Request request, Response response) {
        String passportId = request.params(PASSPORT_ID_PARAM);
        response.status(HttpStatus.OK_200);
        User userResponse = moneyTransferService.getUserByPassportId(passportId);
        return gson.toJson(userResponse);
    }

    public static String getAllAccounts(Request request, Response response) {
        Collection<Account> accounts = moneyTransferService.getAllAccounts();
        response.status(HttpStatus.OK_200);
        return gson.toJson(accounts);
    }

    public static String getAccountByAccountId(Request request, Response response) {
        Long accountId = Long.valueOf(request.params(ACCOUNT_ID_PARAM));
        Account account = moneyTransferService.getAccountByAccountId(accountId);
        response.status(HttpStatus.OK_200);
        return gson.toJson(account);
    }

    public static String getAllTransfersHistory(Request request, Response response) {
        Collection<AccountTransfer> transfers = moneyTransferService.getAllTransfersHistory();
        response.status(HttpStatus.OK_200);
        return gson.toJson(transfers);
    }

    public static String getTransfersHistoryBySenderId(Request request, Response response) {
        String accountId = request.params(ACCOUNT_FROM_ID_PARAM);
        Long accountIdLong = Long.valueOf(accountId);
        Collection<AccountTransfer> transfers = moneyTransferService.getTransfersHistoryBySenderId(accountIdLong);
        //check if transfers for this account exist
        moneyTransferRequestValidator.assertTransfersExist(transfers, accountId);
        response.status(HttpStatus.OK_200);
        return gson.toJson(transfers);
    }

    public static String getTransfersHistoryByReceiverId(Request request, Response response) {
        String accountId = request.params(ACCOUNT_TO_ID_PARAM);
        Long accountIdLong = Long.valueOf(accountId);
        Collection<AccountTransfer> transfers = moneyTransferService.getTransfersHistoryByReceiverId(accountIdLong);

        //check if transfers for this account exist
        moneyTransferRequestValidator.assertTransfersExist(transfers, accountId);
        response.status(HttpStatus.OK_200);
        return gson.toJson(transfers);
    }

    public static String addMoneyToAccount(Request request, Response response) {
        String requestBody = request.body();
        TransferRequest transferRequest = gson.fromJson(requestBody, TransferRequest.class);
        moneyTransferRequestValidator.validateTransferInsideAccount(transferRequest);
        InsideAccountTransfer transfer = moneyTransferModelConverter.convertTransferRequest(transferRequest);

        moneyTransferService.addMoneyToAccount(transfer);

        response.status(HttpStatus.OK_200);
        return gson.toJson(transfer);
    }

    public static String transferMoneyBetweenAccounts(Request request, Response response) {
        String requestBody = request.body();
        TransferRequest transferRequest = gson.fromJson(requestBody, TransferRequest.class);

        moneyTransferRequestValidator.validateTransferRequestBetweenAccounts(transferRequest);
        BetweenAccountsTransfer betweenAccountsTransfer = moneyTransferModelConverter.convertBetweenAccountsTransferRequest(transferRequest);

        moneyTransferService.transferMoneyBetweenAccounts(betweenAccountsTransfer);

        response.status(HttpStatus.OK_200);
        return gson.toJson(betweenAccountsTransfer);
    }

    //TODO: it is better not to remove users and its binded accounts, but make it NOT ACTIVE. For further improvement
    public static String deleteUserByPassportId(Request request, Response response) {
        String passportId = request.params(PASSPORT_ID_PARAM);
        moneyTransferService.deleteUserByPassportId(passportId);

        response.status(HttpStatus.NO_CONTENT_204);

        return gson.toJson(response.body());
    }

    //TODO: it is better not to remove account, but make it NOT ACTIVE. For further improvement
    public static String deleteAccountByAccountId(Request request, Response response) {
        Long accountId = Long.valueOf(request.params(ACCOUNT_ID_PARAM));

        moneyTransferService.deleteAccountByAccountId(accountId);
        response.status(HttpStatus.NO_CONTENT_204);

        return gson.toJson(response.body());
    }

    public static String deleteTransferByTransferId(Request request, Response response) {
        Long transferId = Long.valueOf(request.params(TRANSFER_ID_ID_PARAM));

        moneyTransferService.deleteTransferByTransferId(transferId);
        response.status(HttpStatus.NO_CONTENT_204);

        return gson.toJson(response.body());
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

    public static String createSomeTestObjects(Request request, Response response) {
        User john = new User(1L, "John", "111");
        moneyTransferService.createUser(john);

        User mike = new User(2L, "Mike", "222");
        moneyTransferService.createUser(mike);

        Account johnsFirstAccount = new Account("111", new BigDecimal(1000), AccountType.DEBIT, new BigDecimal(0));
        moneyTransferService.createAccount(johnsFirstAccount);

        Account johnsSecondAccount = new Account("111", new BigDecimal(0), AccountType.CREDIT, new BigDecimal(1000));
        moneyTransferService.createAccount(johnsSecondAccount);

        Account mikesAccount = new Account("222", new BigDecimal(3000), AccountType.DEBIT, new BigDecimal(0));
        moneyTransferService.createAccount(mikesAccount);

        response.status(HttpStatus.NO_CONTENT_204);
        return gson.toJson(response.body());
    }

}
