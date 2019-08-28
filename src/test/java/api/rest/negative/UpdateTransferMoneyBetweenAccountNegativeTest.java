package api.rest.negative;

import api.rest.TestPayloadBuilder;
import api.rest.MoneyTransferRest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static api.rest.TestConstants.*;
import static api.rest.TestHelper.*;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UpdateTransferMoneyBetweenAccountNegativeTest {
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
    public void MTRA_110201_moneyTransfer_whenAccountDoesNotExist_ThenExceptionIsThrown() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String accountFromId = createAccountWithMoney(PASSPORT_ID1, MONEY_VALUE);
        String accountToId = ID_NON_EXIST;
        String payload = new TestPayloadBuilder().setAccountFromId(accountFromId).setAccountToId(accountToId).setAmount(SMALL_MONEY_VALUE).buildPayload();

        //test
        JsonPath actual = given().body(payload).
                when().put("/accounts/transfer").then().
                assertThat().statusCode(HttpStatus.NOT_FOUND_404).extract().jsonPath();

        //verify
        String actualException = actual.getString(DETAIL_MESSAGE_REQUEST_PARAM);
        assertTrue(actualException.contains(ACCOUNT_NOT_FOUND_EXCEPTION));

        //cleanup
        cleanAccount(accountFromId);
        cleanUser(PASSPORT_ID1);

    }

    @Test
    public void MTRA_110202_moneyTransfer_whenNotEnoughMoney_ThenExceptionIsThrown() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        createUser(USER_NAME2, PASSPORT_ID2);
        String accountFromId = createAccountWithMoney(PASSPORT_ID1, MONEY_VALUE);
        String accountToId = createEmptyAccount(PASSPORT_ID2);
        String payload = new TestPayloadBuilder().setAccountFromId(accountFromId).setAccountToId(accountToId).setAmount(BIG_MONEY_VALUE).buildPayload();

        //test
        JsonPath actual = given().body(payload).
                when().put("/accounts/transfer").then().
                assertThat().statusCode(HttpStatus.BAD_REQUEST_400).extract().jsonPath();

        //verify
        String actualException = actual.getString(DETAIL_MESSAGE_REQUEST_PARAM);
        assertTrue(actualException.contains(NOT_ENOUGH_MONEY_EXCEPTION));

        //cleanup
        cleanUser(PASSPORT_ID1);
        cleanUser(PASSPORT_ID2);
    }

    @Test
    public void MTRA_110203_moneyTransfer_whenAccountFromAndAccountToAreEquals_ThenExceptionIsThrown() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String accountFromId = createAccountWithMoney(PASSPORT_ID1, MONEY_VALUE);
        String payload = new TestPayloadBuilder().setAccountFromId(accountFromId).setAccountToId(accountFromId).setAmount(SMALL_MONEY_VALUE).buildPayload();

        //test
        JsonPath actual = given().body(payload).
                when().put("/accounts/transfer").then().
                assertThat().statusCode(HttpStatus.BAD_REQUEST_400).extract().jsonPath();

        //verify
        String actualException = actual.getString(DETAIL_MESSAGE_REQUEST_PARAM);
        assertTrue(actualException.contains(CHOSE_DIFFERENT_ACCOUNT_IDS_EXCEPTION));

        //cleanup
        cleanUser(PASSPORT_ID1);
    }
}
