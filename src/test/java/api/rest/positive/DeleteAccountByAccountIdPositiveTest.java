package api.rest.positive;

import api.rest.MoneyTransferRest;
import io.restassured.RestAssured;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static api.rest.TestConstants.PASSPORT_ID1;
import static api.rest.TestConstants.USER_NAME1;
import static api.rest.TestHelper.cleanUser;
import static api.rest.TestHelper.createEmptyAccount;
import static api.rest.TestHelper.createUser;
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;

public class DeleteAccountByAccountIdPositiveTest {
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
    public void MTRA_130101_deleteAccount_whenAccountIdExist_thenOperationExecute() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String accountId = createEmptyAccount(PASSPORT_ID1);
        
        //test
        delete("/accounts/"+accountId).then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT_204).
                        extract().jsonPath();

        //verify
        get("/accounts/"+accountId).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404);

        //cleanup
        cleanUser(PASSPORT_ID1);
    }
}
