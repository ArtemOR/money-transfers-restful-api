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
        String payload = new TestPayloadBuilder().setName(name).setPassportId(passportId).buildPayload();

        given().body(payload).
                when().
                post("/users").
                then().
                assertThat().
                statusCode(HttpStatus.OK_200);

    }

    public static String createAccountWithMoney(String passportId, String moneyBalance) {
        String payload = new TestPayloadBuilder().setPassportId(passportId).setMoneyBalance(moneyBalance).buildPayload();

        return given().body(payload).
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200).extract().jsonPath().getString(ID_PARAM);
    }

    public static String createAccountWithCreditMoney(String passportId, String creditLimit) {
        String payload = new TestPayloadBuilder().setPassportId(passportId).setCreditLimit(creditLimit).buildPayload();

        return given().body(payload).
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200).extract().jsonPath().getString(ID_PARAM);
    }

    public static String createEmptyAccount(String passportId) {
        String payload = new TestPayloadBuilder().setPassportId(passportId).buildPayload();

        return given().body(payload).
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200).extract().jsonPath().getString(ID_PARAM);
    }

    public static String transferMoneyBetweenAccounts(String accountFromId, String accountToId, String value) {
        String payload = new TestPayloadBuilder().setAccountFromId(accountFromId).setAccountToId(accountToId).setAmount(value).buildPayload();

        return given().body(payload).
                when().put("/accounts/transfer").then().
                assertThat().statusCode(HttpStatus.OK_200).extract().jsonPath().getString(ID_PARAM);
    }
}
