package com.yrgo.client;

import com.yrgo.services.customers.CustomerManagementService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SimpleClient {
    public static void main(String[] args) {
        System.out.println("Testing Customer Management Service...");
        ClassPathXmlApplicationContext container = new ClassPathXmlApplicationContext("application.xml");
        CustomerManagementService customerManagementService = container.getBean(CustomerManagementService.class);

        customerManagementService.getAllCustomers().forEach(System.out::println);
        container.close();
    }
}
