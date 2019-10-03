package api.rest.negative;

import api.rest.MoneyTransferTest;
import io.restassured.path.json.JsonPath;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import static api.rest.TestConstants.*;
import static api.rest.TestHelper.*;
import static io.restassured.RestAssured.get;
import static org.junit.Assert.assertTrue;

public class GetTransfersBySenderIdNegativeTest extends MoneyTransferTest {

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
