package api.rest.positive;

import api.rest.TestPayloadBuilder;
import api.rest.MoneyTransferRest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static api.rest.TestConstants.*;
import static api.rest.TestHelper.*;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class CreateAccountPositiveTest {

    @BeforeClass
    public static void init() {
        MoneyTransferRest.start();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8082;

    }

    @AfterClass
    public static void stopServer() {
        MoneyTransferRest.stop();
    }

    @Test
    public void MTRA_020101_createAccount_whenAccountCreatesWithOnlyPassportID_thenZeroMoneyDebitAccountInResponse() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String payload = new TestPayloadBuilder().setPassportId(PASSPORT_ID1).buildPayload();

        //test
        JsonPath createResult = given().body(payload).
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200).
                extract().jsonPath();

        //verify
        String passportId = createResult.getString(PASSPORT_ID_PARAM);
        String moneyBalance = createResult.getString(MONEY_BALANCE_PARAM);
        String accountType = createResult.getString(ACCOUNT_TYPE_PARAM);
        String creditLimit = createResult.getString(CREDIT_LIMIT_PARAM);
        assertEquals(PASSPORT_ID1, passportId);
        assertEquals(ZERO_MONEY_VALUE, moneyBalance);
        assertEquals(DEBIT_VALUE, accountType);
        assertEquals(ZERO_MONEY_VALUE, creditLimit);

        //cleanup
        String id = createResult.getString(ID_PARAM);
        cleanAccount(id);
        cleanUser(PASSPORT_ID1);
    }

    @Test
    public void MTRA_020102_createAccount_whenTwoSimilarAccountCreates_thenUserHasTwoAccounts() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String payload = new TestPayloadBuilder().setPassportId(PASSPORT_ID1).setMoneyBalance(MONEY_VALUE).buildPayload();

        //test
        given().body(payload).
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200);

        given().body(payload).
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200);

        //verify
        JsonPath readResult = get("/users/" + PASSPORT_ID1).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).
                        extract().jsonPath();

        int accountNumber = readResult.getList(ACCOUNTS_PARAM).size();
        assertEquals(2, accountNumber);

        //cleanup
        ArrayList accounts = (ArrayList) readResult.getList(ACCOUNTS_PARAM);
        for (Object map : accounts) {
            cleanAccount(String.valueOf(((HashMap) map).get(ID_PARAM)));
        }
        cleanUser(PASSPORT_ID1);
    }

    @Test
    public void MTRA_020103_createAccount_whenAccountCreatesWithPositiveBalance_thenDebitAccountWithMoneyBalanceInResponse() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String payload = new TestPayloadBuilder().setPassportId(PASSPORT_ID1).setMoneyBalance(MONEY_VALUE).buildPayload();

        //test
        JsonPath createResult = given().body(payload).
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200).
                extract().jsonPath();

        String id = createResult.getString(ID_PARAM);

        //verify
        JsonPath readResult = get("/accounts/" + id).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).
                        extract().jsonPath();

        String passportId = readResult.getString(PASSPORT_ID_PARAM);
        String moneyBalance = readResult.getString(MONEY_BALANCE_PARAM);
        String accountType = readResult.getString(ACCOUNT_TYPE_PARAM);
        String creditLimit = readResult.getString(CREDIT_LIMIT_PARAM);
        assertEquals(PASSPORT_ID1, passportId);
        assertEquals(MONEY_VALUE, moneyBalance);
        assertEquals(DEBIT_VALUE, accountType);
        assertEquals(ZERO_MONEY_VALUE, creditLimit);

        //cleanup
        cleanAccount(id);
        cleanUser(PASSPORT_ID1);
    }

    @Test
    public void MTRA_020104_createAccount_whenAccountCreatesWithCreditLimit_thenCreditAccountWithCreditMoneyAccountInResponse() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String payload = new TestPayloadBuilder().setPassportId(PASSPORT_ID1).setCreditLimit(MONEY_VALUE).buildPayload();

        //test
        JsonPath createResult = given().body(payload).
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200).
                extract().jsonPath();

        //verify
        String passportId = createResult.getString(PASSPORT_ID_PARAM);
        String moneyBalance = createResult.getString(MONEY_BALANCE_PARAM);
        String accountType = createResult.getString(ACCOUNT_TYPE_PARAM);
        String creditLimit = createResult.getString(CREDIT_LIMIT_PARAM);
        assertEquals(PASSPORT_ID1, passportId);
        assertEquals(ZERO_MONEY_VALUE, moneyBalance);
        assertEquals(CREDIT_VALUE, accountType);
        assertEquals(MONEY_VALUE, creditLimit);

        //cleanup
        String id = createResult.getString(ID_PARAM);
        cleanAccount(id);
        cleanUser(PASSPORT_ID1);
    }

    @Test
    public void MTRA_020105_createAccount_whenAccountCreatesWithCreditLimitAndMoneyBalance_thenCreditAccountWithMoneyInResponse() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String payload = new TestPayloadBuilder().setPassportId(PASSPORT_ID1).setMoneyBalance(MONEY_VALUE).setCreditLimit(MONEY_VALUE).buildPayload();

        //test
        JsonPath createResult = given().body(payload).
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200).
                extract().jsonPath();

        //verify
        String passportId = createResult.getString(PASSPORT_ID_PARAM);
        String moneyBalance = createResult.getString(MONEY_BALANCE_PARAM);
        String accountType = createResult.getString(ACCOUNT_TYPE_PARAM);
        String creditLimit = createResult.getString(CREDIT_LIMIT_PARAM);
        assertEquals(PASSPORT_ID1, passportId);
        assertEquals(MONEY_VALUE, moneyBalance);
        assertEquals(CREDIT_VALUE, accountType);
        assertEquals(MONEY_VALUE, creditLimit);

        //cleanup
        String id = createResult.getString(ID_PARAM);
        cleanAccount(id);
        cleanUser(PASSPORT_ID1);
    }
}
