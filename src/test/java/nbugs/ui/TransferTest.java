package nbugs.ui;

import nbugs.annotations.UserSession;
import nbugs.models.CreateDepositRequest;
import nbugs.models.TransferType;
import nbugs.pages.BankAlert;
import nbugs.pages.TransferPage;
import nbugs.pages.UserDashboard;
import nbugs.storage.SessionStorage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static nbugs.pages.BankAlert.*;
import static nbugs.pages.TransferPage.MAKE_TRANSFER_TEXT;
import static nbugs.utils.AccountTransactionsUtils.findCountAccountTransactions;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferTest extends BaseUiTest {

    @Test
    @UserSession
    void userCanOpenTransferPage() {
        var transferPage = new UserDashboard().open().openTransferPage();
        assertThat(transferPage.getTransferMoneyText().getText()).isEqualTo(MAKE_TRANSFER_TEXT);
    }

    @Test
    @UserSession
    void createSuccessfulTransfer() {
        var accountFirst = SessionStorage.getSteps().createAccount(SessionStorage.getUser());
        var accountSecond = SessionStorage.getSteps().createAccount(SessionStorage.getUser());
        var depositRequest = CreateDepositRequest.builder().id(accountFirst.getId()).build();
        SessionStorage.getSteps().deposit(SessionStorage.getUser(), depositRequest);
        var transferAmount = 1D;

        new TransferPage().open()
                .selectAccount(String.valueOf(accountFirst.getId()))
                .fillRecipientAccountNumber(accountSecond.getAccountNumber())
                .fillAmount(String.valueOf(transferAmount))
                .confirmDetails()
                .clickTransferButton()
                .checkAlertMessageAndAccept(SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT.getMessage().formatted(transferAmount, accountSecond.getAccountNumber()));

        var transferOut = findCountAccountTransactions(SessionStorage.getSteps().getAllAccounts(),
                accountFirst.getAccountNumber(),
                TransferType.TRANSFER_OUT,
                transferAmount);
        Assertions.assertThat(transferOut).isNotZero().isEqualTo(1);

        var transferIn = findCountAccountTransactions(SessionStorage.getSteps().getAllAccounts(),
                accountSecond.getAccountNumber(),
                TransferType.TRANSFER_IN,
                transferAmount);
        Assertions.assertThat(transferIn).isNotZero().isEqualTo(1);
    }

    @Test
    @UserSession
    void createEmptyTransfer() {
        new TransferPage().open()
                .clickTransferButton()
                .checkAlertMessageAndAccept(PLEASE_FILL_ALL_FIELDS_AND_CONFIRM.getMessage());
    }

    @ParameterizedTest
    @MethodSource("transferInvalidData")
    @UserSession
    void createTransferWithInvalidData(Double transferAmount, String account, BankAlert alert) {
        var accountFirst = SessionStorage.getSteps().createAccount(SessionStorage.getUser());
        var accountSecond = SessionStorage.getSteps().createAccount(SessionStorage.getUser());
        var depositRequest = CreateDepositRequest.builder().id(accountFirst.getId()).build();
        SessionStorage.getSteps().deposit(SessionStorage.getUser(), depositRequest);
        var receiverAccountNumber = getAccount(accountSecond.getAccountNumber(), account);

        new TransferPage().open()
                .selectAccount(String.valueOf(accountFirst.getId()))
                .fillRecipientAccountNumber(receiverAccountNumber)
                .fillAmount(String.valueOf(transferAmount))
                .confirmDetails()
                .clickTransferButton()
                .checkAlertMessageAndAccept(alert.getMessage());

        var transferOut = findCountAccountTransactions(SessionStorage.getSteps().getAllAccounts(),
                accountSecond.getAccountNumber(),
                TransferType.TRANSFER_OUT,
                transferAmount);
        Assertions.assertThat(transferOut).isZero();

        var transferIn = findCountAccountTransactions(SessionStorage.getSteps().getAllAccounts(),
                accountSecond.getAccountNumber(),
                TransferType.TRANSFER_IN,
                transferAmount);
        Assertions.assertThat(transferIn).isZero();
    }

    public static Stream<Arguments> transferInvalidData() {
        return Stream.of(
                Arguments.of(10_000.1, null, TRANSFER_AMOUNT_CANNOT_EXCEED_10000),
                Arguments.of(0.0, null, TRANSFER_AMOUNT_MUST_BE_AT_LEAST_001),
                Arguments.of(-0.1, null, TRANSFER_AMOUNT_MUST_BE_AT_LEAST_001),
                Arguments.of(6_000.0, null, INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS),
                Arguments.of(1.0, "ACC", NO_USER_FOUND_WITH_THIS_ACCOUNT_NUMBER)
        );
    }

    private String getAccount(String realAccount, String accountFromParameter) {
        if (accountFromParameter == null) {
            return realAccount;
        }
        return accountFromParameter;
    }
}
