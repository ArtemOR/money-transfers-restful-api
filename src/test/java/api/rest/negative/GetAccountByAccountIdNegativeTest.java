package api.rest.negative;

import api.rest.MoneyTransferRest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static api.rest.TestConstants.*;
import static io.restassured.RestAssured.get;
import static org.junit.Assert.assertTrue;

public class GetAccountByAccountIdNegativeTest {
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
    public void MTRA_060201_getAccount_whenMethodIsCallsWithNonexistentAccountId_thenExceptionIsThrown() {
        //test
        JsonPath readResult = get("/accounts/" + ID_NON_EXIST).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404).
                        extract().jsonPath();

        //verify
        String actualException = readResult.getString(DETAIL_MESSAGE_REQUEST_PARAM);
        assertTrue(actualException.contains(ACCOUNT_NOT_FOUND_EXCEPTION));
    }
}
