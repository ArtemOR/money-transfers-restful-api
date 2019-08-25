package api.rest.positive;

import api.rest.MoneyTransferRest;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static api.rest.TestConstants.*;
import static api.rest.TestHelper.cleanUser;
import static api.rest.TestHelper.createUser;
import static io.restassured.RestAssured.get;
import static org.junit.Assert.assertTrue;

public class GetAllUsersPositiveTest {
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
    public void MTRA_030101_getAllUsers_whenMethodIsCalls_thenUsersReturn() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        createUser(USER_NAME2, PASSPORT_ID2);

        //test
        JsonPath readResult = get("/users/getAll").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).
                        extract().jsonPath();

        //verify
        ArrayList users = (ArrayList) readResult.getList("");
        assertTrue(users.size() >= 2);

        //cleanup
        cleanUser(PASSPORT_ID1);
        cleanUser(PASSPORT_ID2);
    }
}
