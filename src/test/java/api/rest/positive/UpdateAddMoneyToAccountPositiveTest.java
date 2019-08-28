package api.rest.positive;

import api.rest.TestPayloadBuilder;
import api.rest.MoneyTransferRest;
import io.restassured.RestAssured;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static api.rest.TestConstants.*;
import static api.rest.TestHelper.*;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class UpdateAddMoneyToAccountPositiveTest {
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
    public void MTRA_100101_addMoney_whenParametersAreValid_thenOperationExecute() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String accountToId = createEmptyAccount(PASSPORT_ID1);
        String payload = new TestPayloadBuilder().setAccountToId(accountToId).setAmount(SMALL_MONEY_VALUE).buildPayload();

        //test
        String transferId = given().body(payload).
                when().put("/accounts/recharge").then().
                assertThat().statusCode(HttpStatus.OK_200).extract().jsonPath().getString(ID_PARAM);

        //verify
        String actualMoneyBalanceAccountTo = get("/accounts/" + accountToId).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).
                        extract().jsonPath().getString(MONEY_BALANCE_PARAM);
        String expectedBalanceAccountTo = SMALL_MONEY_VALUE;
        assertEquals(expectedBalanceAccountTo, actualMoneyBalanceAccountTo);

        //cleanup
        cleanTransfer(transferId);
        cleanUser(PASSPORT_ID1);
    }

}
