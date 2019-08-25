package api.rest.positive;

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
import static org.junit.Assert.assertEquals;

public class GetAccountByAccountIdPositiveTest {
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
    public void MTRA_060101_getAccount_whenMethodIsCallsWithExistentAccountId_thenAccountReturns() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String accountId = createEmptyAccount(PASSPORT_ID1);

        //test
        JsonPath readResult = get("/accounts/"+accountId).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).
                        extract().jsonPath();

        //verify
        String actualPassportId = readResult.getString(PASSPORT_ID_PARAM);
        assertEquals(PASSPORT_ID1, actualPassportId);

        //cleanup
        cleanUser(PASSPORT_ID1);
    }
}
