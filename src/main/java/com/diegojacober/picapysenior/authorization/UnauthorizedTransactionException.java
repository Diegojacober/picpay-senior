package com.diegojacober.picapysenior.authorization;

public class UnauthorizedTransactionException  extends RuntimeException {
    public UnauthorizedTransactionException(String message) {
        super(message);
    }
}
