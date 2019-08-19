package api.implementation.service;

import api.implementation.exception.ExceptionList;
import api.implementation.exception.MoneyTransfersException;
import api.implementation.helper.IdGenerator;
import api.implementation.model.Account;
import api.implementation.model.AccountType;
import api.implementation.model.User;
import api.implementation.model.request.AccountRequest;
import api.implementation.model.request.UserRequest;

import java.math.BigDecimal;
import java.util.Objects;

public class ServiceHelper {

    private static final String CONTENT_TYPE = "application/json";
    private static final String REQUEST = "request";
    private static final String PASSPORT_ID_PARAM = "passportId";
    private static final String NAME_PARAM = "name";
    private static final String MONEY_BALANCE_PARAM = "moneyBalance";
    private static final String AMOUNT_PARAM = "amount";
    private static final String CREDIT_LIMIT_PARAM = "creditLimit";
    private static final String ACCOUNT_ID_PARAM = "accountId";
    private static final String ACCOUNT_TO_ID_PARAM = "accountToId";
    private static final String ACCOUNT_FROM_ID_PARAM = "accountFromId";

    private static IdGenerator getIdGenerator() {
        return IdGenerator.getInstance();
    }

     static void assertNotNull(Object object) {
        if (Objects.isNull(object)) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, REQUEST);
        }
    }

     static User convertResponseUser(UserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setPassportId(userRequest.getPassportId());
        user.setId(getIdGenerator().generateUserId());

        return user;
    }

     static void validateUser(UserRequest userRequest) {
        //check for mandatory params
        if (Objects.isNull(userRequest.getName())) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, NAME_PARAM);
        }
        String passportId = userRequest.getPassportId();
        if (Objects.isNull(passportId)) {
            throw new MoneyTransfersException(ExceptionList.MISSING_MANDATORY_PARAMETERS, PASSPORT_ID_PARAM);
        }

    }


     static Account convertResponseAccount(AccountRequest accountRequest) {
        Account account = new Account();
        long accountId = getIdGenerator().generateAccountId();
        account.setId(accountId);
        account.setPassportId(accountRequest.getPassportId());
        account.setMoneyBalance(Objects.isNull(accountRequest.getMoneyBalance()) ? BigDecimal.ZERO : new BigDecimal(accountRequest.getMoneyBalance()));
        account.setMoneyBalance(Objects.isNull(accountRequest.getCreditLimit()) ? BigDecimal.ZERO : new BigDecimal(accountRequest.getCreditLimit()));
        account.setAccountType(account.getCreditLimit().equals(BigDecimal.ZERO)? AccountType.DEBIT:AccountType.CREDIT);

        return account;
    }

}
