package com.yrgo.services.calls;

import com.yrgo.domain.Action;
import com.yrgo.domain.Call;
import com.yrgo.domain.Customer;
import com.yrgo.services.customers.CustomerManagementMockImpl;
import com.yrgo.services.customers.CustomerManagementService;
import com.yrgo.services.customers.CustomerNotFoundException;
import com.yrgo.services.diary.DiaryManagementService;
import com.yrgo.services.diary.DiaryManagementServiceMockImpl;

import java.util.Collection;
import java.util.HashMap;

public class CallHandlingServiceImpl implements CallHandlingService {

    private HashMap<String, Customer> customerMap;

    public CallHandlingServiceImpl() {
        customerMap = new HashMap<String, Customer>();
        customerMap.put("OB74", new Customer("OB74" ,"Fargo Ltd", "some notes"));
        customerMap.put("NV10", new Customer("NV10" ,"North Ltd", "some other notes"));
        customerMap.put("RM210", new Customer("RM210" ,"River Ltd", "some more notes"));
    }

    @Override
    public void recordCall(String customerId, Call newCall, Collection<Action> actions) throws CustomerNotFoundException {
        CustomerManagementService customerManagementService = new CustomerManagementMockImpl();
        customerManagementService.recordCall(customerId, newCall);

        DiaryManagementService diaryManagementService = new DiaryManagementServiceMockImpl();
        for (Action action: actions) {
            diaryManagementService.recordAction(action);
        }
    }
}
