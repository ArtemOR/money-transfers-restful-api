package api.rest.negative;

import api.rest.MoneyTransferRest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static api.rest.TestConstants.*;
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static org.junit.Assert.assertTrue;

public class DeleteUserByPassportIdNegativeTest {
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
    public void MTRA_120201_deleteUser_whenPassportIdDoesNotExist_ThenExceptionIsThrown() {
        //test
        JsonPath readResult = delete("/users/" + PASSPORT_ID_NON_EXIST).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404).
                        extract().jsonPath();

        //verify
        String actualException = readResult.getString(DETAIL_MESSAGE_REQUEST_PARAM);
        assertTrue(actualException.contains(USER_NOT_FOUND_EXCEPTION));

    }
}
