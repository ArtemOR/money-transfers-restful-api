
package api.implementation.controller;

import api.implementation.converter.MoneyTransferModelConverter;
import api.implementation.exception.ExceptionList;
import api.implementation.exception.MoneyTransferException;
import api.implementation.model.Account;
import api.implementation.model.User;
import api.implementation.model.request.AccountRequest;
import api.implementation.model.request.UserRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import spark.Request;
import spark.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static api.implementation.constants.StringConstants.PASSPORT_ID_PARAM;
import static api.implementation.exception.ExceptionList.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({MoneyTransferRequestValidator.class, MoneyTransferModelConverter.class})
public class MoneyTransferRestControllerTest {

    @Mock
    private Request request;

    @Mock
    private Response response;

    private Gson gson = new Gson();

    @Before
    public void setup() {
        doCallRealMethod().when(response).body(any(String.class));
        doCallRealMethod().when(response).body();
        PowerMockito.spy(MoneyTransferRequestValidator.class);
        PowerMockito.spy(MoneyTransferModelConverter.class);
    }

    @Test
    public void createUser_whenRequestContainsValidParameters_thenUserInResponse() {
        UserRequest userRequest = new UserRequest("name", "passprtId");
        String requestBody = gson.toJson(userRequest);
        when(request.body()).thenReturn(requestBody);

        String responseString = MoneyTransferRestController.createUser(request, response);

        verify(request).body();
        verify(response).status(200);
        User actual = gson.fromJson(responseString, User.class);
        assertNotNull(actual);
        assertEquals(userRequest.getName(), actual.getName());
        assertEquals(userRequest.getPassportId(), actual.getPassportId());
    }

    @Test
    public void createUser_whenRequestIsEmpty_thenExceptionIsThrows() {
        String requestBody = null;
        when(request.body()).thenReturn(requestBody);

        try {
            String responseString = MoneyTransferRestController.createUser(request, response);
            fail();
        } catch (Exception e) {
            assertEquals(e.getClass(), MoneyTransferException.class);
            assertTrue(e.getMessage().contains(MISSING_REQUEST.message));
        }

        verify(request).body();
    }

    @Test
    public void createUser_whenNotSpecifiedPassportId_thenExceptionIsThrows() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("name");
        String requestBody = gson.toJson(userRequest);
        when(request.body()).thenReturn(requestBody);

        try {
            String responseString = MoneyTransferRestController.createUser(request, response);
            fail();
        } catch (Exception e) {
            assertEquals(e.getClass(), MoneyTransferException.class);
            assertTrue(e.getMessage().contains(MISSING_MANDATORY_PARAMETERS.message));
        }

        verify(request).body();
    }

    @Test
    public void createAccount_whenRequestContainsValidParameters_thenAccountInResponse() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setPassportId("passId");
        accountRequest.setMoneyBalance("1000");
        String requestBody = gson.toJson(accountRequest);
        when(request.body()).thenReturn(requestBody);

        assertThatThrownBy(() -> MoneyTransferRestController.createAccount(request, response))
                .isInstanceOf(MoneyTransferException.class).hasMessageContaining(USER_NOT_FOUND.message);

    }


    @Test
    public void createAccount_whenCreditLimitParameterNegative_thenExceptionIsThrows() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setPassportId("passId");
        accountRequest.setCreditLimit("-1000");
        String requestBody = gson.toJson(accountRequest);
        when(request.body()).thenReturn(requestBody);

        assertThatThrownBy(() -> MoneyTransferRestController.createAccount(request, response))
                .isInstanceOf(MoneyTransferException.class).hasMessageContaining(MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE.message);
    }

    @Test
    public void createAccount_whenNotSpecifiedPassportId_thenExceptionIsThrows() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setMoneyBalance("1000");
        String requestBody = gson.toJson(accountRequest);
        when(request.body()).thenReturn(requestBody);

        assertThatThrownBy(() -> MoneyTransferRestController.createAccount(request, response))
                .isInstanceOf(MoneyTransferException.class).hasMessageContaining(MISSING_MANDATORY_PARAMETERS.message);

        verify(request).body();
    }

    @Test
    public void getUserByPassportId_whenUserNotFound_thenExceptionIsThrows() {
        String passportId = "nonExistent";
        when(request.params(PASSPORT_ID_PARAM)).thenReturn(passportId);

        assertThatThrownBy(() -> MoneyTransferRestController.getUserByPassportId(request, response))
                .isInstanceOf(MoneyTransferException.class).hasMessageContaining(USER_NOT_FOUND.message);

        verify(request).params(PASSPORT_ID_PARAM);
    }

    @Test
    public void getAllUsers_whenMethodCalls_thenUsersReturns() {
        String responseString = MoneyTransferRestController.getAllUsers(request, response);

        Type itemsListType = new TypeToken<List<User>>() {
        }.getType();
        ArrayList<User> actual = gson.fromJson(responseString, itemsListType);
        assertNotNull(actual);
    }


    @Test
    public void getAllAccounts_whenMethodCalls_thenAccountsReturns() {
        String responseString = MoneyTransferRestController.getAllAccounts(request, response);

        Type itemsListType = new TypeToken<List<Account>>() {
        }.getType();
        ArrayList<Account> actual = gson.fromJson(responseString, itemsListType);
        assertNotNull(actual);

    }

    @Test
    public void generateException_whenMethodCalls_thenExceptionMessageInResponse() {
        ExceptionList exceptionMessage = ExceptionList.BAD_REQUEST;
        MoneyTransferException exception = new MoneyTransferException(exceptionMessage);

        MoneyTransferRestController.generateException(exception, request, response);

        verify(response).status(exceptionMessage.httpStatus);
        assertTrue(response.body().contains(exceptionMessage.message));
    }

    @Test
    public void generateException_whenNumberFormatException_thenExceptionMessageInResponse() {
        NumberFormatException exception = new NumberFormatException();

        MoneyTransferRestController.generateException(exception, request, response);

        verify(response).status(ExceptionList.WRONG_NUMBER_FORMAT.httpStatus);
        assertTrue(response.body().contains(ExceptionList.WRONG_NUMBER_FORMAT.message));
    }

    @Test
    public void generateException_whenInternalServerError_thenErrorIsHandles() {
        String responseError = MoneyTransferRestController.handleInternalServerError(request, response);

        verify(response).status(ExceptionList.BAD_REQUEST.httpStatus);
        assertTrue(responseError.contains(ExceptionList.BAD_REQUEST.message));
    }


    private User createUser(String name, String passportId) {
        User user = new User();
        user.setPassportId(passportId);
        user.setName(name);

        return user;
    }

}
