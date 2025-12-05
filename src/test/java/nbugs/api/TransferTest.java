package nbugs.api;

import nbugs.dao.comparison.DaoAndModelAssertions;
import nbugs.models.*;
import nbugs.models.comparison.ModelAssertions;
import nbugs.requests.skelethon.Endpoint;
import nbugs.requests.skelethon.requesters.CrudRequester;
import nbugs.requests.steps.AccountSteps;
import nbugs.requests.steps.AdminSteps;
import nbugs.requests.steps.CustomerSteps;
import nbugs.requests.steps.DataBaseSteps;
import nbugs.specs.RequestSpecs;
import nbugs.specs.ResponseSpecs;
import nbugs.utils.RepeatUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TransferTest extends BaseTest {

    @Test
    void makerValidBetweenOwnAccountTransfer() {
        var userRequest = AdminSteps.createUser();
        var createAccountResponse = AccountSteps.createAccount(userRequest);
        var createSecondAccountResponse = AccountSteps.createAccount(userRequest);

        var depositRequest = CreateDepositRequest.builder().id(createAccountResponse.getId()).build();
        AccountSteps.deposit(userRequest, depositRequest);

        var amount = depositRequest.getBalance() - 1;
        var transferRequest = CreateTransferRequest.builder()
                .senderAccountId(createAccountResponse.getId())
                .receiverAccountId(createSecondAccountResponse.getId())
                .amount(amount)
                .build();

        var transferResponse = AccountSteps.transfer(userRequest, transferRequest);

        ModelAssertions.assertThatModels(transferRequest, transferResponse).match();

        var accountResponse = CustomerSteps.getCustomerAccounts(userRequest);

//        var OutTransaction = findTransfer(accountResponse, TransferType.TRANSFER_OUT, createAccountResponse.getId(), amount);
//        assertThat(OutTransaction.getAmount()).isEqualTo(transferRequest.getAmount());
//        assertThat(OutTransaction.getRelatedAccountId()).isEqualTo(transferRequest.getReceiverAccountId());
//
//        var InTransaction = findTransfer(accountResponse, TransferType.TRANSFER_IN, createSecondAccountResponse.getId(), amount);
//        assertThat(InTransaction.getAmount()).isEqualTo(transferRequest.getAmount());
//        assertThat(InTransaction.getRelatedAccountId()).isEqualTo(transferRequest.getSenderAccountId());
    }

    @Test
    @Disabled
    void makeValidBetweenOwnAccountTransferCheckDb() {
        var userRequest = AdminSteps.createUser();
        var createAccountResponse = AccountSteps.createAccount(userRequest);
        var createSecondAccountResponse = AccountSteps.createAccount(userRequest);

        var depositRequest = CreateDepositRequest.builder().id(createAccountResponse.getId()).build();
        AccountSteps.depositV2(userRequest, depositRequest);

        var amount = depositRequest.getBalance() - 1;
        var transferRequest = CreateTransferRequest.builder()
                .senderAccountId(createAccountResponse.getId())
                .receiverAccountId(createSecondAccountResponse.getId())
                .amount(amount)
                .build();

        var transferResponse = AccountSteps.transfer(userRequest, transferRequest);

        ModelAssertions.assertThatModels(transferRequest, transferResponse).match();

//        var transferOut = DataBaseSteps.getTransactionByAccountId(transferRequest.getSenderAccountId(), TransferType.TRANSFER_OUT.name());
//        DaoAndModelAssertions.assertThat(transferRequest, transferOut).match();
//
//        var transferIn = DataBaseSteps.getTransactionByAccountId(transferRequest.getReceiverAccountId(), TransferType.TRANSFER_IN.name());
//        DaoAndModelAssertions.assertThat(CreateTransferRequest.builder()
//                .senderAccountId(createSecondAccountResponse.getId())
//                .receiverAccountId(createAccountResponse.getId())
//                .amount(amount)
//                .build(), transferIn).match();
    }

    @Test
    void makerValidBetweenDifferentUserAccountTransfer() {
        var user1 = AdminSteps.createUser();
        var user2 = AdminSteps.createUser();

        var createAccountResponse = AccountSteps.createAccount(user1);
        var createSecondUserAccountResponse = AccountSteps.createAccount(user2);

        var depositRequest = CreateDepositRequest.builder().id(createAccountResponse.getId()).build();
        AccountSteps.deposit(user1, depositRequest);

        var amount = depositRequest.getBalance() - 1;
        var transferRequest = CreateTransferRequest.builder()
                .senderAccountId(createAccountResponse.getId())
                .receiverAccountId(createSecondUserAccountResponse.getId())
                .amount(amount)
                .build();

        var transferResponse = AccountSteps.transfer(user1, transferRequest);

        ModelAssertions.assertThatModels(transferRequest, transferResponse).match();

        var accountUser1Response = CustomerSteps.getCustomerAccounts(user1);

//        var OutTransaction = findTransfer(accountUser1Response, TransferType.TRANSFER_OUT, createAccountResponse.getId(), amount);
//        assertThat(OutTransaction.getAmount()).isEqualTo(transferRequest.getAmount());
//        assertThat(OutTransaction.getRelatedAccountId()).isEqualTo(transferRequest.getReceiverAccountId());
//
//        var accountUser2Response = CustomerSteps.getCustomerAccounts(user2);
//
//        var InTransaction = findTransfer(accountUser2Response, TransferType.TRANSFER_IN, createSecondUserAccountResponse.getId(), amount);
//        assertThat(InTransaction.getAmount()).isEqualTo(transferRequest.getAmount());
//        assertThat(InTransaction.getRelatedAccountId()).isEqualTo(transferRequest.getSenderAccountId());
    }

    @ParameterizedTest
    @MethodSource("transferInvalidAmount")
    void makeInvalidTransfer(Double amount, Integer depositCount, String errorText) {
        var userRequest = AdminSteps.createUser();
        var createAccountResponse = AccountSteps.createAccount(userRequest);
        var createSecondAccountResponse = AccountSteps.createAccount(userRequest);

        RepeatUtils.repeat(depositCount, () -> {
            var depositRequest = CreateDepositRequest.builder().id(createAccountResponse.getId())
                    .balance(5_000.0).build();
            AccountSteps.deposit(userRequest, depositRequest);
        });

        var transferRequest = CreateTransferRequest.builder()
                .senderAccountId(createAccountResponse.getId())
                .receiverAccountId(createSecondAccountResponse.getId())
                .amount(amount)
                .build();

        new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsBadRequest(errorText))
                .post(transferRequest);

    }

    @ParameterizedTest
    @MethodSource("transferInvalidAccountId")
    void makeInvalidTransferAccountId(Long receiverAccountId, Long senderAccountId, String errorText) {
        var userRequest = AdminSteps.createUser();
        var createAccountResponse = AccountSteps.createAccount(userRequest);
        var createSecondAccountResponse = AccountSteps.createAccount(userRequest);

        var depositRequest = CreateDepositRequest.builder().id(createAccountResponse.getId()).build();
        var depositSecondRequest = CreateDepositRequest.builder().id(createSecondAccountResponse.getId()).build();
        AccountSteps.deposit(userRequest, depositRequest);
        AccountSteps.deposit(userRequest, depositSecondRequest);

        receiverAccountId = receiverAccountId == null ? createSecondAccountResponse.getId() : receiverAccountId;
        senderAccountId = senderAccountId == null ? createAccountResponse.getId() : senderAccountId;

        var transferRequest = CreateTransferRequest.builder()
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .amount(depositRequest.getBalance() - 1)
                .build();

        new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsForbiddenRequest(errorText))
                .post(transferRequest);

    }

    public static Stream<Arguments> transferInvalidAmount() {
        return Stream.of(
                //todo уточнить требования
                //Arguments.of(10_000.1, 3, "Transfer amount cannot exceed 10000"),
                Arguments.of(0.0, 1, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(-0.1, 1, "Invalid transfer: insufficient funds or invalid accounts"),
                Arguments.of(6_000.0, 1, "Invalid transfer: insufficient funds or invalid accounts")
        );
    }

    public static Stream<Arguments> transferInvalidAccountId() {
        return Stream.of(
                Arguments.of(Long.MAX_VALUE, Long.MAX_VALUE, "Unauthorized access to account"),
                Arguments.of(null, Long.MAX_VALUE, "Unauthorized access to account")
        );
    }

    private Transaction findTransfer(GetCustomerAccountsResponse[] getCustomerAccountsResponse,
                                     TransferType transferType,
                                     Long accountId,
                                     Double amount) {
        return Arrays.stream(getCustomerAccountsResponse)
                .filter(it -> it.getId().equals(accountId))
                .findFirst()
                .orElseThrow()
                .getTransactions().stream()
                .filter(it -> it.getType().equals(transferType.name()))
                .filter(it -> it.getAmount().equals(amount))
                .findFirst()
                .orElseThrow();
    }
}
