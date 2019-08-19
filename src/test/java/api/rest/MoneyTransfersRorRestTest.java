package api.rest;

import io.restassured.RestAssured;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MoneyTransfersRorRestTest {

    private String USER_ALREADY_EXIST_MESSAGE_EXCEPTION = "User with provided passportId is already exist: passportId=";
    private String USER_DOES_NOT_EXIST_MESSAGE_EXCEPTION = "User with provided passportId does not exist: passportId=";
    private String NOT_ENOUGH_MONEY_MESSAGE_EXCEPTION = "Not enough money to complete the operation";

    @BeforeClass
    public static void init() {
        MoneyTransfersRorRest.start();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8082;
    }

    @AfterClass
    public static void stopServer() {
        MoneyTransfersRorRest.stop();
    }

    @Test
    public void createUserAndDeleteUser() {
        String PASSPORT_ID = "123pass";
        //positive crweate user
        get("/users/" + PASSPORT_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404);

        given().body("{\n" +
                "\"name\": \"testUser\",\n" +
                "\"passportId\":" + PASSPORT_ID + "\n" +
                "}").
                when().
                post("/users").
                then().
                assertThat().
                statusCode(HttpStatus.OK_200);

        get("/users/" + PASSPORT_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200);

        //negative: trying to create user that already exist
        String createResult = given().body("{\n" +
                "\"name\": \"testUser\",\n" +
                "\"passportId\":" + PASSPORT_ID + "\n" +
                "}").
                when().
                post("/users").
                then().
                assertThat().
                statusCode(HttpStatus.BAD_REQUEST_400).extract().jsonPath().getString("detailMessage");
        assertTrue(createResult.contains(USER_ALREADY_EXIST_MESSAGE_EXCEPTION));


        //positive: delete user
        delete("/users/" + PASSPORT_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT_204);

        //expected: searching for not found user
        get("/users/" + PASSPORT_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404);

        //negative: delete user that already not exist
        String deleteResult = delete("/users/" + PASSPORT_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404).extract().jsonPath().getString("detailMessage");
        assertTrue(deleteResult.contains(USER_DOES_NOT_EXIST_MESSAGE_EXCEPTION));
    }

    @Test
    public void createAccountAndAddMoneyForUser() {
        int INITIAL_MONEY_AMOUNT = 0;
        int AMOUNT_TO_ADD = 133;
        String USER_PASSPORT_ID = "1234f";

        given().body("{\n" +
                "\"name\": \"testUser1\",\n" +
                "\"passportId\": " + USER_PASSPORT_ID + "\n" +
                "}").
                when().
                post("/users").
                then().
                assertThat().
                statusCode(HttpStatus.OK_200);

        get("/users/" + USER_PASSPORT_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200);

        long accountId = given().body("{\n" +
                "\"passportId\": " + USER_PASSPORT_ID + ",\n" +
                "\"moneyBalance\": " + INITIAL_MONEY_AMOUNT + " \n" +
                "}").
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200).
                extract().jsonPath().getLong("id");

        get("/accounts/" + accountId).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).body("moneyBalance", equalTo(INITIAL_MONEY_AMOUNT));

        given().body("{\n" +
                "\"accountToId\": " + accountId + ",\n" +
                "\"amount\": " + AMOUNT_TO_ADD + "\n" +
                "}").when().put("/accounts/recharge").
                then().assertThat().statusCode(HttpStatus.OK_200);

        get("/accounts/" + accountId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).body("moneyBalance", equalTo(AMOUNT_TO_ADD));

        get("/users/" + USER_PASSPORT_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200);

        //whenUserDeletes_thanUsersAccountsAreDeletedToo
        delete("/users/" + USER_PASSPORT_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT_204);

        get("/users/" + USER_PASSPORT_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404);

        get("/accounts/" + accountId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND_404);
    }

    @Test
    public void createTwoAccountsAndTransferMoneyBetweenThem() {
        int FIRST_ACCOUNT_INITIAL_MONEY_AMOUNT = 0;
        int FIRST_ACCOUNT_INITIAL_CREDIT_AMOUNT = 500;
        int SECOND_ACCOUNT_INITIAL_MONEY_AMOUNT = 100;
        int A_HUGE_AMOUNT_OF_MONEY = 1000000;
        int A_LITTLE_AMOUNT_OF_MONEY = 200;
        String CREDIT_ACCOUNT_TYPE = "CREDIT";

        String FIRST_USER_PASSPORT_ID = "1pass";
        String SECOND_USER_PASSPORT_ID = "2pass";

        //create users
        given().body("{\n" +
                "\"name\": \"testUser1\",\n" +
                "\"passportId\": " + FIRST_USER_PASSPORT_ID + "\n" +
                "}").
                when().
                post("/users").
                then().
                assertThat().
                statusCode(HttpStatus.OK_200);

        given().body("{\n" +
                "\"name\": \"testUser2\",\n" +
                "\"passportId\": " + SECOND_USER_PASSPORT_ID + "\n" +
                "}").
                when().
                post("/users").
                then().
                assertThat().
                statusCode(HttpStatus.OK_200);

        //create accounts for users
        long accountFirstId = given().body("{\n" +
                "\"passportId\": " + FIRST_USER_PASSPORT_ID + ",\n" +
                "\"moneyBalance\": " + FIRST_ACCOUNT_INITIAL_MONEY_AMOUNT + ",\n" +
                "\"creditLimit\": " + FIRST_ACCOUNT_INITIAL_CREDIT_AMOUNT + " \n" +
                "}").
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200).body("accountType", equalTo(CREDIT_ACCOUNT_TYPE)).
                extract().jsonPath().getLong("id");

        long accountSecondId = given().body("{\n" +
                "\"passportId\": " + SECOND_USER_PASSPORT_ID + ",\n" +
                "\"moneyBalance\": " + SECOND_ACCOUNT_INITIAL_MONEY_AMOUNT + "\n" +
                "}").
                when().post("/accounts").then().
                assertThat().statusCode(HttpStatus.OK_200).
                extract().jsonPath().getLong("id");

        //negative: try to transfer much money than have
        given().body("{\n" +
                "\"accountFromId\": " + accountFirstId + ",\n" +
                "\"accountToId\": " + accountSecondId + ",\n" +
                "\"amount\": " + A_HUGE_AMOUNT_OF_MONEY + "\n" +
                "}").
                when().put("/accounts/transfer").then().
                assertThat().statusCode(HttpStatus.BAD_REQUEST_400).body("detailMessage", equalTo(NOT_ENOUGH_MONEY_MESSAGE_EXCEPTION));

        //positive: try to use credit money
        Object actual = given().body("{\n" +
                "\"accountFromId\": " + accountFirstId + ",\n" +
                "\"accountToId\": " + accountSecondId + ",\n" +
                "\"amount\": " + A_LITTLE_AMOUNT_OF_MONEY + "\n" +
                "}").
                when().put("/accounts/transfer").then().
                assertThat().statusCode(HttpStatus.OK_200).extract().jsonPath().getList("moneyBalance");

        //Check that both accounts changed for constant amount of money. First increase, second decrease
        List<Integer> expectedValues = new ArrayList<>();
        expectedValues.add(FIRST_ACCOUNT_INITIAL_MONEY_AMOUNT - A_LITTLE_AMOUNT_OF_MONEY);
        expectedValues.add(SECOND_ACCOUNT_INITIAL_MONEY_AMOUNT + A_LITTLE_AMOUNT_OF_MONEY);

        assertEquals(expectedValues, actual);

        //clean:
        delete("/users/" + FIRST_USER_PASSPORT_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT_204);

        delete("/users/" + SECOND_USER_PASSPORT_ID)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT_204);

        //positive: get history of transfers
        get("/account/transfers?accountToId=" + accountSecondId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).body("amount", equalTo(Collections.singletonList(A_LITTLE_AMOUNT_OF_MONEY)));

    }
}
