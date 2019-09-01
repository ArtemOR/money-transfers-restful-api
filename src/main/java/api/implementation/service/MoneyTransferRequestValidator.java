package api.implementation.service;

import api.implementation.exception.ExceptionList;
import api.implementation.exception.MoneyTransferException;
import api.implementation.model.Account;
import api.implementation.model.AccountTransfer;
import api.implementation.model.request.*;
import spark.utils.CollectionUtils;

import java.util.List;
import java.util.Objects;

import static api.implementation.service.MoneyTransferRestService.transfers;
import static api.implementation.service.MoneyTransferRestService.accounts;
import static api.implementation.service.MoneyTransferRestService.users;
import static api.implementation.service.StringConstants.*;

public class MoneyTransferRequestValidator {

    static void assertObjectNotNull(Object object) {
        if (Objects.isNull(object)) {
            throw new MoneyTransferException(ExceptionList.MISSING_REQUEST, REQUEST);
        }
    }

    static void validateUser(UserRequest userRequest) {
        //check for mandatory params
        verifyMandatoryParam(userRequest.getName(), NAME_PARAM);
        String passportId = userRequest.getPassportId();
        verifyMandatoryParam(passportId, PASSPORT_ID_PARAM);
        //check is this object already exist
        if (users.containsKey(passportId)) {
            throw new MoneyTransferException(ExceptionList.USER_ALREADY_EXIST, passportId);
        }
    }

    static void validateAccount(AccountRequest accountRequest) {
        String passportId = accountRequest.getPassportId();
        //check for mandatory params
        verifyMandatoryParam(passportId, PASSPORT_ID_PARAM);
        //check that user exist
        assertUserExist(passportId);
        System.out.println();
        //check that creditLimit and money balance not negative
        if (Objects.nonNull(accountRequest.getCreditLimit())) {
            verifyMoneyPositiveParam(accountRequest.getCreditLimit(), CREDIT_LIMIT_PARAM);
        }

        if (Objects.nonNull(accountRequest.getMoneyBalance())) {
            verifyMoneyPositiveParam(accountRequest.getMoneyBalance(), MONEY_BALANCE_PARAM);
        }
    }

    static void validateRecharging(TransferRequest transferRequest) {
        String accountId = transferRequest.getAccountToId();
        //check for mandatory params
        verifyMandatoryParam(accountId, ACCOUNT_ID_PARAM);
        //check if the object exist
        assertAccountExist(Long.valueOf(accountId));
        //check that amount of money is positive
        verifyMandatoryParam(transferRequest.getAmount(), AMOUNT_PARAM);
        verifyMoneyPositiveParam(transferRequest.getAmount(), AMOUNT_PARAM);
    }

    static void validateTransferBetweenAccounts(TransferRequest transferRequest) {

        //check for mandatory parameters an that all accounts exist
        String accountFromId = transferRequest.getAccountFromId();
        verifyMandatoryParam(accountFromId, ACCOUNT_FROM_ID_PARAM);
        String accountToId = transferRequest.getAccountToId();
        if (accountFromId.equals(accountToId)) {
            throw new MoneyTransferException(ExceptionList.CHOSE_DIFFERENT_ACCOUNT_IDS);
        }
        verifyMandatoryParam(accountToId, ACCOUNT_TO_ID_PARAM);
        assertAccountExist(Long.valueOf(accountToId));
        assertAccountExist(Long.valueOf(accountFromId));
        String value = transferRequest.getAmount();
        verifyMandatoryParam(value, AMOUNT_PARAM);

        //check that value is positive
        verifyMoneyPositiveParam(value, AMOUNT_PARAM);

        //check that there is enough money
        Account accountFrom = accounts.get(Long.valueOf(accountFromId));
        if ((accountFrom.getMoneyBalance().doubleValue() + accountFrom.getCreditLimit().doubleValue()) < Double.valueOf(value)) {
            throw new MoneyTransferException(ExceptionList.NOT_ENOUGH_MONEY);
        }
    }

    static void assertUserExist(String passportId) {
        //check if user exist
        if (!users.containsKey(passportId)) {
            throw new MoneyTransferException(ExceptionList.USER_NOT_FOUND, passportId);
        }
    }

    static void assertAccountExist(Long accountId) {
        //check if account exist
        if (!accounts.containsKey(accountId)) {
            throw new MoneyTransferException(ExceptionList.ACCOUNT_NOT_FOUND, accountId.toString());
        }
    }

    static void assertTransferExist(Long transferId) {
        //check if account exist
        if (!transfers.containsKey(transferId)) {
            throw new MoneyTransferException(ExceptionList.TRANSFER_NOT_FOUND, transferId.toString());
        }
    }

    static void assertTransfersExist(List<AccountTransfer> transfers, String accountId) {
        //check if account exist
        if (transfers.isEmpty()) {
            throw new MoneyTransferException(ExceptionList.TRANSFERS_NOT_FOUND, accountId);
        }
    }

    private static void verifyMandatoryParam(String paramValue, String paramName){
        if (Objects.isNull(paramValue)) {
            throw new MoneyTransferException(ExceptionList.MISSING_MANDATORY_PARAMETERS, paramName);
        }
    }

    private static void verifyMoneyPositiveParam(String value, String paramName){
        if (Double.valueOf(value) < 0) {
            throw new MoneyTransferException(ExceptionList.MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE, paramName);
        }
    }

    static List<String> validateMultiRead(WhereRequest request){
        IdListRequest idListRequest = request.getWhere();
        assertObjectNotNull(idListRequest);
        assertIdListNotEmpty(idListRequest.getIds());

        return request.getWhere().getIds();
    }

    private static void assertIdListNotEmpty(List list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new MoneyTransferException(ExceptionList.ID_LIST_IS_EMPTY);
        }
    }
}
