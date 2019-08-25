package api.rest.negative;

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
import static org.junit.Assert.assertTrue;

public class TransfersGetBySenderIdNegativeTest {
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
    public void MTRA_090201_getTransfersBySenderId_whenAccountDoesNotExist_ThenExceptionIsThrown() {
        //prepare data
        String accountFromId = ID_NON_EXIST;

        //test
        JsonPath actual = get("/accounts/transfers/accountFromId/" + accountFromId).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404).
                        extract().jsonPath();


        //verify
        String actualException = actual.getString(DETAIL_MESSAGE_REQUEST_PARAM);
        assertTrue(actualException.contains(ACCOUNT_NOT_FOUND_EXCEPTION));
    }

    @Test
    public void MTRA_090202_getTransfersBySenderId_whenNoTransfersForAccountFrom_ThenExceptionIsThrown() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String accountId = createAccountWithMoney(PASSPORT_ID1, MONEY_VALUE);

        //test
        JsonPath actual = get("/accounts/transfers/accountFromId/" + accountId).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404).
                        extract().jsonPath();


        //verify
        String actualException = actual.getString(DETAIL_MESSAGE_REQUEST_PARAM);
        assertTrue(actualException.contains(TRANSFERS_NOT_FOUND_EXCEPTION));

        //cleanup
        cleanAccount(accountId);
        cleanUser(PASSPORT_ID1);
    }
}
