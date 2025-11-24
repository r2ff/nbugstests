package nbugs.utils;

import nbugs.models.CreateAccountResponse;
import nbugs.models.TransferType;

import java.util.List;
import java.util.Objects;

public class AccountTransactionsUtils {

    public static Long findCountAccountTransactions(List<CreateAccountResponse> response, String accountNumber, TransferType type, Double amount) {
        return response.stream()
                .filter(it -> it.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElseThrow()
                .getTransactions().stream()
                .filter(it -> it.getType().equals(type.name()))
                .filter(it -> Objects.equals(it.getAmount(), amount))
                .count();
    }
}
