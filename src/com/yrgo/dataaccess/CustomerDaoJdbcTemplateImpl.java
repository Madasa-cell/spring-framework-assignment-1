package com.yrgo.dataaccess;

import com.yrgo.domain.Call;
import com.yrgo.domain.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class CustomerDaoJdbcTemplateImpl implements CustomerDao {
    //SQL Strings
    private static final String DELETE_SQL = "delete from CUSTOMER where CUSTOMER_ID=?";
    private static final String UPDATE_SQL = "update CUSTOMER set COMPANY_NAME=?, , NOTES=? where CUSTOMER_ID=?";
    private static final String INSERT_SQL = "insert into CUSTOMER (CUSTOMER_ID, COMPANY_NAME, NOTES) values (?,?,?)";
    private static final String GET_CUSTOMER_ID_SQL = "select CUSTOMER_ID, COMPANY_NAME, NOTES from CUSTOMER where CUSTOMER_ID=?";
    private static final String GET_CUSTOMER_NAME_SQL = "select * from CUSTOMER where NAME=?";
    private static final String GET_ALL_CUSTOMER_SQL = "select * from CUSTOMER";
    private static final String GET_FULL_CUSTOMER_DETAILS = "select * from CUSTOMER_CALL where CUSTOMER_ID=?";
    private static final String ADD_CALL_SQL = "insert into CUSTOMER_CALL (CUSTOMER_ID, NOTES, CALL_DATE) values (?, ?, ?)";


    private JdbcTemplate jdbcTemplate;

    public CustomerDaoJdbcTemplateImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        dropTables();
        createTables();
        grantPrivilege();
    }

    // Assuming this has to look like the constructor.
    @Override
    public void create(Customer customer) {
        jdbcTemplate.update(INSERT_SQL, customer.getCustomerId(), customer.getCompanyName(), customer.getNotes());
    }

    public void grantPrivilege() {
        try {
            this.jdbcTemplate.update("grant all on CUSTOMER to public");
            this.jdbcTemplate.update("grant all on CUSTOMER_CALL to public");
        } catch (Exception e) {
            System.err.println("Couldn't grant privilege " + e.getMessage());
        }
    }

    public void dropTables() {
        try {
            this.jdbcTemplate.update("drop table CUSTOMER_CALL if exists");
            this.jdbcTemplate.update("drop table CUSTOMER if exists");
        } catch (Exception e) {
            System.err.println("Couldn't drop any tables " + e.getMessage());
        }
    }

    public void createTables() {
        try{
            this.jdbcTemplate.update("""
                create table CUSTOMER (
                    CUSTOMER_ID varchar(20) primary key,
                    COMPANY_NAME varchar(255),
                    NOTES varchar(255)
                )
            """);
            this.jdbcTemplate.update("""
                create table CUSTOMER_CALL (
                    CALL_ID integer generated by default as identity (start with 1),
                    CUSTOMER_ID varchar(20),
                    NOTES varchar(255),
                    CALL_DATE timestamp,
                    foreign key (CUSTOMER_ID) references CUSTOMER(CUSTOMER_ID)
                )
            """);
        } catch (Exception e) {
            System.err.println("Assuming the table already exists " + e.getMessage());
        }
    }

    @Override
    public Customer getById(String customerId) throws RecordNotFoundException {
        List<Customer> customers = jdbcTemplate.query(GET_CUSTOMER_ID_SQL, new CustomerRowMapper(), customerId);
        if (customers.isEmpty()) {
            throw new RecordNotFoundException();
        }
        Customer customer = customers.get(0);

        List<Call> calls = jdbcTemplate.query(GET_FULL_CUSTOMER_DETAILS, new CallRowMapper(), customerId);
        customer.setCalls(calls);

        return customer;
    }

    @Override
    public List<Customer> getByName(String name) {
        return this.jdbcTemplate.query(GET_CUSTOMER_NAME_SQL, new CustomerRowMapper(), name);
    }

    @Override
    public void update(Customer customerToUpdate) throws RecordNotFoundException {
        this.jdbcTemplate.update(UPDATE_SQL, customerToUpdate.getCompanyName(), customerToUpdate.getNotes(),
                customerToUpdate.getCustomerId());
    }

    @Override
    public void delete(Customer oldCustomer) throws RecordNotFoundException {
        this.jdbcTemplate.update(DELETE_SQL, oldCustomer.getCustomerId());
    }

    @Override
    public List<Customer> getAllCustomers() {
        return this.jdbcTemplate.query(GET_ALL_CUSTOMER_SQL, new CustomerRowMapper());
    }

    @Override
    public Customer getFullCustomerDetail(String customerId) throws RecordNotFoundException {
        Customer customer = getById(customerId);
        List<Call> calls = jdbcTemplate.query(GET_FULL_CUSTOMER_DETAILS, new CallRowMapper(), customerId);
        customer.setCalls(calls);
        return customer;
    }

    @Override
    public void addCall(Call newCall, String customerId) throws RecordNotFoundException {
        getById(customerId);
        this.jdbcTemplate.update(ADD_CALL_SQL, customerId, newCall.getNotes(), new Timestamp(newCall.getTimeAndDate().getTime()));
    }

    private static class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow (ResultSet rs, int arg1) throws SQLException {
            return new Customer(rs.getString("CUSTOMER_ID"), rs.getString("COMPANY_NAME"), rs.getString("NOTES"));
        }
    }

    private static class CallRowMapper implements RowMapper<Call> {
        @Override
        public Call mapRow (ResultSet rs, int arg1) throws SQLException {
            return new Call(rs.getString("NOTES"), rs.getTimestamp("CALL_DATE"));
        }
    }
}