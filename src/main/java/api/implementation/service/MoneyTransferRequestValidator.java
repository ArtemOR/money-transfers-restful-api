package api.implementation.service;

import api.implementation.exception.ExceptionList;
import api.implementation.exception.MoneyTransferException;
import api.implementation.model.Account;
import api.implementation.model.AccountTransfer;
import api.implementation.model.request.AccountRequest;
import api.implementation.model.request.TransferRequest;
import api.implementation.model.request.UserRequest;

import java.util.List;
import java.util.Objects;

import static api.implementation.service.MoneyTransferRestService.accounts;
import static api.implementation.service.MoneyTransferRestService.users;
import static api.implementation.service.StringConstants.*;

public class MoneyTransferRequestValidator {

    static void assertNotNull(Object object) {
        if (Objects.isNull(object)) {
            throw new MoneyTransferException(ExceptionList.MISSING_MANDATORY_PARAMETERS, REQUEST);
        }
    }

    static void validateUser(UserRequest userRequest) {
        //check for mandatory params
        if (Objects.isNull(userRequest.getName())) {
            throw new MoneyTransferException(ExceptionList.MISSING_MANDATORY_PARAMETERS, NAME_PARAM);
        }
        String passportId = userRequest.getPassportId();
        if (Objects.isNull(passportId)) {
            throw new MoneyTransferException(ExceptionList.MISSING_MANDATORY_PARAMETERS, PASSPORT_ID_PARAM);
        }
        //check is this object already exist
        if (users.containsKey(passportId)) {
            throw new MoneyTransferException(ExceptionList.USER_ALREADY_EXIST, passportId);
        }
    }

    static void validateAccount(AccountRequest accountRequest) {
        String passportId = accountRequest.getPassportId();
        //check for mandatory params
        if (Objects.isNull(passportId)) {
            throw new MoneyTransferException(ExceptionList.MISSING_MANDATORY_PARAMETERS, PASSPORT_ID_PARAM);
        }
        //check that user exist
        if (!users.containsKey(passportId)) {
            throw new MoneyTransferException(ExceptionList.USER_NOT_FOUND, passportId);
        }

        //check that creditLimit and money balance not negative
        if (Objects.nonNull(accountRequest.getCreditLimit()) && Double.valueOf(accountRequest.getCreditLimit()) < 0) {
            throw new MoneyTransferException(ExceptionList.MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE, CREDIT_LIMIT_PARAM);
        }

        if (Objects.nonNull(accountRequest.getMoneyBalance()) && Double.valueOf(accountRequest.getMoneyBalance()) < 0)
            throw new MoneyTransferException(ExceptionList.MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE, MONEY_BALANCE_PARAM);
    }

    static void validateRecharging(TransferRequest transferRequest) {
        String accountId = transferRequest.getAccountToId();
        //check for mandatory params
        if (Objects.isNull(accountId)) {
            throw new MoneyTransferException(ExceptionList.MISSING_MANDATORY_PARAMETERS, ACCOUNT_ID_PARAM);
        }
        //check if the object exist
        if (!accounts.containsKey(Long.valueOf(accountId))) {
            throw new MoneyTransferException(ExceptionList.ACCOUNT_NOT_FOUND, accountId);
        }
        //check that amount of money is positive
        if (Objects.isNull(transferRequest.getAmount())) {
            throw new MoneyTransferException(ExceptionList.MISSING_MANDATORY_PARAMETERS, AMOUNT_PARAM);
        }

        if (Double.valueOf(transferRequest.getAmount()) <= 0) {
            throw new MoneyTransferException(ExceptionList.MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE, AMOUNT_PARAM);
        }
    }

    static void validateTransferBetweenAccounts(TransferRequest transferRequest) {

        //check for mandatory parameters an that all accounts exist
        String accountFromId = transferRequest.getAccountFromId();
        if (Objects.isNull(accountFromId)) {
            throw new MoneyTransferException(ExceptionList.MISSING_MANDATORY_PARAMETERS, ACCOUNT_FROM_ID_PARAM);
        }
        String accountToId = transferRequest.getAccountToId();
        if (accountFromId.equals(accountToId)) {
            throw new MoneyTransferException(ExceptionList.CHOSE_DIFFERENT_ACCOUNT_IDS);
        }
        if (Objects.isNull(accountToId)) {
            throw new MoneyTransferException(ExceptionList.MISSING_MANDATORY_PARAMETERS, ACCOUNT_TO_ID_PARAM);
        }
        if (!accounts.containsKey(Long.valueOf(accountToId))) {
            throw new MoneyTransferException(ExceptionList.ACCOUNT_NOT_FOUND, accountToId);
        }
        if (!accounts.containsKey(Long.valueOf(accountFromId))) {
            throw new MoneyTransferException(ExceptionList.ACCOUNT_NOT_FOUND, accountFromId);
        }

        String value = transferRequest.getAmount();
        if (Objects.isNull(value)) {
            throw new MoneyTransferException(ExceptionList.MISSING_MANDATORY_PARAMETERS, ACCOUNT_TO_ID_PARAM);
        }
        //check that value is positive
        if (Double.valueOf(value) < 0) {
            throw new MoneyTransferException(ExceptionList.MONEY_PARAMETER_SHOULD_CONTAIN_POSITIVE_VALUE, AMOUNT_PARAM);
        }
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

    static void assertTransfersExist(List<AccountTransfer> transfers, String accountId) {
        //check if account exist
        if (transfers.isEmpty()) {
            throw new MoneyTransferException(ExceptionList.TRANSFER_NOT_FOUND, accountId);
        }
    }

}
