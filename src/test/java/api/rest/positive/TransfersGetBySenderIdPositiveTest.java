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
import static io.restassured.RestAssured.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransfersGetBySenderIdPositiveTest {
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
    public void MTRA_080101_getTransfersByReceiverId_whenMethodIsCalls_thenTransfersReturn() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        createUser(USER_NAME2, PASSPORT_ID2);
        String accountFromId = createAccountWithMoney(PASSPORT_ID1, MONEY_VALUE);
        String accountToId = createEmptyAccount(PASSPORT_ID2);
        String firstTransferId = transferMoneyBetweenAccounts(accountFromId, accountToId, SMALL_MONEY_VALUE);
        String secondTransferId = transferMoneyBetweenAccounts(accountFromId, accountToId, SMALL_MONEY_VALUE);

        //test
        JsonPath readResult = get("/accounts/transfers/accountFromId/"+accountFromId).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).
                        extract().jsonPath();

        //verify
        ArrayList transfers = (ArrayList) readResult.getList("");
        assertTrue(transfers.size() >= 2);
        for(Object transfer:transfers){
            assertEquals(((HashMap)transfer).get(ACCOUNT_TO_ID_PARAM).toString(), accountToId);
            assertEquals(((HashMap)transfer).get(ACCOUNT_FROM_ID_PARAM).toString(), accountFromId);
            assertEquals(((HashMap)transfer).get(AMOUNT_PARAM).toString(), SMALL_MONEY_VALUE);
        }

        //cleanup
        cleanTransfer(firstTransferId);
        cleanTransfer(secondTransferId);
        cleanUser(PASSPORT_ID1);
        cleanUser(PASSPORT_ID2);
    }
}
