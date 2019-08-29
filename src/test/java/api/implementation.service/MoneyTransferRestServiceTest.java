package api.implementation.service;

import api.implementation.exception.MoneyTransferException;
import api.implementation.model.User;
import api.implementation.model.request.UserRequest;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import spark.Request;
import spark.Response;

import static api.implementation.exception.ExceptionList.MISSING_MANDATORY_PARAMETERS;
import static api.implementation.service.MoneyTransferModelConverter.convertUserRequest;
import static api.implementation.service.MoneyTransferRequestValidator.validateUser;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MoneyTransferRequestValidator.class, MoneyTransferModelConverter.class})
public class MoneyTransferRestServiceTest {

    @Mock
    private Request request;

    @Mock
    private Response response;

    Gson gson = new Gson();

    @Before
    public void setup() {
        doCallRealMethod().when(response).body(any(String.class));
        doCallRealMethod().when(response).body();
        PowerMockito.spy(MoneyTransferRequestValidator.class);
        PowerMockito.spy(MoneyTransferModelConverter.class);
    }


    @Test
    public void createUser_whenRequestContainsValidParameter_thenAppropriateUserInResponse() {
        UserRequest userRequest = new UserRequest("name", "passId");
        String requestBody = gson.toJson(userRequest);
        when(request.body()).thenReturn(requestBody);

        String responseString = MoneyTransferRestService.createUser(request, response);

        verify(request).body();
        verify(response).status(200);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        MoneyTransferRequestValidator.assertNotNull(userRequest);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        validateUser(userRequest);
        PowerMockito.verifyStatic(MoneyTransferModelConverter.class);
        convertUserRequest(userRequest);
        User actual = gson.fromJson(responseString, User.class);
        assertNotNull(actual);
        assertEquals(userRequest.getName(), actual.getName());
        assertEquals(userRequest.getPassportId(), actual.getPassportId());
    }

    @Test
    public void createUser_whenRequestDoesNotContainPassportId_thenExceptionIsThrows() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("name");
        String requestBody = gson.toJson(userRequest);
        when(request.body()).thenReturn(requestBody);

        try {
            String responseString = MoneyTransferRestService.createUser(request, response);
            fail();
        } catch (Exception e){
            assertEquals(e.getClass(), MoneyTransferException.class);
            assertTrue(e.getMessage().contains(MISSING_MANDATORY_PARAMETERS.message));
        }

        verify(request).body();
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        assertNotNull(userRequest);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        validateUser(userRequest);
        PowerMockito.verifyStatic(MoneyTransferModelConverter.class, Mockito.never());
        convertUserRequest(userRequest);
    }

}
