package api.implementation.service;

import api.implementation.exception.MoneyTransferException;
import api.implementation.model.User;
import org.junit.Test;

import static api.implementation.exception.ExceptionList.OBJECT_IS_NULL;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

public class MoneyTransferServiceTest {

    private MoneyTransferService underTest = new MoneyTransferService();

    @Test
    public void createUser_whenMethodIsCalls_thenUserReturns() {
        User expected = new User();
        expected.setPassportId("123");
        expected.setName("name");

        User actual = underTest.createUser(expected);

        assertEquals(expected, actual);
    }

    @Test
    public void createUser_whenUserIsNull_thenExceptionIsThrows() {
        User expected = null;

        assertThatThrownBy(() -> underTest.createUser(expected))
                .isInstanceOf(MoneyTransferException.class)
                .hasMessageContaining(OBJECT_IS_NULL.message);
    }

}
