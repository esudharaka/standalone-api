package com.programming.repository;

import com.programming.domain.Account;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TransferDaoImpl implements TransferDao {

    private static TransferDaoImpl object;



    private AccountDataSource dataSource;
    private Lock accountsLock;


    public TransferDaoImpl() {
        this.dataSource = AccountDataSourceImpl.getInstance();
        this.accountsLock = new ReentrantLock();
    }

    public TransferDaoImpl(AccountDataSource dataSource, Lock lock) {
        this.dataSource = dataSource;
        this.accountsLock = lock;
    }

    public static TransferDaoImpl getInstance() {
        if (object != null) {
            return object;
        } else {
            synchronized (TransferDaoImpl.class){
                if(object == null) {
                    object = new TransferDaoImpl();
                }
            }
            return object;
        }
    }
    @Override
    public Account getAccount(final int id) {
        final List<Account> accountList = dataSource.getAccountList();
        final Optional<Account> requestedAccount = accountList.stream().filter(account -> account.getId() == id).findFirst();
        return requestedAccount.orElse(null);
    }


    @Override
    public Lock getAccountLock() {
        return accountsLock;
    }

    @Override
    public void saveBalance(Account targetAccount) {
        final List<Account> accountList = dataSource.getAccountList();
        Optional<Account> savedAccount = accountList.stream()
                .filter(account -> targetAccount.getId() == account.getId())
                .findAny();
        savedAccount.ifPresent(account -> account.setBalance(targetAccount.getBalance()));
    }
}
