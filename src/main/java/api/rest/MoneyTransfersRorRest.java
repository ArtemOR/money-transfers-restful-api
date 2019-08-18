package api.rest;

import api.service.RestService;

import static spark.Spark.*;

public class MoneyTransfersRorRest {

    private static final String ACCEPT_TYPE = "application/json";

    public static void main(String[] args) {

        RestService.createSomeTestObjects();

        port(8082);

        post("/users", ACCEPT_TYPE, RestService::createUser);

        post("/accounts", ACCEPT_TYPE, RestService::createAccount);

        get("/users/getAll", ACCEPT_TYPE, RestService::getAllUsers);

        get("/users/:passportId", ACCEPT_TYPE,  RestService::getUserByPassportId);

        get("/accounts/getAll", ACCEPT_TYPE, RestService::getAllAccounts);

        get("/accounts/:accountId", ACCEPT_TYPE, RestService::getAccountByAccountId);

        get("/account/transfers", ACCEPT_TYPE, RestService::getTransfersHistory);

        put("/accounts/recharge", ACCEPT_TYPE, RestService::addMoneyToAccount);

        put("/accounts/transfer", ACCEPT_TYPE, RestService::transferMoneyBetweenAccounts);

        delete("/users/:passportId", ACCEPT_TYPE, RestService::deleteUserByPassportId);

        delete("/accounts/:accountId",  ACCEPT_TYPE, RestService::deleteAccountByAccountId);

    }
    
}
