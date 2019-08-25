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
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class CreateUserPositiveTest {

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
    public void MTRA_010101_createUser_whenUserCreates_thenValidResponseReturns() {
        //before test verify
        get("/users/" + PASSPORT_ID1)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404);

        //test
        given().body("{\n" +
                "\"name\": " + USER_NAME1 + ",\n" +
                "\"passportId\":" + PASSPORT_ID1 + "\n" +
                "}").
                when().
                post("/users").
                then().
                assertThat().
                statusCode(HttpStatus.OK_200);

        //after test verify
        JsonPath getResult = get("/users/" + PASSPORT_ID1)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).extract().jsonPath();

        String actualName = getResult.getString(NAME_PARAM);
        String actualPassportId = getResult.getString(PASSPORT_ID_PARAM);
        assertEquals(USER_NAME1, actualName);
        assertEquals(PASSPORT_ID1, actualPassportId);

        //cleanup
        cleanUser(actualPassportId);
    }
}
