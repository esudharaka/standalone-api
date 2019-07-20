package com.programming.repository;

import com.programming.domain.Account;

import java.util.concurrent.locks.Lock;

public interface TransferDao {
    Account getAccount(int id);
    Lock getAccountLock();
    void saveBalance(Account account);
}
