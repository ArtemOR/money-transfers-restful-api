package api.rest;

import api.implementation.exception.MoneyTransferException;
import api.implementation.service.MoneyTransferRestService;
import spark.Spark;

import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.internalServerError;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.threadPool;

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

        post("/users", ACCEPT_TYPE, MoneyTransferRestService::createUser);

        post("/accounts", ACCEPT_TYPE, MoneyTransferRestService::createAccount);

        get("/users/getAll", ACCEPT_TYPE, MoneyTransferRestService::getAllUsers);

        get("/users/:passportId", ACCEPT_TYPE, MoneyTransferRestService::getUserByPassportId);

        get("/accounts/getAll", ACCEPT_TYPE, MoneyTransferRestService::getAllAccounts);

        get("/accounts/:accountId", ACCEPT_TYPE, MoneyTransferRestService::getAccountByAccountId);

        get("/accounts/transfers/getAll", ACCEPT_TYPE, MoneyTransferRestService::getAllTransfersHistory);

        get("/accounts/transfers/accountFromId/:accountFromId", ACCEPT_TYPE, MoneyTransferRestService::getTransfersHistoryBySenderId);

        get("/accounts/transfers/accountToId/:accountToId", ACCEPT_TYPE, MoneyTransferRestService::getTransfersHistoryByReceiverId);

        put("/accounts/recharge", ACCEPT_TYPE, MoneyTransferRestService::addMoneyToAccount);

        put("/accounts/transfer", ACCEPT_TYPE, MoneyTransferRestService::transferMoneyBetweenAccounts);

        delete("/users/:passportId", ACCEPT_TYPE, MoneyTransferRestService::deleteUserByPassportId);

        delete("/accounts/:accountId", ACCEPT_TYPE, MoneyTransferRestService::deleteAccountByAccountId);

        delete("/accounts/transfers/:transferId", ACCEPT_TYPE, MoneyTransferRestService::deleteTransferByTransferId);

        exception(MoneyTransferException.class, MoneyTransferRestService::generateException);

        exception(NumberFormatException.class, MoneyTransferRestService::generateException);

        internalServerError(MoneyTransferRestService::handleInternalServerError);

    }

}
