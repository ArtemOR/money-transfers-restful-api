package api.rest;

import api.implementation.exception.MoneyTransferException;
import api.implementation.service.MoneyTransferRestService;
import spark.Spark;

import static spark.Spark.*;

public class MoneyTransferRest {

    private static final String ACCEPT_TYPE = "application/json";

    public static void start() {
        MoneyTransferRest.main(null);
        Spark.awaitInitialization();
    }

    public static void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public static void main(String[] args) {

        MoneyTransferRestService.createSomeTestObjects();

        threadPool(100);
        port(8082);
        after((request, response) -> response.type(ACCEPT_TYPE));

        post("/users", MoneyTransferRestService::createUser);

        post("/accounts", MoneyTransferRestService::createAccount);

        get("/users/getAll", MoneyTransferRestService::getAllUsers);

        get("/users/:passportId", MoneyTransferRestService::getUserByPassportId);

        get("/accounts/getAll", MoneyTransferRestService::getAllAccounts);

        get("/accounts/:accountId", MoneyTransferRestService::getAccountByAccountId);

        get("/accounts/transfers/getAll", MoneyTransferRestService::getAllTransfersHistory);

        get("/accounts/transfers/accountFromId/:accountFromId", MoneyTransferRestService::getTransfersHistoryBySenderId);

        get("/accounts/transfers/accountToId/:accountToId", MoneyTransferRestService::getTransfersHistoryByReceiverId);

        put("/accounts/recharge", MoneyTransferRestService::addMoneyToAccount);

        put("/accounts/transfer", MoneyTransferRestService::transferMoneyBetweenAccounts);

        delete("/users/:passportId", MoneyTransferRestService::deleteUserByPassportId);

        delete("/accounts/:accountId", MoneyTransferRestService::deleteAccountByAccountId);

        delete("/accounts/transfers/:transferId", MoneyTransferRestService::deleteTransferByTransferId);

        post("/users/multi-read", MoneyTransferRestService::multiReadUsers);

        post("/accounts/multi-read", MoneyTransferRestService::multiReadAccounts);

        exception(MoneyTransferException.class, MoneyTransferRestService::generateException);

        exception(NumberFormatException.class, MoneyTransferRestService::generateException);

        internalServerError(MoneyTransferRestService::handleInternalServerError);

    }

}
