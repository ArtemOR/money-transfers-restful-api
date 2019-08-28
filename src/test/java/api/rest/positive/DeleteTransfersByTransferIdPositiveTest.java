package api.rest.positive;

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
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeleteTransfersByTransferIdPositiveTest {
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
    public void MTRA_140101_deleteTransfersByTransferId_whenTransferIdExist_thenOperationExecute() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        createUser(USER_NAME2, PASSPORT_ID2);
        String accountFromId = createAccountWithMoney(PASSPORT_ID1, MONEY_VALUE);
        String accountToId = createEmptyAccount(PASSPORT_ID2);
        String transferId = transferMoneyBetweenAccounts(accountFromId, accountToId, SMALL_MONEY_VALUE);

        //test
         delete("/accounts/transfers/"+transferId).then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT_204).
                        extract().jsonPath();

        //verify
        get("/accounts/transfers/"+transferId).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404).
                extract().jsonPath();

        //cleanup
        cleanUser(PASSPORT_ID1);
        cleanUser(PASSPORT_ID2);
    }
}
