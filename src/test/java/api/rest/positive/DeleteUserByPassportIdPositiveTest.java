package api.rest.positive;

import api.rest.MoneyTransferRest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static api.rest.TestConstants.*;
import static api.rest.TestHelper.cleanUser;
import static api.rest.TestHelper.createUser;
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static org.junit.Assert.assertEquals;

public class DeleteUserByPassportIdPositiveTest {
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
    public void MTRA_120101_deleteUser_whenPassportIdExist_thenOperationExecute() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);


        //test
        delete("/users/"+PASSPORT_ID1).then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT_204).
                        extract().jsonPath();

        //verify
        get("/users/"+PASSPORT_ID1).then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404);

    }
}
