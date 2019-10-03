package api.rest.positive;

import api.rest.MoneyTransferTest;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import static api.rest.TestConstants.*;
import static api.rest.TestHelper.*;
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;

public class DeleteTransfersByTransferIdPositiveTest extends MoneyTransferTest {

    @Test
    public void MTRA_140101_deleteTransfersByTransferId_whenTransferIdExist_thenOperationExecute() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        createUser(USER_NAME2, PASSPORT_ID2);
        String accountFromId = createAccountWithMoney(PASSPORT_ID1, MONEY_VALUE);
        String accountToId = createEmptyAccount(PASSPORT_ID2);
        String transferId = transferMoneyBetweenAccounts(accountFromId, accountToId, SMALL_MONEY_VALUE);

        //test
        delete("/accounts/transfers/" + transferId).then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT_204).
                extract().jsonPath();

        //verify
        get("/accounts/transfers/" + transferId).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404).
                extract().jsonPath();

        //cleanup
        cleanUser(PASSPORT_ID1);
        cleanUser(PASSPORT_ID2);
    }
}
