package com.yrgo.services.customers;

import com.yrgo.dataaccess.CustomerDao;
import com.yrgo.dataaccess.CustomerDaoJdbcTemplateImpl;
import com.yrgo.dataaccess.RecordNotFoundException;
import com.yrgo.domain.Call;
import com.yrgo.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Component("customerService")
@Transactional
public class CustomerManagementProductionImpl implements CustomerManagementService {
    private HashMap<String, Customer> customerMap;

    // @Autowired
    private CustomerDao customerDao;

    public CustomerManagementProductionImpl() {
        customerMap = new HashMap<String,Customer>();
        customerMap.put("OB74", new Customer("OB74" ,"Fargo Ltd", "some notes"));
        customerMap.put("NV10", new Customer("NV10" ,"North Ltd", "some other notes"));
        customerMap.put("RM210", new Customer("RM210" ,"River Ltd", "some more notes"));
    }

    @Override
    public void newCustomer(Customer newCustomer) {
        customerDao.create(newCustomer);
    }

    @Override
    public void updateCustomer(Customer updatedCustomer) throws CustomerNotFoundException {
        try {
            customerDao.update(updatedCustomer);
        } catch (RecordNotFoundException e) {
            throw new CustomerNotFoundException();
        }
    }

    @Override
    public void deleteCustomer(Customer oldCustomer) {
        try {
            customerDao.delete(oldCustomer);
        } catch (RecordNotFoundException e) {
            System.err.println();
        }
    }

    @Override
    public Customer findCustomerById(String customerId) throws CustomerNotFoundException {
        try {
            return customerDao.getById(customerId);
        } catch (RecordNotFoundException e) {
            throw new CustomerNotFoundException();
        }
    }

    @Override
    public List<Customer> findCustomersByName(String customerName) {
        return customerDao.getByName(customerName);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerDao.getAllCustomers();
    }

    @Override
    public Customer getFullCustomerDetail(String customerId) throws CustomerNotFoundException {
        try {
            return customerDao.getFullCustomerDetail(customerId);
        } catch (RecordNotFoundException e) {
            throw new CustomerNotFoundException();
        }
    }

    @Override
    public void recordCall(String customerId, Call callDetails) throws CustomerNotFoundException {
        try {
            customerDao.addCall(callDetails, customerId);
        } catch (RecordNotFoundException e) {
            throw new CustomerNotFoundException();
        }
    }

    // Assignment 4: Changed CustomerDaoJdbcTemplateImpl customerDao for CustomerDao customerDao
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }
}
