package com.programming.service;

import com.programming.controller.to.TransferRequest;
import com.programming.controller.to.TransferResponse;
import com.programming.domain.Account;
import com.programming.exceptions.InSufficientFunds;
import com.programming.exceptions.NoAccountFound;

public interface TransferService {
    TransferResponse doTransfer(TransferRequest transferRequest) throws InSufficientFunds, NoAccountFound;
    Account getAccount(int id);
}
