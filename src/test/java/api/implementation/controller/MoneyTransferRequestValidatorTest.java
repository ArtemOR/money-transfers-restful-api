package api.implementation.controller;

import api.implementation.exception.MoneyTransferException;
import api.implementation.model.request.AccountRequest;
import api.implementation.model.request.TransferRequest;
import api.implementation.model.request.UserRequest;
import org.junit.Test;

import static api.implementation.exception.ExceptionList.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class MoneyTransferRequestValidatorTest {

    MoneyTransferRequestValidator undertest = new MoneyTransferRequestValidator();

    @Test
    public void assertObjectNotNull_whenObjectIsNull_ThenExceptionIsThrown() {
        Object nullObject = null;

        assertThatThrownBy(() -> undertest.assertObjectNotNull(nullObject))
                .isInstanceOf(MoneyTransferException.class).hasMessage(MISSING_REQUEST.message);
    }

    @Test
    public void validateUser_whenNameIsNull_ThenExceptionIsThrown() {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassportId("123");

        assertThatThrownBy(() -> undertest.validateUser(userRequest))
                .isInstanceOf(MoneyTransferException.class).hasMessageContaining(MISSING_MANDATORY_PARAMETERS.message);
    }

    @Test
    public void validateUser_whenPassportIsNull_ThenExceptionIsThrown() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("123");

        assertThatThrownBy(() -> undertest.validateUser(userRequest))
                .isInstanceOf(MoneyTransferException.class).hasMessageContaining(MISSING_MANDATORY_PARAMETERS.message);
    }

    @Test
    public void validateUser_whenObjectIsValid_ThenMethodExecutes() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("123");
        userRequest.setPassportId("123");

        undertest.validateUser(userRequest);
    }

    @Test
    public void validateAccount_whenPassportIsNull_ThenExceptionIsThrown() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setMoneyBalance("123");

        assertThatThrownBy(() -> undertest.validateAccount(accountRequest))
                .isInstanceOf(MoneyTransferException.class).hasMessageContaining(MISSING_MANDATORY_PARAMETERS.message);
    }

    @Test
    public void validateAccount_whenObjectIsValid_ThenMethodExecutes() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setMoneyBalance("123");
        accountRequest.setPassportId("1233");

        undertest.validateAccount(accountRequest);
    }

    @Test
    public void validateTransferInsideAccount_whenObjectIsValid_ThenMethodExecutes() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountToId("123");
        transferRequest.setAmount("1233");

        undertest.validateTransferInsideAccount(transferRequest);
    }

    @Test
    public void validateTransferInsideAccount_whenAmountIsNegative_ThenExceptionIsThrown() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountToId("123");
        transferRequest.setAmount("-1233");

        assertThatThrownBy(() -> undertest.validateTransferInsideAccount(transferRequest))
                .isInstanceOf(MoneyTransferException.class).hasMessageContaining(MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE.message);
    }

    @Test
    public void validateTransferRequestBetweenAccounts_whenObjectIsValid_ThenMethodExecutes() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountToId("123");
        transferRequest.setAccountFromId("121");
        transferRequest.setAmount("1233");

        undertest.validateTransferRequestBetweenAccounts(transferRequest);
    }

    @Test
    public void validateTransferRequestBetweenAccounts_whenObjectIsValid_ThenExceptionIsThrown() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountToId("123");
        transferRequest.setAccountFromId("123");
        transferRequest.setAmount("1233");

        assertThatThrownBy(() -> undertest.validateTransferRequestBetweenAccounts(transferRequest))
                .isInstanceOf(MoneyTransferException.class).hasMessageContaining(CHOSE_DIFFERENT_ACCOUNT_IDS.message);
    }

}
