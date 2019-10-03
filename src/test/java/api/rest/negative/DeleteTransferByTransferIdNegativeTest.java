package api.rest.negative;

import api.rest.MoneyTransferTest;
import io.restassured.path.json.JsonPath;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import static api.rest.TestConstants.*;
import static io.restassured.RestAssured.delete;
import static org.junit.Assert.assertTrue;

public class DeleteTransferByTransferIdNegativeTest extends MoneyTransferTest {

    @Test
    public void MTRA_140201_getTransfersBySenderId_whenTransferDoesNotExist_ThenExceptionIsThrown() {
        //prepare data
        String nonExist = ID_NON_EXIST;

        //test
        JsonPath actual = delete("/accounts/transfers/" + nonExist).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404).
                        extract().jsonPath();


        //verify
        String actualException = actual.getString(DETAIL_MESSAGE_REQUEST_PARAM);
        assertTrue(actualException.contains(TRANSFER_NOT_FOUND_EXCEPTION));
    }

}
