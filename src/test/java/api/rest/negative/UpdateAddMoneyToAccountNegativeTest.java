package api.rest.negative;

import api.rest.MoneyTransferTest;
import api.rest.TestPayloadBuilder;
import io.restassured.path.json.JsonPath;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import static api.rest.TestConstants.*;
import static api.rest.TestHelper.*;
import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;

public class UpdateAddMoneyToAccountNegativeTest extends MoneyTransferTest {

    @Test
    public void MTRA_100201_addMoney_whenAccountDoesNotExist_ThenExceptionIsThrown() {
        //prepare data
        String payload = new TestPayloadBuilder().setAccountToId(ID_NON_EXIST).setAmount(SMALL_MONEY_VALUE).buildPayload();

        //test
        JsonPath actual = given().body(payload).
                when().put("/accounts/recharge").then().
                assertThat().statusCode(HttpStatus.NOT_FOUND_404).extract().jsonPath();

        //verify
        String actualException = actual.getString(DETAIL_MESSAGE_REQUEST_PARAM);
        assertTrue(actualException.contains(ACCOUNT_NOT_FOUND_EXCEPTION));
    }

    @Test
    public void MTRA_100202_moneyTransfer_whenAmountParameterHasNegativeValue_ThenExceptionIsThrown() {
        //prepare data
        createUser(USER_NAME1, PASSPORT_ID1);
        String accountToId = createEmptyAccount(PASSPORT_ID1);
        String payload = new TestPayloadBuilder().setAccountToId(accountToId).setAmount(NEGATIVE_MONEY_VALUE).buildPayload();

        //test
        JsonPath actual = given().body(payload).
                when().put("/accounts/recharge").then().
                assertThat().statusCode(HttpStatus.BAD_REQUEST_400).extract().jsonPath();

        //verify
        String actualException = actual.getString(DETAIL_MESSAGE_REQUEST_PARAM);
        assertTrue(actualException.contains(MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE_EXCEPTION));

        //cleanup
        cleanUser(PASSPORT_ID1);

    }

}
