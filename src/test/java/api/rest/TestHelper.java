package api.rest;

import org.eclipse.jetty.http.HttpStatus;

import static api.rest.TestConstants.ID_PARAM;
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;

public class TestHelper {

    public static void cleanUser(String passportId) {
        delete("/users/" + passportId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT_204);
    }

    public static void cleanAccount(String accountId) {
        delete("/accounts/" + accountId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT_204);
    }

    public static void cleanTransfer(String transferId) {
        delete("/accounts/transfers/" + transferId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT_204);
    }

    public static void createUser(String name, String passportId) {
        given().body("{\n" +
                "\"name\": " + name + ",\n" +
                "\"passportId\":" + passportId + "\n" +
                "}").
                when().
                post("/users").
                then().
                assertThat().
                statusCode(HttpStatus.OK_200);

    }

    public static String createAccountWithMoney(String passportId, String moneyBalance) {
        return given().body("{\n" +
                "\"passportId\": " + passportId + ",\n" +
                "\"moneyBalance\": " + moneyBalance + " \n" +
                "}").
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200).extract().jsonPath().getString(ID_PARAM);
    }

    public static String createAccountWithCreditMoney(String passportId, String creditLimit) {
        return given().body("{\n" +
                "\"passportId\": " + passportId + ",\n" +
                "\"creditLimit\": " + creditLimit + " \n" +
                "}").
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200).extract().jsonPath().getString(ID_PARAM);
    }

    public static String createEmptyAccount(String passportId) {
        return given().body("{\n" +
                "\"passportId\": " + passportId + "\n" +
                "}").
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200).extract().jsonPath().getString(ID_PARAM);
    }

    public static String transferMoneyBetweenAccounts(String accountFromId, String accountToId, String value) {
        return given().body("{\n" +
                "\"accountFromId\": " + accountFromId + ",\n" +
                "\"accountToId\": " + accountToId + ",\n" +
                "\"amount\": " + value + "\n" +
                "}").
                when().put("/accounts/transfer").then().
                assertThat().statusCode(HttpStatus.OK_200).extract().jsonPath().getString(ID_PARAM);
    }
}
