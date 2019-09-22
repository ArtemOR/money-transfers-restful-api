package api.rest;

import api.implementation.exception.MoneyTransferException;
import api.implementation.controller.MoneyTransferRestController;
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

        threadPool(100);
        port(8082);
        after((request, response) -> response.type(ACCEPT_TYPE));

        post("/users", MoneyTransferRestController::createUser);

        post("/accounts", MoneyTransferRestController::createAccount);

        get("/users/getAll", MoneyTransferRestController::getAllUsers);

        get("/users/:passportId", MoneyTransferRestController::getUserByPassportId);

        get("/accounts/getAll", MoneyTransferRestController::getAllAccounts);

        get("/accounts/:accountId", MoneyTransferRestController::getAccountByAccountId);

        get("/accounts/transfers/getAll", MoneyTransferRestController::getAllTransfersHistory);

        get("/accounts/transfers/accountFromId/:accountFromId", MoneyTransferRestController::getTransfersHistoryBySenderId);

        get("/accounts/transfers/accountToId/:accountToId", MoneyTransferRestController::getTransfersHistoryByReceiverId);

        put("/accounts/recharge", MoneyTransferRestController::addMoneyToAccount);

        put("/accounts/transfer", MoneyTransferRestController::transferMoneyBetweenAccounts);

        delete("/users/:passportId", MoneyTransferRestController::deleteUserByPassportId);

        delete("/accounts/:accountId", MoneyTransferRestController::deleteAccountByAccountId);

        delete("/accounts/transfers/:transferId", MoneyTransferRestController::deleteTransferByTransferId);

        exception(MoneyTransferException.class, MoneyTransferRestController::generateException);

        exception(NumberFormatException.class, MoneyTransferRestController::generateException);

        internalServerError(MoneyTransferRestController::handleInternalServerError);

        post("/testData", MoneyTransferRestController::createSomeTestObjects);

    }

}
