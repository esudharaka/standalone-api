package com.programming.service;

import com.programming.controller.to.TransferRequest;
import com.programming.controller.to.TransferResponse;
import com.programming.domain.Account;
import com.programming.exceptions.InSufficientFunds;
import com.programming.exceptions.NoAccountFound;
import com.programming.repository.TransferDao;
import com.programming.repository.TransferDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.concurrent.locks.Lock;

public class TransferServiceImpl  implements TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferServiceImpl.class);
    private static TransferService object;
    private TransferDao transferDao;


    private TransferServiceImpl() {
        transferDao = TransferDaoImpl.getInstance();
    }

    TransferServiceImpl(TransferDao transferDao) {
        this.transferDao = transferDao;
    }

    public static TransferService getInstance() {
        if (object != null) {
            return object;
        } else {
            synchronized (TransferServiceImpl.class){
                if(object == null) {
                    object = new TransferServiceImpl();
                }
            }
            return object;
        }
    }
    @Override
    public TransferResponse doTransfer(TransferRequest transferRequest) throws InSufficientFunds, NoAccountFound {
        LOGGER.info("Received a money transfer request : {} ", transferRequest);
        Lock accountLock = transferDao.getAccountLock();
        accountLock.lock();

        try {
            final Account fromAccount = getAccount(transferRequest.getFromAccount());
            final Account toAccount = getAccount(transferRequest.getToAccount());


            if (fromAccount == null || toAccount == null) {
                throw  new NoAccountFound(MessageFormat.format("Accounts not available from: {0}, to: {1}"
                        , fromAccount, toAccount));
            }
            LOGGER.info("Initial balance AccountId : {} , balance : {} ", fromAccount.getId(), fromAccount.getBalance());
            LOGGER.info("Initial balance AccountId : {} , balance : {} ", toAccount.getId(), toAccount.getBalance());

            if (fromAccount.hasEnoughBalance(transferRequest.getTransferAmount())) {
                fromAccount.transferMoney(toAccount, transferRequest.getTransferAmount());
                transferDao.saveBalance(fromAccount);
                transferDao.saveBalance(toAccount);
                LOGGER.info("Final balance AccountId : {} , balance : {} ", fromAccount.getId(), fromAccount.getBalance());
                LOGGER.info("Final balance AccountId : {} , balance : {} ", toAccount.getId(), toAccount.getBalance());
                final TransferResponse transferResponse = new TransferResponse();
                transferResponse.setStatus("Success");
                return transferResponse;
            } else {
                LOGGER.error("No Funds available for the transfer");
                throw new InSufficientFunds(MessageFormat.format("Insufficient balance found in account Id :{0}. " +
                        "Current Balance: {1}", fromAccount.getId(), fromAccount.getBalance()));
            }
        } finally {
            accountLock.unlock();
        }


    }

    @Override
    public Account getAccount(int id) {
        Lock accountLock = transferDao.getAccountLock();
        accountLock.lock();
        try {
            return transferDao.getAccount(id);
        } finally {
            accountLock.unlock();
        }
    }
}
