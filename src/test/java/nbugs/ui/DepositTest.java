package nbugs.ui;

import com.codeborne.selenide.Selenide;
import nbugs.annotations.UserSession;
import nbugs.models.CreateAccountResponse;
import nbugs.models.TransferType;
import nbugs.pages.BankAlert;
import nbugs.pages.DepositPage;
import nbugs.pages.UserDashboard;
import nbugs.storage.SessionStorage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static nbugs.pages.BankAlert.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DepositTest extends BaseUiTest{

    @Test
    @UserSession
    void userCanOpenDepositPage() {
        var depositPage = new UserDashboard().open().openDepositPage();
        assertThat(depositPage.getDepositMoneyText().getText()).isEqualTo("\uD83D\uDCB0 Deposit Money");
    }

    @Test
    @UserSession(account = 1)
    void createDeposit() {
        var amount = 5000.0;
        new DepositPage().open()
                .chooseFirstAccount()
                .enterAmount(amount)
                .clickDepositButton()
                .checkAlertMessageAndAccept(SUCCESSFULLY_DEPOSITED_TO_ACCOUNT.getMessage().formatted(amount));

        var deposits = findCountAccount(SessionStorage.getSteps().getAllAccounts(), TransferType.DEPOSIT, amount);
        Assertions.assertThat(deposits).isNotZero().isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("customerInvalidBalance")
    @UserSession(account = 1)
    void createInvalidDeposit(Double amount, BankAlert alert) {
        new DepositPage().open()
                .chooseFirstAccount()
                .enterAmount(amount)
                .clickDepositButton()
                .checkAlertMessageAndAccept(alert.getMessage().formatted(amount));

        var deposits = findCountAccount(SessionStorage.getSteps().getAllAccounts(), TransferType.DEPOSIT, amount);
        Assertions.assertThat(deposits).isZero();
    }

    @Test
    @UserSession(account = 1)
    void createWithNotChosenAccDeposit() {
        var amount = 5000.0;
        new DepositPage().open()
                .enterAmount(amount)
                .clickDepositButton()
                .checkAlertMessageAndAccept(PLEASE_SELECT_ACCOUNT.getMessage().formatted(amount));

        var deposits = findCountAccount(SessionStorage.getSteps().getAllAccounts(), TransferType.DEPOSIT, amount);
        Assertions.assertThat(deposits).isZero();
    }

    public static Stream<Arguments> customerInvalidBalance() {
        return Stream.of(
                Arguments.of(5000.1, PLEASE_DEPOSIT_LESS_OR_EQUAL_TO_5000),
                Arguments.of(0.0, PLEASE_ENTER_VALID_AMOUNT),
                Arguments.of(-0.1, PLEASE_ENTER_VALID_AMOUNT)
        );
    }

    private Long findCountAccount(List<CreateAccountResponse> response, TransferType type, Double amount) {
        return response.stream().findFirst().orElseThrow()
                .getTransactions().stream()
                .filter(it -> it.getType().equals(type.name()))
                .filter(it -> Objects.equals(it.getAmount(), amount))
                .count();
    }
}
