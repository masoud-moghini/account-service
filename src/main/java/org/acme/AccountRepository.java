package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountRepository implements PanacheRepository<Account> {
    public Account findByAccountNumber(Long accountNumber){
        return find("accountNumber = ?",accountNumber).firstResult();
    }
}
