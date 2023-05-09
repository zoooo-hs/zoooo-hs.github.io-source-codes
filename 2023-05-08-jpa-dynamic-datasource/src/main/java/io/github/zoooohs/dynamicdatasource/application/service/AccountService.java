package io.github.zoooohs.dynamicdatasource.application.service;

import io.github.zoooohs.dynamicdatasource.datasource.TransactionManagerConfig;
import io.github.zoooohs.dynamicdatasource.domain.model.Account;
import io.github.zoooohs.dynamicdatasource.domain.model.Customer;
import io.github.zoooohs.dynamicdatasource.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerService customerService;

    @Transactional(TransactionManagerConfig.DYNAMIC_DATASOURCE_TM_NAME)
    public Account create(Integer customerId) {
        Customer customer = customerService.findById(customerId);
        Account account = new Account(customer);

        return accountRepository.save(account);
    }


    @Transactional(TransactionManagerConfig.DYNAMIC_DATASOURCE_TM_NAME)
    public Account send(Integer customerId, String customerAccountNumber, String recieverAccountNumber, Double amount) {
        Account customerAccount = accountRepository.findByAccountNumber(customerAccountNumber).filter(account -> account.getCustomer().getId().equals(customerId)).orElseThrow(() -> new RuntimeException("계좌 찾을 수 없음 : " + customerAccountNumber));
        Account recieverAccount = accountRepository.findByAccountNumber(recieverAccountNumber).orElseThrow(() -> new RuntimeException("계좌 찾을 수 없음 : " + recieverAccountNumber));
        customerAccount.send(amount, recieverAccount);
        return customerAccount;
    }

}