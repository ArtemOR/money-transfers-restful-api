package api.rest.positive;

import api.rest.MoneyTransferTest;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import static api.rest.TestConstants.PASSPORT_ID1;
import static api.rest.TestConstants.USER_NAME1;
import static api.rest.TestHelper.createUser;
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;

public class DeleteUserByPassportIdPositiveTest extends MoneyTransferTest {

    @Test
    public void MTRA_120101_deleteUser_whenPassportIdExist_thenOperationExecute() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);


        //test
        delete("/users/" + PASSPORT_ID1).then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT_204).
                extract().jsonPath();

        //verify
        get("/users/" + PASSPORT_ID1).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404);

    }
}
