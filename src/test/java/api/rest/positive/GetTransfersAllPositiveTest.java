package api.rest.positive;

import api.rest.MoneyTransferTest;
import io.restassured.path.json.JsonPath;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import java.util.ArrayList;

import static api.rest.TestConstants.*;
import static api.rest.TestHelper.*;
import static io.restassured.RestAssured.get;
import static org.junit.Assert.assertTrue;

public class GetTransfersAllPositiveTest extends MoneyTransferTest {

    @Test
    public void MTRA_070101_getAllTransfers_whenMethodIsCalls_thenTransfersReturn() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        createUser(USER_NAME2, PASSPORT_ID2);
        String accountFromId = createAccountWithMoney(PASSPORT_ID1, MONEY_VALUE);
        String accountToId = createEmptyAccount(PASSPORT_ID2);
        String transferId = transferMoneyBetweenAccounts(accountFromId, accountToId, SMALL_MONEY_VALUE);

        //test
        JsonPath readResult = get("/accounts/transfers/getAll").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).
                        extract().jsonPath();

        //verify
        ArrayList transfers = (ArrayList) readResult.getList("");
        assertTrue(transfers.size() >= 1);

        //cleanup
        cleanTransfer(transferId);
        cleanUser(PASSPORT_ID1);
        cleanUser(PASSPORT_ID2);
    }
}
