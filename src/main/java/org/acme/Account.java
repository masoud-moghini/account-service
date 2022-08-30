package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import java.math.BigDecimal;


@Entity
public class Account extends PanacheEntity {


    @Id
    @GeneratedValue
    private Long id;
    public Long accountNumber;
    public Long customerNumber;
    public String customerName;
    public BigDecimal balance;
    public AccountStatus accountStatus = AccountStatus.OPEN;

    public void markOverdrawn() {
        accountStatus = AccountStatus.OVERDRAWN;
    }
    public void removeOverdrawnStatus() {
        accountStatus = AccountStatus.OPEN;
    }
    public void close() {
        accountStatus = AccountStatus.CLOSED;
        balance = BigDecimal.valueOf(0);
    }
    public void withdrawFunds(BigDecimal amount) {
        balance = balance.subtract(amount);
    }
    public void addFunds(BigDecimal amount) {
        balance = balance.add(amount);
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public Long getAccountNumber() {
        return accountNumber;
    }
    public String getCustomerName() {
        return customerName;
    }
    public AccountStatus getAccountStatus() {
        return accountStatus;
    }
}
