package api.implementation.converter;

import api.implementation.model.Account;
import api.implementation.model.AccountType;
import api.implementation.model.User;
import api.implementation.model.request.AccountRequest;
import api.implementation.model.request.TransferRequest;
import api.implementation.model.request.UserRequest;
import api.implementation.model.transfer.BetweenAccountsTransfer;
import api.implementation.model.transfer.InsideAccountTransfer;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MoneyTransferModelConverterTest {
    MoneyTransferModelConverter underTest = new MoneyTransferModelConverter();

    @Test
    public void convertUserRequest_whenMethodIsCalls_thenUserReturns(){
        UserRequest userRequest = new UserRequest();
        userRequest.setName("name");
        userRequest.setPassportId("passportId");
        User expected = new User();
        expected.setName(userRequest.getName());
        expected.setPassportId(userRequest.getPassportId());

        User actual = underTest.convertUserRequest(userRequest);

        assertEquals(expected, actual);
    }

    @Test
    public void convertAccountRequest_whenRequestHasNotCreditBalance_thenDebitAccountReturns(){
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setPassportId("passportId");
        accountRequest.setMoneyBalance("1000");
        Account expected = new Account();
        expected.setPassportId(accountRequest.getPassportId());
        expected.setMoneyBalance(new BigDecimal(accountRequest.getMoneyBalance()));
        expected.setCreditLimit(BigDecimal.ZERO);
        expected.setAccountType(AccountType.DEBIT);

        Account actual = underTest.convertAccountRequest(accountRequest);

        assertEquals(expected, actual);
    }

    @Test
    public void convertAccountRequest_whenRequestHasCreditBalance_thenCreditAccountReturns(){
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setPassportId("passportId");
        accountRequest.setCreditLimit("1000");
        Account expected = new Account();
        expected.setPassportId(accountRequest.getPassportId());
        expected.setMoneyBalance(BigDecimal.ZERO);
        expected.setCreditLimit(new BigDecimal(accountRequest.getCreditLimit()));
        expected.setAccountType(AccountType.CREDIT);

        Account actual = underTest.convertAccountRequest(accountRequest);

        assertEquals(expected, actual);
    }

    @Test
    public void convertTransferRequest_whenMethodIsCalls_thenInsideAccountTransferReturns(){
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountToId("123");
        transferRequest.setAmount("1000");
        InsideAccountTransfer expected = new InsideAccountTransfer();
        expected.setAccountToId(Long.valueOf(transferRequest.getAccountToId()));
        expected.setAmount(new BigDecimal(transferRequest.getAmount()));
        expected.setTime(System.currentTimeMillis());

        InsideAccountTransfer actual = underTest.convertTransferRequest(transferRequest);

        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getAccountToId(), actual.getAccountToId());
        assertTrue(expected.getTime()<=actual.getTime());
    }

    @Test
    public void convertBetweenAccountsTransferRequestt_whenMethodIsCalls_thenBetweenAccountsTransferReturns(){
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAccountToId("123");
        transferRequest.setAccountFromId("122");
        transferRequest.setAmount("1000");

        BetweenAccountsTransfer expected = new BetweenAccountsTransfer();
        expected.setAccountToId(Long.valueOf(transferRequest.getAccountToId()));
        expected.setAccountFromId(Long.valueOf(transferRequest.getAccountFromId()));
        expected.setAmount(new BigDecimal(transferRequest.getAmount()));
        expected.setTime(System.currentTimeMillis());

        BetweenAccountsTransfer actual = underTest.convertBetweenAccountsTransferRequest(transferRequest);

        assertEquals(expected.getAmount(), actual.getAmount());
        assertEquals(expected.getAccountToId(), actual.getAccountToId());
        assertEquals(expected.getAccountFromId(), actual.getAccountFromId());
        assertTrue(expected.getTime()<=actual.getTime());
    }
}
