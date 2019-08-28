package api.rest.negative;

import api.rest.TestPayloadBuilder;
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
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;

public class CreateAccountNegativeTest {
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
    public void MTRA_020201_createAccount_whenAccountCreatesWithoutMandatoryParam_thenExceptionThrows() {
        //prepare data
        String payload = new TestPayloadBuilder().setAmount(MONEY_VALUE).buildPayload();

        //test
        JsonPath createResult = given().body(payload).
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.BAD_REQUEST_400).extract().jsonPath();

        //verify
        String actualException = createResult.getString(DETAIL_MESSAGE_REQUEST_PARAM);
        assertTrue(actualException.contains(MISSING_MANDATORY_PARAMETERS_EXCEPTION));
    }

    @Test
    public void MTRA_020202_createAccount_whenAccountCreatesWithNonexistentPassportId_thenExceptionThrows() {
        //prepare data
        String payload = new TestPayloadBuilder().setPassportId(PASSPORT_ID_NON_EXIST).setAmount(MONEY_VALUE).buildPayload();

        //test
        JsonPath createResult = given().body(payload).
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.NOT_FOUND_404).
                extract().jsonPath();

        //verify
        String actualException = createResult.getString(DETAIL_MESSAGE_REQUEST_PARAM);
        assertTrue(actualException.contains(USER_NOT_FOUND_EXCEPTION));
    }

    @Test
    public void MTRA_020203_createAccount_whenAccountCreatesWithInvalidMoneyValue_thenExceptionThrows() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String payload = new TestPayloadBuilder().setPassportId(PASSPORT_ID1).setMoneyBalance(INVALID_MONEY_VALUE).buildPayload();

        //test
        JsonPath createResult = given().body(payload).
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.BAD_REQUEST_400).
                extract().jsonPath();

        //verify
        String actualException = createResult.getString(DETAIL_MESSAGE_REQUEST_PARAM);
        assertTrue(actualException.contains(WRONG_NUMBER_FORMAT_EXCEPTION));

        //cleanUp
        cleanUser(PASSPORT_ID1);
    }
}

