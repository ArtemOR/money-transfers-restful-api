package api.implementation.service;

import api.implementation.exception.ExceptionList;
import api.implementation.exception.MoneyTransferException;
import api.implementation.exception.MoneyTransferExceptionDetailResult;
import api.implementation.model.Account;
import api.implementation.model.AccountTransfer;
import api.implementation.model.AccountType;
import api.implementation.model.User;
import api.implementation.model.request.*;
import api.implementation.model.transfer.BetweenAccountsTransfer;
import api.implementation.model.transfer.InsideAccountTransfer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static api.implementation.exception.ExceptionList.*;
import static api.implementation.service.MoneyTransferModelConverter.*;
import static api.implementation.service.MoneyTransferRequestValidator.*;
import static api.implementation.service.StringConstants.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MoneyTransferRequestValidator.class, MoneyTransferModelConverter.class})
public class MoneyTransferRestServiceTest {

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
        MoneyTransferRestService.users = new HashMap<>();

        String responseString = MoneyTransferRestService.createUser(request, response);

        verify(request).body();
        verify(response).status(200);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        assertObjectNotNull(userRequest);
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
    public void createUser_whenRequestIsEmpty_thenExceptionIsThrows() {
        String requestBody = null;
        when(request.body()).thenReturn(requestBody);

        try {
            String responseString = MoneyTransferRestService.createUser(request, response);
            fail();
        } catch (Exception e) {
            assertEquals(e.getClass(), MoneyTransferException.class);
            assertTrue(e.getMessage().contains(MISSING_REQUEST.message));
        }

        verify(request).body();
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        assertObjectNotNull(requestBody);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class, Mockito.never());
        validateUser(any());
        PowerMockito.verifyStatic(MoneyTransferModelConverter.class, Mockito.never());
        convertUserRequest(any());
    }

    @Test
    public void createUser_whenNotSpecifiedPassportId_thenExceptionIsThrows() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("name");
        String requestBody = gson.toJson(userRequest);
        when(request.body()).thenReturn(requestBody);

        try {
            String responseString = MoneyTransferRestService.createUser(request, response);
            fail();
        } catch (Exception e) {
            assertEquals(e.getClass(), MoneyTransferException.class);
            assertTrue(e.getMessage().contains(MISSING_MANDATORY_PARAMETERS.message));
        }

        verify(request).body();
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        assertObjectNotNull(userRequest);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        validateUser(userRequest);
        PowerMockito.verifyStatic(MoneyTransferModelConverter.class, Mockito.never());
        convertUserRequest(userRequest);
    }

    @Test
    public void createAccount_whenRequestContainsValidParameters_thenAccountInResponse() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setPassportId("passId");
        accountRequest.setMoneyBalance("1000");
        String requestBody = gson.toJson(accountRequest);
        when(request.body()).thenReturn(requestBody);
        HashMap<String, User> userHashMap = new HashMap<>();
        userHashMap.put("passId", new User());
        MoneyTransferRestService.users = userHashMap;
        MoneyTransferRestService.accounts = new ConcurrentHashMap<>();

        String responseString = MoneyTransferRestService.createAccount(request, response);

        verify(request).body();
        verify(response).status(200);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        MoneyTransferRequestValidator.assertObjectNotNull(accountRequest);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        validateAccount(accountRequest);
        PowerMockito.verifyStatic(MoneyTransferModelConverter.class);
        convertAccountRequest(accountRequest);
        Account actual = gson.fromJson(responseString, Account.class);
        assertObjectNotNull(actual);
        assertEquals(accountRequest.getPassportId(), actual.getPassportId());
        assertEquals(new BigDecimal(accountRequest.getMoneyBalance()), actual.getMoneyBalance());
        assertEquals(AccountType.DEBIT, actual.getAccountType());
        assertEquals(BigDecimal.ZERO, actual.getCreditLimit());
    }

    @Test
    public void createAccount_whenRequestContainsCreditLimit_thenCreditAccountInResponse() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setPassportId("passId");
        accountRequest.setCreditLimit("1000");
        String requestBody = gson.toJson(accountRequest);
        when(request.body()).thenReturn(requestBody);
        HashMap<String, User> userHashMap = new HashMap<>();
        userHashMap.put("passId", new User());
        MoneyTransferRestService.users = userHashMap;
        MoneyTransferRestService.accounts = new ConcurrentHashMap<>();

        String responseString = MoneyTransferRestService.createAccount(request, response);

        verify(request).body();
        verify(response).status(200);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        MoneyTransferRequestValidator.assertObjectNotNull(accountRequest);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        validateAccount(accountRequest);
        PowerMockito.verifyStatic(MoneyTransferModelConverter.class);
        convertAccountRequest(accountRequest);
        Account actual = gson.fromJson(responseString, Account.class);
        assertObjectNotNull(actual);
        assertEquals(accountRequest.getPassportId(), actual.getPassportId());
        assertEquals(BigDecimal.ZERO, actual.getMoneyBalance());
        assertEquals(AccountType.CREDIT, actual.getAccountType());
        assertEquals(new BigDecimal(accountRequest.getCreditLimit()), actual.getCreditLimit());
    }

    @Test
    public void createAccount_whenUsersMapDoesNotContainsKey_thenExceptionIsThrows() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setPassportId("passportId");
        accountRequest.setCreditLimit("1000");
        String requestBody = gson.toJson(accountRequest);
        when(request.body()).thenReturn(requestBody);
        MoneyTransferRestService.accounts = new ConcurrentHashMap<>();

        try {
            String responseString = MoneyTransferRestService.createAccount(request, response);
            fail();
        } catch (Exception e) {
            assertEquals(e.getClass(), MoneyTransferException.class);
            assertTrue(e.getMessage().contains(USER_NOT_FOUND.message));
        }

        verify(request).body();
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        assertObjectNotNull(accountRequest);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        validateAccount(accountRequest);
        PowerMockito.verifyStatic(MoneyTransferModelConverter.class, Mockito.never());
        convertAccountRequest(accountRequest);
    }

    @Test
    public void createAccount_whenCreditLimitParameterNegative_thenExceptionIsThrows() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setPassportId("passId");
        accountRequest.setCreditLimit("-1000");
        String requestBody = gson.toJson(accountRequest);
        when(request.body()).thenReturn(requestBody);
        HashMap<String, User> userHashMap = new HashMap<>();
        userHashMap.put("passId", new User());
        MoneyTransferRestService.users = userHashMap;
        MoneyTransferRestService.accounts = new ConcurrentHashMap<>();

        try {
            String responseString = MoneyTransferRestService.createAccount(request, response);
            fail();
        } catch (Exception e) {
            assertEquals(e.getClass(), MoneyTransferException.class);
            assertTrue(e.getMessage().contains(MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE.message));
        }

        verify(request).body();
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        assertObjectNotNull(accountRequest);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        validateAccount(accountRequest);
        PowerMockito.verifyStatic(MoneyTransferModelConverter.class, Mockito.never());
        convertAccountRequest(accountRequest);
    }

    @Test
    public void createAccount_whenNotSpecifiedPassportId_thenExceptionIsThrows() {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setMoneyBalance("1000");
        String requestBody = gson.toJson(accountRequest);
        when(request.body()).thenReturn(requestBody);
        HashMap<String, User> userHashMap = new HashMap<>();
        userHashMap.put("passId", new User());
        MoneyTransferRestService.users = userHashMap;
        MoneyTransferRestService.accounts = new ConcurrentHashMap<>();

        try {
            String responseString = MoneyTransferRestService.createAccount(request, response);
            fail();
        } catch (Exception e) {
            assertEquals(e.getClass(), MoneyTransferException.class);
            assertTrue(e.getMessage().contains(MISSING_MANDATORY_PARAMETERS.message));
        }

        verify(request).body();
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        assertObjectNotNull(accountRequest);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        validateAccount(accountRequest);
        PowerMockito.verifyStatic(MoneyTransferModelConverter.class, Mockito.never());
        convertAccountRequest(accountRequest);
    }

    @Test
    public void getUserByPassportId_whenUserNotFound_thenExceptionIsThrows() {
        String passportId = "nonExistent";
        when(request.params(PASSPORT_ID_PARAM)).thenReturn(passportId);

        try {
            String responseString = MoneyTransferRestService.getUserByPassportId(request, response);
            fail();
        } catch (Exception e) {
            assertEquals(e.getClass(), MoneyTransferException.class);
            assertTrue(e.getMessage().contains(USER_NOT_FOUND.message));
        }

        verify(request).params(PASSPORT_ID_PARAM);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        assertUserExist(passportId);
    }

    @Test
    public void getUserByPassportId_whenUserExist_thenUserInResponse() {
        String passportId = "existent";
        String name = "name1";
        when(request.params(PASSPORT_ID_PARAM)).thenReturn(passportId);
        HashMap<String, User> userHashMap = new HashMap<>();
        User expected = createUser(name, passportId);
        userHashMap.put(expected.getPassportId(), expected);
        MoneyTransferRestService.users = userHashMap;

        String responseString = MoneyTransferRestService.getUserByPassportId(request, response);

        User actual = gson.fromJson(responseString, User.class);
        verify(request).params(PASSPORT_ID_PARAM);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        assertUserExist(passportId);
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPassportId(), actual.getPassportId());
    }

    @Test
    public void getAllUsers_whenMethodCalls_thenUsersReturns() {
        User user1 = createUser("name1", "passp1");
        User user2 = createUser("name2", "passp2");
        HashMap<String, User> userHashMap = new HashMap<>();
        userHashMap.put(user1.getPassportId(), user1);
        userHashMap.put(user2.getPassportId(), user2);
        MoneyTransferRestService.users = userHashMap;

        String responseString = MoneyTransferRestService.getAllUsers(request, response);

        Type itemsListType = new TypeToken<List<User>>() {
        }.getType();
        ArrayList<User> actual = gson.fromJson(responseString, itemsListType);
        assertNotNull(actual);
        assertEquals(2, actual.size());
    }

    @Test
    public void getAccountByAccountId_whenMethodCalls_thenAccountsReturns() {
        Account expected = new Account(1L, "userid1", BigDecimal.ZERO, AccountType.CREDIT, BigDecimal.TEN);
        Map<Long, Account> accountsMap = new ConcurrentHashMap<>();
        accountsMap.put(expected.getId(), expected);
        MoneyTransferRestService.accounts = accountsMap;
        String accountId = String.valueOf(expected.getId());
        when(request.params(ACCOUNT_ID_PARAM)).thenReturn(accountId);

        String responseString = MoneyTransferRestService.getAccountByAccountId(request, response);

        verify(request).params(ACCOUNT_ID_PARAM);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        assertAccountExist(Long.valueOf(accountId));
        Account actual = gson.fromJson(responseString, Account.class);
        assertEquals(expected, actual);
    }

    @Test
    public void getAllAccounts_whenMethodCalls_thenAccountsReturns() {
        Account account1 = new Account(1L, "userid1", BigDecimal.ZERO, AccountType.CREDIT, BigDecimal.TEN);
        Account account2 = new Account(2L, "userid1", BigDecimal.TEN, AccountType.DEBIT, BigDecimal.ZERO);
        Map<Long, Account> accountsMap = new ConcurrentHashMap<>();
        accountsMap.put(account1.getId(), account1);
        accountsMap.put(account2.getId(), account2);
        MoneyTransferRestService.accounts = accountsMap;

        String responseString = MoneyTransferRestService.getAllAccounts(request, response);

        Type itemsListType = new TypeToken<List<Account>>() {
        }.getType();
        ArrayList<Account> actual = gson.fromJson(responseString, itemsListType);
        assertNotNull(actual);
        assertEquals(2, actual.size());
    }

    @Test
    public void getAllTransfersHistory_whenMethodCalls_thenTransfersReturns() {
        BetweenAccountsTransfer accountTransfer1 = new BetweenAccountsTransfer(1L, 11L, 111L, BigDecimal.TEN, System.currentTimeMillis());
        BetweenAccountsTransfer accountTransfer2 = new BetweenAccountsTransfer(2L, 11L, 111L, BigDecimal.ONE, System.currentTimeMillis());
        Map<Long, AccountTransfer> transfersMap = new HashMap<>();
        transfersMap.put(accountTransfer1.getId(), accountTransfer1);
        transfersMap.put(accountTransfer2.getId(), accountTransfer2);
        MoneyTransferRestService.transfers = transfersMap;

        String responseString = MoneyTransferRestService.getAllTransfersHistory(request, response);

        Type itemsListType = new TypeToken<List<BetweenAccountsTransfer>>() {
        }.getType();
        ArrayList<Account> actual = gson.fromJson(responseString, itemsListType);
        assertNotNull(actual);
        assertEquals(2, actual.size());
    }

    @Test
    public void getTransfersHistoryBySenderId_whenTransferInMap_thenTransferReturns() {
        BetweenAccountsTransfer expected = new BetweenAccountsTransfer(1L, 11L, 111L, BigDecimal.TEN, System.currentTimeMillis());
        BetweenAccountsTransfer accountTransfer2 = new BetweenAccountsTransfer(2L, 12L, 112L, BigDecimal.ONE, System.currentTimeMillis());
        Map<Long, AccountTransfer> transfersMap = new HashMap<>();
        transfersMap.put(expected.getId(), expected);
        transfersMap.put(accountTransfer2.getId(), accountTransfer2);
        MoneyTransferRestService.transfers = transfersMap;
        String accountFromId = String.valueOf(expected.getAccountFromId());
        when(request.params(ACCOUNT_FROM_ID_PARAM)).thenReturn(accountFromId);
        Map<Long, Account> accountsMap = new ConcurrentHashMap<>();
        Account account = new Account(expected.getAccountFromId(), "userid1", BigDecimal.TEN, AccountType.DEBIT, BigDecimal.ZERO);
        accountsMap.put(account.getId(), account);
        MoneyTransferRestService.accounts = accountsMap;

        String responseString = MoneyTransferRestService.getTransfersHistoryBySenderId(request, response);

        verify(request).params(ACCOUNT_FROM_ID_PARAM);
        Type itemsListType = new TypeToken<List<BetweenAccountsTransfer>>() {
        }.getType();
        ArrayList<BetweenAccountsTransfer> actual = gson.fromJson(responseString, itemsListType);
        assertNotNull(actual);
        assertEquals(actual, Collections.singletonList(expected));
    }

    @Test
    public void getTransfersHistoryByReceiverId_whenTransferInMap_thenTransferReturns() {
        BetweenAccountsTransfer expected = new BetweenAccountsTransfer(1L, 11L, 111L, BigDecimal.TEN, System.currentTimeMillis());
        BetweenAccountsTransfer accountTransfer2 = new BetweenAccountsTransfer(2L, 12L, 112L, BigDecimal.ONE, System.currentTimeMillis());
        Map<Long, AccountTransfer> transfersMap = new HashMap<>();
        transfersMap.put(expected.getId(), expected);
        transfersMap.put(accountTransfer2.getId(), accountTransfer2);
        MoneyTransferRestService.transfers = transfersMap;
        String accountToId = String.valueOf(expected.getAccountToId());
        when(request.params(ACCOUNT_TO_ID_PARAM)).thenReturn(accountToId);
        Map<Long, Account> accountsMap = new ConcurrentHashMap<>();
        Account account = new Account(expected.getAccountToId(), "userid1", BigDecimal.TEN, AccountType.DEBIT, BigDecimal.ZERO);
        accountsMap.put(account.getId(), account);
        MoneyTransferRestService.accounts = accountsMap;

        String responseString = MoneyTransferRestService.getTransfersHistoryByReceiverId(request, response);

        verify(request).params(ACCOUNT_TO_ID_PARAM);
        Type itemsListType = new TypeToken<List<BetweenAccountsTransfer>>() {
        }.getType();
        ArrayList<BetweenAccountsTransfer> actual = gson.fromJson(responseString, itemsListType);
        assertNotNull(actual);
        assertEquals(actual, Collections.singletonList(expected));
    }

    @Test
    public void transferMoneyBetweenAccounts_whenTransferInMap_thenTransferReturns() {
        String accountId = "112";
        String amount = "100";
        String initialAmount = "10";
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountToId(accountId);
        transferRequest.setAmount(amount);
        when(request.body()).thenReturn(gson.toJson(transferRequest, TransferRequest.class));
        Map<Long, Account> accountsMap = new ConcurrentHashMap<>();
        Account account = new Account(Long.valueOf(accountId), "userid1", new BigDecimal(initialAmount), AccountType.DEBIT, BigDecimal.ZERO);
        accountsMap.put(account.getId(), account);
        MoneyTransferRestService.accounts = accountsMap;

        String responseString = MoneyTransferRestService.addMoneyToAccount(request, response);

        verify(request).body();
        InsideAccountTransfer actual = gson.fromJson(responseString, InsideAccountTransfer.class);
        PowerMockito.verifyStatic(MoneyTransferModelConverter.class);
        convertTransferRequest(transferRequest);
        assertNotNull(actual);
        assertEquals(new BigDecimal(amount), actual.getAmount());
    }

    @Test
    public void transferMoneyBetweenAccounts_whenMethodCalls_thenTransferReturns() {
        String accountToId = "11";
        String accountFromId = "22";
        String amount = "5";
        Account account1 = new Account(Long.valueOf(accountFromId), "userid1", BigDecimal.ZERO, AccountType.CREDIT, BigDecimal.TEN);
        Account account2 = new Account(Long.valueOf(accountToId), "userid1", BigDecimal.ZERO, AccountType.DEBIT, BigDecimal.ZERO);
        Map<Long, Account> accountsMap = new ConcurrentHashMap<>();
        accountsMap.put(account1.getId(), account1);
        accountsMap.put(account2.getId(), account2);
        MoneyTransferRestService.accounts = accountsMap;
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountFromId(accountFromId);
        transferRequest.setAccountToId(accountToId);
        transferRequest.setAmount(amount);
        when(request.body()).thenReturn(gson.toJson(transferRequest, TransferRequest.class));

        String responseString = MoneyTransferRestService.transferMoneyBetweenAccounts(request, response);

        verify(request).body();
        BetweenAccountsTransfer actual = gson.fromJson(responseString, BetweenAccountsTransfer.class);
        PowerMockito.verifyStatic(MoneyTransferModelConverter.class);
        convertBetweenAccountsTransferRequest(transferRequest);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        validateTransferBetweenAccounts(transferRequest);
        assertNotNull(actual);
        assertEquals(new BigDecimal(amount), actual.getAmount());
    }

    @Test
    public void transferMoneyBetweenAccounts_whenNotEnoughMonet_thenExceptionIsThrows() {
        String accountToId = "11";
        String accountFromId = "22";
        String amount = "5000";
        Account account1 = new Account(Long.valueOf(accountFromId), "userid1", BigDecimal.ZERO, AccountType.CREDIT, BigDecimal.TEN);
        Account account2 = new Account(Long.valueOf(accountToId), "userid1", BigDecimal.ZERO, AccountType.DEBIT, BigDecimal.ZERO);
        Map<Long, Account> accountsMap = new ConcurrentHashMap<>();
        accountsMap.put(account1.getId(), account1);
        accountsMap.put(account2.getId(), account2);
        MoneyTransferRestService.accounts = accountsMap;
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountFromId(accountFromId);
        transferRequest.setAccountToId(accountToId);
        transferRequest.setAmount(amount);
        when(request.body()).thenReturn(gson.toJson(transferRequest, TransferRequest.class));

        try {
            String responseString = MoneyTransferRestService.transferMoneyBetweenAccounts(request, response);
            fail();
        } catch (Exception e) {
            assertEquals(e.getClass(), MoneyTransferException.class);
            assertTrue(e.getMessage().contains(NOT_ENOUGH_MONEY.message));
        }

        verify(request).body();
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        validateTransferBetweenAccounts(transferRequest);
        PowerMockito.verifyStatic(MoneyTransferModelConverter.class, Mockito.never());
        convertBetweenAccountsTransferRequest(transferRequest);
    }

    @Test
    public void deleteUserByPassportId_whenUserExist_thenMethodIsExecutes() {
        String passportId = "passpId";
        String name = "name1";
        when(request.params(PASSPORT_ID_PARAM)).thenReturn(passportId);
        HashMap<String, User> userHashMap = new HashMap<>();

        User forDelete = createUser(name, passportId);
        userHashMap.put(forDelete.getPassportId(), forDelete);
        MoneyTransferRestService.users = userHashMap;

        MoneyTransferRestService.deleteUserByPassportId(request, response);

        verify(request).params(PASSPORT_ID_PARAM);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        assertUserExist(passportId);
        assertTrue(!userHashMap.containsKey(passportId));
    }

    @Test
    public void deleteAccountByAccountId_whenMethodCalls_thenMethodIsExecutes() {
        Account forDelete = new Account(1L, "userid1", BigDecimal.ZERO, AccountType.CREDIT, BigDecimal.TEN);
        Map<Long, Account> accountsMap = new ConcurrentHashMap<>();
        accountsMap.put(forDelete.getId(), forDelete);
        MoneyTransferRestService.accounts = accountsMap;
        String accountId = String.valueOf(forDelete.getId());
        when(request.params(ACCOUNT_ID_PARAM)).thenReturn(accountId);

        MoneyTransferRestService.deleteAccountByAccountId(request, response);

        verify(request).params(ACCOUNT_ID_PARAM);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        assertAccountExist(Long.valueOf(accountId));
        assertTrue(!accountsMap.containsKey(forDelete.getId()));
    }

    @Test
    public void deleteTransferByTransferId_whenMethodCalls_thenMethodIsExecutes() {
        BetweenAccountsTransfer forDelete = new BetweenAccountsTransfer(1L, 11L, 111L, BigDecimal.TEN, System.currentTimeMillis());
        Map<Long, AccountTransfer> transfersMap = new HashMap<>();
        transfersMap.put(forDelete.getId(), forDelete);
        MoneyTransferRestService.transfers = transfersMap;
        String transferId = String.valueOf(forDelete.getId());
        when(request.params(TRANSFER_ID_ID_PARAM)).thenReturn(transferId);

        MoneyTransferRestService.deleteTransferByTransferId(request, response);

        verify(request).params(TRANSFER_ID_ID_PARAM);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        assertTransferExist(Long.valueOf(transferId));
        assertTrue(!transfersMap.containsKey(forDelete.getId()));
    }

    @Test
    public void generateException_whenMethodCalls_thenExceptionMessageInResponse() {
        ExceptionList exceptionMessage = ExceptionList.BAD_REQUEST;
        MoneyTransferException exception = new MoneyTransferException(exceptionMessage);

        MoneyTransferRestService.generateException(exception, request, response);

        verify(response).status(exceptionMessage.httpStatus);
        assertTrue(response.body().contains(exceptionMessage.message));
    }

    @Test
    public void generateException_whenNumberFormatException_thenExceptionMessageInResponse() {
        NumberFormatException exception = new NumberFormatException();

        MoneyTransferRestService.generateException(exception, request, response);

        verify(response).status(ExceptionList.WRONG_NUMBER_FORMAT.httpStatus);
        assertTrue(response.body().contains(ExceptionList.WRONG_NUMBER_FORMAT.message));
    }

    @Test
    public void generateException_whenInternalServerError_thenErrorIsHandles() {
        String responseError = MoneyTransferRestService.handleInternalServerError(request, response);

        verify(response).status(ExceptionList.BAD_REQUEST.httpStatus);
        assertTrue(responseError.contains(ExceptionList.BAD_REQUEST.message));
    }

    @Test
    public void createSomeTestObjects_whenMethodIsCalls_thenMapsNotEmpty() {
        assertTrue(MoneyTransferRestService.users.isEmpty());
        assertTrue(MoneyTransferRestService.transfers.isEmpty());
        assertTrue(MoneyTransferRestService.accounts.isEmpty());

        MoneyTransferRestService.createSomeTestObjects();

        assertFalse(MoneyTransferRestService.users.isEmpty());
        assertFalse(MoneyTransferRestService.transfers.isEmpty());
        assertFalse(MoneyTransferRestService.accounts.isEmpty());
    }

    @Test
    public void multiReadUsers_whenAllUserExist_thenAllUsersInResponse() {
        String passportId1 = "passportId1";
        String name1 = "name1";
        String passportId2 = "passportId2";
        String name2 = "name2";

        List<String> ids = Arrays.asList(passportId1, passportId2);
        WhereRequest whereRequest = new WhereRequest(new IdListRequest(ids));
        when(request.body()).thenReturn(gson.toJson(whereRequest));

        User expected1 = createUser(name1, passportId1);
        User expected2 = createUser(name2, passportId2);

        HashMap<String, User> expectedHashMap = new HashMap<>();
        expectedHashMap.put(expected2.getPassportId(), expected2);
        expectedHashMap.put(expected1.getPassportId(), expected1);
        MoneyTransferRestService.users = expectedHashMap;

        String responseString = MoneyTransferRestService.multiReadUsers(request, response);

        Type itemsListType = new TypeToken<List<User>>() {
        }.getType();
        ArrayList<User> actual = gson.fromJson(responseString, itemsListType);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        validateMultiRead(whereRequest);
        assertNotNull(actual);
        assertEquals(ids.size(), actual.size());
    }

    @Test
    public void multiReadUsers_whenPartOfUsersExist_thenPartOfUsersInResponse() {
        String passportId1 = "passportId1";
        String name1 = "name1";
        String passportId2 = "passportId2";

        List<String> ids = Arrays.asList(passportId1, passportId2);
        WhereRequest whereRequest = new WhereRequest(new IdListRequest(ids));
        when(request.body()).thenReturn(gson.toJson(whereRequest));

        HashMap<String, User> expectedHashMap = new HashMap<>();
        User expected1 = createUser(name1, passportId1);
        expectedHashMap.put(expected1.getPassportId(), expected1);
        MoneyTransferRestService.users = expectedHashMap;

        try {
            String responseString = MoneyTransferRestService.multiReadUsers(request, response);
            fail();
        } catch (Exception e) {
            assertEquals(e.getClass(), MoneyTransferException.class);
            assertTrue(e.getMessage().contains(PARTIAL_RESULT.message));
            assertEquals(ids.size(), ((MoneyTransferException) e).getResults().size());
            List successResult = ((MoneyTransferException) e).getResults().stream().filter(res -> res.getSuccess() != null).map(MoneyTransferExceptionDetailResult::getSuccess).collect(Collectors.toList());
            assertEquals(expectedHashMap.size(), successResult.size());
            successResult.forEach(result -> assertTrue(expectedHashMap.containsValue(result)));
        }
    }

    @Test
    public void multiReaAccounts_whenAllAccountsExist_thenAllAccountsInResponse() {
        Long id1 = 1L;
        Long id2 = 2L;
        Account account1 = new Account(id1, "userid1", BigDecimal.ZERO, AccountType.CREDIT, BigDecimal.TEN);
        Account account2 = new Account(id2, "userid1", BigDecimal.TEN, AccountType.DEBIT, BigDecimal.ZERO);

        List<String> ids = Arrays.asList(String.valueOf(id1), String.valueOf(id2));
        WhereRequest whereRequest = new WhereRequest(new IdListRequest(ids));
        when(request.body()).thenReturn(gson.toJson(whereRequest));

        Map<Long, Account> accountsMap = new ConcurrentHashMap<>();
        accountsMap.put(account1.getId(), account1);
        accountsMap.put(account2.getId(), account2);
        MoneyTransferRestService.accounts = accountsMap;

        String responseString = MoneyTransferRestService.multiReadAccounts(request, response);

        Type itemsListType = new TypeToken<List<User>>() {
        }.getType();
        ArrayList<Account> actual = gson.fromJson(responseString, itemsListType);
        PowerMockito.verifyStatic(MoneyTransferRequestValidator.class);
        validateMultiRead(whereRequest);
        assertNotNull(actual);
        assertEquals(ids.size(), actual.size());
    }

    @Test
    public void multiReaAccounts_whenAllAccountsNotExist_thenAllAccountsInResponse() {
        String id1 = "1";
        String id2 = "2";

        List<String> ids = Arrays.asList(id1, id2);
        WhereRequest whereRequest = new WhereRequest(new IdListRequest(ids));
        when(request.body()).thenReturn(gson.toJson(whereRequest));
        MoneyTransferRestService.accounts = new ConcurrentHashMap<>();

        try {
            String responseString = MoneyTransferRestService.multiReadUsers(request, response);
            fail();
        } catch (Exception e) {
            assertEquals(e.getClass(), MoneyTransferException.class);
            assertTrue(e.getMessage().contains(ALL_FAILED.message));
            assertEquals(ids.size(), ((MoneyTransferException) e).getResults().size());
        }
    }

    @Test
    public void multiReaAccounts_whenEmptyIdList_thenExceptionIsThrows() {

        List<String> ids = new ArrayList<>();
        WhereRequest whereRequest = new WhereRequest(new IdListRequest(ids));
        when(request.body()).thenReturn(gson.toJson(whereRequest));

        try {
            String responseString = MoneyTransferRestService.multiReadUsers(request, response);
            fail();
        } catch (Exception e) {
            assertEquals(e.getClass(), MoneyTransferException.class);
            assertTrue(e.getMessage().contains(ID_LIST_IS_EMPTY.message));
        }
    }

    private User createUser(String name, String passportId) {
        User user = new User();
        user.setPassportId(passportId);
        user.setName(name);

        return user;
    }

}
