package com.programming.repository;

import com.programming.domain.Account;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccountDataSourceImpl implements AccountDataSource {


    private List<Account> accountList = new ArrayList<>();

    public Map<Integer, Lock> getAccountLockMap() {
        return accountLockMap;
    }

    private Map<Integer,Lock> accountLockMap = new HashMap<>();
    private static AccountDataSourceImpl object;
    private AccountDataSourceImpl() {
        loadAccounts();
    }

    public AccountDataSourceImpl(List<Account> accounts) {
        this.accountList = accounts;
    }

    public static AccountDataSourceImpl getInstance() {
        if (object != null) {
            return object;
        } else {
            synchronized (AccountDataSourceImpl.class){
                if(object == null) {
                    object = new AccountDataSourceImpl();
                }
            }
            return object;
        }
    }
    @Override
    public List<Account> getAccountList() {
        return accountList;
    }

    private void loadAccounts() {
        Account account1 = new Account(1, new BigDecimal(100));
        Account account2 = new Account(2, new BigDecimal(50));
        Account account3 = new Account(3, new BigDecimal(75));
        Account account4 = new Account(4, new BigDecimal(150));
        accountList.addAll(Arrays.asList(account1, account2, account3, account4));

        Lock account1Lock = new ReentrantLock();
        Lock account2Lock = new ReentrantLock();
        Lock account3Lock = new ReentrantLock();
        Lock account4Lock = new ReentrantLock();

        accountLockMap.put(account1.getId(), account1Lock);
        accountLockMap.put(account2.getId(), account2Lock);
        accountLockMap.put(account3.getId(), account3Lock);
        accountLockMap.put(account4.getId(), account4Lock);


    }



}
