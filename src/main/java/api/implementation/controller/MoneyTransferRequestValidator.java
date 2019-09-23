package api.implementation.controller;

import api.implementation.exception.ExceptionList;
import api.implementation.exception.MoneyTransferException;
import api.implementation.model.AccountTransfer;
import api.implementation.model.request.AccountRequest;
import api.implementation.model.request.TransferRequest;
import api.implementation.model.request.UserRequest;

import java.util.Collection;
import java.util.Objects;

import static api.implementation.constants.StringConstants.*;

public class MoneyTransferRequestValidator {

    public void assertObjectNotNull(Object object) {
        if (Objects.isNull(object)) {
            throw new MoneyTransferException(ExceptionList.MISSING_REQUEST);
        }
    }

    public void validateUser(UserRequest userRequest) {
        assertObjectNotNull(userRequest);
        //check for mandatory params
        verifyMandatoryParam(userRequest.getName(), NAME_PARAM);
        String passportId = userRequest.getPassportId();
        verifyMandatoryParam(passportId, PASSPORT_ID_PARAM);
    }

    public void validateAccount(AccountRequest accountRequest) {
        String passportId = accountRequest.getPassportId();
        //check for mandatory params
        verifyMandatoryParam(passportId, PASSPORT_ID_PARAM);
        //check that creditLimit and money balance not negative
        if (Objects.nonNull(accountRequest.getCreditLimit())) {
            verifyMoneyPositiveParam(accountRequest.getCreditLimit(), CREDIT_LIMIT_PARAM);
        }

        if (Objects.nonNull(accountRequest.getMoneyBalance())) {
            verifyMoneyPositiveParam(accountRequest.getMoneyBalance(), MONEY_BALANCE_PARAM);
        }
    }

    public void validateTransferInsideAccount(TransferRequest transferRequest) {
        assertObjectNotNull(transferRequest);
        String accountId = transferRequest.getAccountToId();
        //check for mandatory params
        verifyMandatoryParam(accountId, ACCOUNT_ID_PARAM);
        //check that amount of money is positive
        verifyMandatoryParam(transferRequest.getAmount(), AMOUNT_PARAM);
        verifyMoneyPositiveParam(transferRequest.getAmount(), AMOUNT_PARAM);
    }

    public void validateTransferRequestBetweenAccounts(TransferRequest transferRequest) {
        assertObjectNotNull(transferRequest);
        //check for mandatory parameters an that all accounts exist
        String accountFromId = transferRequest.getAccountFromId();
        verifyMandatoryParam(accountFromId, ACCOUNT_FROM_ID_PARAM);
        String accountToId = transferRequest.getAccountToId();
        if (accountFromId.equals(accountToId)) {
            throw new MoneyTransferException(ExceptionList.CHOSE_DIFFERENT_ACCOUNT_IDS);
        }
        verifyMandatoryParam(accountToId, ACCOUNT_TO_ID_PARAM);
        String value = transferRequest.getAmount();
        verifyMandatoryParam(value, AMOUNT_PARAM);

        //check that value is positive
        verifyMoneyPositiveParam(value, AMOUNT_PARAM);
    }

    public void assertTransfersExist(Collection<AccountTransfer> transfers, String accountId) {
        //check if account exist
        if (transfers.isEmpty()) {
            throw new MoneyTransferException(ExceptionList.TRANSFERS_NOT_FOUND, accountId);
        }
    }

    private void verifyMandatoryParam(String paramValue, String paramName){
        if (Objects.isNull(paramValue)) {
            throw new MoneyTransferException(ExceptionList.MISSING_MANDATORY_PARAMETERS, paramName);
        }
    }

    private void verifyMoneyPositiveParam(String value, String paramName){
        if (Double.valueOf(value) < 0) {
            throw new MoneyTransferException(ExceptionList.MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE, paramName);
        }
    }

}
