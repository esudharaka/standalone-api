package com.programming.domain;

import com.programming.exceptions.InSufficientFunds;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountTest {
    @Test
    void testFundTransfer() throws InSufficientFunds {
        Account sourAccount = new Account(1, new BigDecimal(100));
        Account targetAccount = new Account(2, new BigDecimal(50));

        sourAccount.transferMoney(targetAccount,  new BigDecimal(10));
        Assert.assertEquals(targetAccount.getBalance(), new BigDecimal(60));
        Assert.assertEquals(sourAccount.getBalance(), new BigDecimal(90));
    }

    @Test
    void testInSufficientBalance() {
        final Account accountA = new Account(1, new BigDecimal(100));
        final Account accountB = new Account(2, new BigDecimal(50));

        assertThrows(InSufficientFunds.class, () -> {
            accountA.transferMoney(accountB,  new BigDecimal(200));
        });

    }
}
