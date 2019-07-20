package com.programming.domain;

import com.programming.exceptions.InSufficientFunds;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Objects;

public class Account {
    private int id;
    private BigDecimal balance;

    public Account(int id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void transferMoney(Account toAccount, BigDecimal amount) throws InSufficientFunds {
        if (getBalance().compareTo(amount) < 0) {
            throw new InSufficientFunds(MessageFormat.format("Insufficient balance found in account Id :{0}. " +
                    "Current Balance: {1}", this.getId(), toAccount.getBalance()));
        }
        this.setBalance(this.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }


    public boolean hasEnoughBalance(BigDecimal amountToBeTaken) {
        return this.getBalance().compareTo(amountToBeTaken) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id &&
                Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance=" + balance +
                '}';
    }
}
