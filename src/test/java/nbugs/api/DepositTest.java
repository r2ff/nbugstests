package nbugs.api;

import nbugs.dao.comparison.DaoAndModelAssertions;
import nbugs.models.CreateDepositRequest;
import nbugs.models.Transaction;
import nbugs.models.TransferType;
import nbugs.requests.skelethon.Endpoint;
import nbugs.requests.skelethon.requesters.CrudRequester;
import nbugs.requests.steps.AccountSteps;
import nbugs.requests.steps.AdminSteps;
import nbugs.requests.steps.CustomerSteps;
import nbugs.requests.steps.DataBaseSteps;
import nbugs.specs.RequestSpecs;
import nbugs.specs.ResponseSpecs;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DepositTest extends BaseTest {

    @Test
    @Disabled
    void changeDepositWithValidData() {
        var userRequest = AdminSteps.createUser();

        var createAccountResponse = AccountSteps.createAccount(userRequest);

        var depositRequest = CreateDepositRequest.builder().id(createAccountResponse.getId()).build();

        AccountSteps.deposit(userRequest, depositRequest);
        var depositResponseSecond = AccountSteps.deposit(userRequest, depositRequest);
        var accountResponse = CustomerSteps.getCustomerAccounts(userRequest);

        var account = Arrays.stream(accountResponse)
                .filter(it -> it.getId().equals(depositRequest.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(account.getTransactions()).isEqualTo(depositResponseSecond.getTransactions());
        assertThat(account.getBalance()).isEqualTo(depositResponseSecond.getTransactions().stream()
                .map(Transaction::getAmount)
                .reduce(0d, Double::sum));
    }

    @Test
    void changeDepositWithValidDataCheckDatabase() {
        var userRequest = AdminSteps.createUser();

        var createAccountResponse = AccountSteps.createAccount(userRequest);

        var depositRequest = CreateDepositRequest.builder().id(createAccountResponse.getId()).build();

        AccountSteps.depositV2(userRequest, depositRequest);

//        var transaction = DataBaseSteps.getTransactionByAccountId(createAccountResponse.getId(), TransferType.DEPOSIT.name());
//        DaoAndModelAssertions.assertThat(depositRequest, transaction).match();
    }

    @ParameterizedTest
    @MethodSource("customerInvalidBalance")
    void changeDepositWithInvalidValidBalance(Double balance, String errorText) {
        var userRequest = AdminSteps.createUser();

        var createAccountResponse = AccountSteps.createAccount(userRequest);
        var depositRequest = CreateDepositRequest.builder()
                .id(createAccountResponse.getId())
                .balance(balance).build();

        new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.requestReturnsBadRequest(errorText))
                .post(depositRequest);
    }

    @ParameterizedTest
    @MethodSource("customerInvalidAccountId")
    void changeDepositWithInvalidValidAccount(Long account, String errorText) {
        var userRequest = AdminSteps.createUser();
        AccountSteps.createAccount(userRequest);

        if (account == null) {
            account = AccountSteps.createAccount(AdminSteps.createUser()).getId();
        }

        var depositRequest = CreateDepositRequest.builder()
                .id(account)
                .build();

        new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.requestReturnsForbiddenRequest(errorText))
                .post(depositRequest);
    }

    public static Stream<Arguments> customerInvalidAccountId() {
        return Stream.of(
                Arguments.of(null, "Unauthorized access to account"),
                Arguments.of(Long.MAX_VALUE, "Unauthorized access to account")
        );
    }

    public static Stream<Arguments> customerInvalidBalance() {
        return Stream.of(
                //todo уточинть требования
                //Arguments.of(5000.01, "Deposit amount cannot exceed 5000"),
                Arguments.of(0.0, "Invalid account or amount"),
                Arguments.of(-0.1, "Invalid account or amount")
        );

    }
}
