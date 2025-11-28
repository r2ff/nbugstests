package nbugs.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class UserDashboard extends BasePage<UserDashboard> {
    public static final String EDIT_PROFILE_TEXT = "✏\uFE0F Edit Profile";

    private SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
    private SelenideElement createNewAccount = $(Selectors.byText("➕ Create New Account"));
    private SelenideElement depositMoney = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));
    private SelenideElement transferMoney = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));
    private SelenideElement editUserPage = $(Selectors.byClassName("user-name"));

    @Override
    public String url() {
        return "/dashboard";
    }

    public UserDashboard createNewAccount() {
        createNewAccount.click();
        return this;
    }

    public EditUserPage openEditUserPage() {
        editUserPage.click();
        return new EditUserPage();
    }

    public DepositPage openDepositPage() {
        depositMoney.click();
        return new DepositPage();
    }

    public TransferPage openTransferPage() {
        transferMoney.click();
        return new TransferPage();
    }
}
