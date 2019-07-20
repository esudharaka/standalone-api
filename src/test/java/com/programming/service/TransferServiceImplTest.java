package com.programming.service;

import com.programming.controller.to.TransferRequest;
import com.programming.controller.to.TransferResponse;
import com.programming.domain.Account;
import com.programming.exceptions.InSufficientFunds;
import com.programming.exceptions.NoAccountFound;
import com.programming.repository.AccountDataSource;
import com.programming.repository.AccountDataSourceImpl;
import com.programming.repository.TransferDao;
import com.programming.repository.TransferDaoImpl;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransferServiceImplTest {

    @Test
    public void testMoneyTransfer() throws Exception {

        Account account1 = new Account(1, new BigDecimal(100));
        Account account2 = new Account(2, new BigDecimal(50));
        List<Account> accounts = Arrays.asList(account1, account2);

        AccountDataSource accountDataSource = new AccountDataSourceImpl(accounts);
        Lock lock = new ReentrantLock();
        TransferDao transferDao = new TransferDaoImpl(accountDataSource, lock);

        TransferService transferService = new TransferServiceImpl(transferDao);


        TransferRequest transferRequest = new TransferRequest(account1.getId(), account2.getId(), new BigDecimal(30)) ;

        transferService.doTransfer(transferRequest);

        Assert.assertEquals(account1.getBalance(), new BigDecimal(70));
        Assert.assertEquals(account2.getBalance(), new BigDecimal(80));
    }

    @Test
    public void testMoneyTransferMultipleTreadsWhenEnoughFunds() throws Exception {

        Account johnAcc = new Account(1, new BigDecimal(500));
        Account fredAxx = new Account(2, new BigDecimal(500));
        List<Account> accounts =  Arrays.asList(johnAcc, fredAxx);

        AccountDataSource accountDataSource = new AccountDataSourceImpl(accounts);
        Lock lock = new ReentrantLock();
        TransferDao transferDao = new TransferDaoImpl(accountDataSource, lock);

        TransferRequest transferRequest = new TransferRequest(johnAcc.getId(), fredAxx.getId(), new BigDecimal(50)) ;

        TransferService transferService = new TransferServiceImpl(transferDao);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<TransferCallable> callableList = new ArrayList<>();
        IntStream.rangeClosed(1, 10)
                .forEach(value -> callableList
                        .add( new TransferCallable(transferService, transferRequest)));

        executorService.invokeAll(callableList);
        executorService.shutdown();

        Assert.assertEquals(johnAcc.getBalance(), BigDecimal.ZERO);
        Assert.assertEquals(fredAxx.getBalance(), new BigDecimal(1000));

    }

    @Test
    public void testMoneyTransferMultipleTreadsWhenEnoughNoFunds() throws InterruptedException {

        Account johnAcc = new Account(1, new BigDecimal(500));
        Account fredAxx = new Account(2, new BigDecimal(500));
        List<Account> accounts =  Arrays.asList(johnAcc, fredAxx);

        AccountDataSource accountDataSource = new AccountDataSourceImpl(accounts);
        Lock lock = new ReentrantLock();
        TransferDao transferDao = new TransferDaoImpl(accountDataSource, lock);

        TransferRequest transferRequest = new TransferRequest(johnAcc.getId(), fredAxx.getId(), new BigDecimal(100)) ;

        TransferService transferService = new TransferServiceImpl(transferDao);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<TransferCallable> callableList = new ArrayList<>();
        IntStream.rangeClosed(1, 10)
                .forEach(value -> callableList
                        .add( new TransferCallable(transferService, transferRequest)));

        executorService.invokeAll(callableList);
        executorService.shutdown();

        Assert.assertEquals(johnAcc.getBalance(), BigDecimal.ZERO);
        Assert.assertEquals(fredAxx.getBalance(), new BigDecimal(1000));

    }

    @Test
    public void transferWhenForInvalidAccount() {
        Account johnAcc = new Account(1, new BigDecimal(500));
        Account fredAxx = new Account(2, new BigDecimal(500));
        List<Account> accounts =  Arrays.asList(johnAcc);

        AccountDataSource accountDataSource = new AccountDataSourceImpl(accounts);
        Lock lock = new ReentrantLock();
        TransferDao transferDao = new TransferDaoImpl(accountDataSource, lock);

        TransferRequest transferRequest = new TransferRequest(johnAcc.getId(), fredAxx.getId(), new BigDecimal(100)) ;

        TransferService transferService = new TransferServiceImpl(transferDao);


        assertThrows(NoAccountFound.class, () -> {
            transferService.doTransfer(transferRequest);
        });
    }

    @Test
    public void transferWhenInSufficientBalance() {
        Account johnAcc = new Account(1, new BigDecimal(500));
        Account fredAxx = new Account(2, new BigDecimal(500));
        List<Account> accounts =  Arrays.asList(johnAcc, fredAxx);

        AccountDataSource accountDataSource = new AccountDataSourceImpl(accounts);
        Lock lock = new ReentrantLock();
        TransferDao transferDao = new TransferDaoImpl(accountDataSource, lock);

        TransferRequest transferRequest = new TransferRequest(johnAcc.getId(), fredAxx.getId(), new BigDecimal(1000)) ;

        TransferService transferService = new TransferServiceImpl(transferDao);


        assertThrows(InSufficientFunds.class, () -> {
            transferService.doTransfer(transferRequest);
        });
    }

    @Test
    public void transferWhenSufficientBalance() throws InSufficientFunds, NoAccountFound {
        Account johnAcc = new Account(1, new BigDecimal(500));
        Account fredAxx = new Account(2, new BigDecimal(500));
        List<Account> accounts =  Arrays.asList(johnAcc, fredAxx);

        AccountDataSource accountDataSource = new AccountDataSourceImpl(accounts);
        Lock lock = new ReentrantLock();
        TransferDao transferDao = new TransferDaoImpl(accountDataSource, lock);

        TransferRequest transferRequest = new TransferRequest(johnAcc.getId(), fredAxx.getId(), new BigDecimal(20)) ;

        TransferService transferService = new TransferServiceImpl(transferDao);

        TransferResponse transferResponse = transferService.doTransfer(transferRequest);
        Assert.assertEquals(transferResponse.getStatus(), "Success");
        Assert.assertEquals(transferService.getAccount(johnAcc.getId()).getBalance(), new BigDecimal(480));

    }

    class TransferCallable implements Callable<TransferResponse>{
        TransferService transferService;
        TransferRequest transferRequest;
        public TransferCallable(TransferService transferService, TransferRequest transferRequest){
            this.transferService = transferService;
            this.transferRequest = transferRequest;
        }
        public TransferResponse call() throws InSufficientFunds, NoAccountFound {
            return transferService.doTransfer(transferRequest);
        }
    }
}
