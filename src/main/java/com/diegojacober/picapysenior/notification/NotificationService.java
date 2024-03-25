package com.diegojacober.picapysenior.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.diegojacober.picapysenior.transaction.Transaction;

@Service
public class NotificationService {

    @Autowired
    private NotificationProducer notificationProducer;

    public void notify(Transaction transaction) {
        notificationProducer.sendNotification(transaction);
    }
}
