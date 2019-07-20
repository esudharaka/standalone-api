package com.programming.repository;

import com.programming.domain.Account;

import java.util.List;

public interface AccountDataSource {
    List<Account> getAccountList();
}
