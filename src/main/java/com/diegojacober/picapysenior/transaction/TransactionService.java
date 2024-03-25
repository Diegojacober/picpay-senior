package com.diegojacober.picapysenior.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diegojacober.picapysenior.authorization.AuthorizeService;
import com.diegojacober.picapysenior.notification.NotificationService;
import com.diegojacober.picapysenior.wallet.Wallet;
import com.diegojacober.picapysenior.wallet.WalletRepository;
import com.diegojacober.picapysenior.wallet.WalletType;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    private NotificationService notificationService;

    @SuppressWarnings("null")
    @Transactional
    public Transaction create(Transaction transaction) {
        // 1 - validar
        validate(transaction);

        // 2 - criar a transição
        var newTransaction = transactionRepository.save(transaction);

        // 3 - debitar da carteira
        var walletPayer = walletRepository.findById(transaction.payer()).get();
        var walletPayee = walletRepository.findById(transaction.payee()).get();
        walletRepository.save(walletPayer.debit(transaction.value()));
        walletRepository.save(walletPayee.credit(transaction.value()));

        // 4 - chamar serviços externos
        //autorizar transação
        authorizeService.authorize(transaction);

        // notificação
        notificationService.notify(transaction);

        return newTransaction;
    }

    /*
     * - the payer has a common wallet
     * - the payer has enough balance
     * - the payer is not the payee
     */
    @SuppressWarnings("null")
    private void validate(Transaction transaction) {
        walletRepository.findById(transaction.payee())
                .map(payee -> walletRepository.findById(transaction.payer())
                        .map(payer -> isTransactionValid(transaction, payer))
                        .orElseThrow(() -> new InvalidTransactionException(
                                "Invalid transaction - %s".formatted(transaction))))
                .orElseThrow(() -> new InvalidTransactionException("Invalid transaction - %s".formatted(transaction)));
    }

    private Transaction isTransactionValid(Transaction transaction, Wallet payer) {
        return payer.type() == WalletType.COMUM.getValue() &&
                payer.balance().compareTo(transaction.value()) >= 0 &&
                !payer.id().equals(transaction.payee()) ? transaction : null;
    }

    public List<Transaction> list() {
        return transactionRepository.findAll();
    }
}
