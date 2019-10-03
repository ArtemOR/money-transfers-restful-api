package api.rest.positive;

import api.rest.MoneyTransferTest;
import api.rest.TestPayloadBuilder;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import static api.rest.TestConstants.*;
import static api.rest.TestHelper.*;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class UpdateTransferMoneyBetweenAccountPositiveTest extends MoneyTransferTest {

    @Test
    public void MTRA_110101_moneyTransfer_whenEnoughMoneyOnDebitAccount_thenTransferExecute() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        createUser(USER_NAME2, PASSPORT_ID2);

        String accountFromId = createAccountWithMoney(PASSPORT_ID1, MONEY_VALUE);
        String accountToId = createEmptyAccount(PASSPORT_ID2);
        String payload = new TestPayloadBuilder().setAccountFromId(accountFromId).setAccountToId(accountToId).setAmount(SMALL_MONEY_VALUE).buildPayload();

        //test
        String transferId = given().body(payload).
                when().put("/accounts/transfer").then().
                assertThat().statusCode(HttpStatus.OK_200).extract().jsonPath().getString(ID_PARAM);

        //verify
        String actualMoneyBalanceAccountFrom = get("/accounts/" + accountFromId).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).
                        extract().jsonPath().getString(MONEY_BALANCE_PARAM);
        String expectedBalanceAccountFrom = String.valueOf(Integer.valueOf(MONEY_VALUE) - Integer.valueOf(SMALL_MONEY_VALUE));
        assertEquals(expectedBalanceAccountFrom, actualMoneyBalanceAccountFrom);
        String actualMoneyBalanceAccountTo = get("/accounts/" + accountToId).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).
                        extract().jsonPath().getString(MONEY_BALANCE_PARAM);
        String expectedBalanceAccountTo = SMALL_MONEY_VALUE;
        assertEquals(expectedBalanceAccountTo, actualMoneyBalanceAccountTo);

        //cleanup
        cleanTransfer(transferId);
        cleanUser(PASSPORT_ID1);
        cleanUser(PASSPORT_ID2);
    }

    @Test
    public void MTRA_110102_moneyTransfer_whenEnoughCreditMoneyOnDebitAccount_thenTransferExecute() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        createUser(USER_NAME2, PASSPORT_ID2);

        String accountFromId = createAccountWithCreditMoney(PASSPORT_ID1, MONEY_VALUE);
        String accountToId = createEmptyAccount(PASSPORT_ID2);
        String payload = new TestPayloadBuilder().setAccountFromId(accountFromId).setAccountToId(accountToId).setAmount(SMALL_MONEY_VALUE).buildPayload();

        //test
        String transferId = given().body(payload).
                when().put("/accounts/transfer").then().
                assertThat().statusCode(HttpStatus.OK_200).extract().jsonPath().getString(ID_PARAM);

        //verify
        String actualMoneyBalanceAccountFrom = get("/accounts/" + accountFromId).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).
                        extract().jsonPath().getString(MONEY_BALANCE_PARAM);
        String expectedBalanceAccountFrom = String.valueOf(Integer.valueOf(ZERO_MONEY_VALUE) - Integer.valueOf(SMALL_MONEY_VALUE));
        assertEquals(expectedBalanceAccountFrom, actualMoneyBalanceAccountFrom);
        String actualMoneyBalanceAccountTo = get("/accounts/" + accountToId).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200).
                        extract().jsonPath().getString(MONEY_BALANCE_PARAM);
        String expectedBalanceAccountTo = SMALL_MONEY_VALUE;
        assertEquals(expectedBalanceAccountTo, actualMoneyBalanceAccountTo);

        //cleanup
        cleanTransfer(transferId);
        cleanUser(PASSPORT_ID1);
        cleanUser(PASSPORT_ID2);
    }
}
