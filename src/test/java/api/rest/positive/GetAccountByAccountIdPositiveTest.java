package api.rest.positive;

import api.rest.MoneyTransferTest;
import io.restassured.path.json.JsonPath;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import static api.rest.TestConstants.*;
import static api.rest.TestHelper.*;
import static io.restassured.RestAssured.get;
import static org.junit.Assert.assertEquals;

public class GetAccountByAccountIdPositiveTest extends MoneyTransferTest {

    @Test
    public void MTRA_060101_getAccount_whenMethodIsCallsWithExistentAccountId_thenAccountReturns() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String accountId = createEmptyAccount(PASSPORT_ID1);

        //test
        JsonPath readResult = get("/accounts/" + accountId).then()
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
