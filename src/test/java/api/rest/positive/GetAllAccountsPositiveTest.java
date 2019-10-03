package api.rest.positive;

import api.rest.MoneyTransferTest;
import io.restassured.path.json.JsonPath;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import java.util.ArrayList;

import static api.rest.TestConstants.PASSPORT_ID1;
import static api.rest.TestConstants.USER_NAME1;
import static api.rest.TestHelper.*;
import static io.restassured.RestAssured.get;
import static org.junit.Assert.assertTrue;

public class GetAllAccountsPositiveTest extends MoneyTransferTest {

    @Test
    public void MTRA_050101_getAllAccounts_whenMethodIsCalls_thenAccountsReturns() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        createEmptyAccount(PASSPORT_ID1);
        createEmptyAccount(PASSPORT_ID1);

        //test
        JsonPath readResult = get("/accounts/getAll").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).
                        extract().jsonPath();

        //verify
        ArrayList accounts = (ArrayList) readResult.getList("");
        assertTrue(accounts.size() >= 2);

        //cleanup
        cleanUser(PASSPORT_ID1);
    }

}
