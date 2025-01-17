package com.diegojacober.picapysenior.authorization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.diegojacober.picapysenior.transaction.Transaction;

@Service
public class AuthorizeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeService.class);
    private RestClient restClient;

    public AuthorizeService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc").build();
    }

    @SuppressWarnings("null")
    public void authorize(Transaction transaction) {
        var response = restClient.get().retrieve().toEntity(Authorization.class);

        if (response.getStatusCode().isError() || !response.getBody().isAuthorized()) {
            throw new UnauthorizedTransactionException("Unauthorized transaction!");
        }
    }
}
