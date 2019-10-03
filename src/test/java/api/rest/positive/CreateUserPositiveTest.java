package api.rest.positive;

import api.rest.MoneyTransferTest;
import api.rest.TestPayloadBuilder;
import io.restassured.path.json.JsonPath;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import static api.rest.TestConstants.*;
import static api.rest.TestHelper.cleanUser;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class CreateUserPositiveTest extends MoneyTransferTest {

    @Test
    public void MTRA_010101_createUser_whenUserCreates_thenValidResponseReturns() {
        //before test verify
        get("/users/" + PASSPORT_ID1)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404);

        //prepate data
        String payload = new TestPayloadBuilder().setName(USER_NAME1).setPassportId(PASSPORT_ID1).buildPayload();

        //test
        given().body(payload).
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
