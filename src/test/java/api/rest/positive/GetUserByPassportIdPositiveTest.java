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
import static io.restassured.RestAssured.get;
import static org.junit.Assert.assertEquals;

public class GetUserByPassportIdPositiveTest {
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
    public void MTRA_040101_getUser_whenMethodIsCallsWithExistentPassportId_thenUserReturns() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);

        //test
        JsonPath readResult = get("/users/"+PASSPORT_ID1).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).
                        extract().jsonPath();

        //verify
        String actualName = readResult.getString(NAME_PARAM);
        String actualPassportId = readResult.getString(PASSPORT_ID_PARAM);
        assertEquals(USER_NAME1, actualName);
        assertEquals(PASSPORT_ID1, actualPassportId);

        //cleanup
        cleanUser(PASSPORT_ID1);
    }
}
