package com.densoft.springcusomerscrm.DAO;

import com.densoft.springcusomerscrm.model.Customer;

import java.util.List;

public interface CustomerDAO {
    List<Customer> getCustomers();

    void saveCustomer(Customer customer);

    Customer getCustomer(int id);

    void deleteCustomer(int id);
}
