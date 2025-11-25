package nbugs.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class DepositPage extends BasePage<DepositPage> {

    public static final String DEPOSIT_MONEY_TEXT = "\uD83D\uDCB0 Deposit Money";

    private SelenideElement depositMoneyText = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));
    private SelenideElement accountSelector = $(Selectors.byClassName("account-selector"));
    private SelenideElement enterAmount = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement depositButton = $(Selectors.byText("\uD83D\uDCB5 Deposit"));
    private ElementsCollection options = $$("select.form-control.account-selector option");


    @Override
    public String url() {
        return "/deposit";
    }

    public DepositPage chooseFirstAccount() {
        options
                .excludeWith(text("-- Choose an account --"))
                .first()
                .click();
        return this;
    }

    public DepositPage enterAmount(Double amount) {
        enterAmount.sendKeys(String.valueOf(amount));
        return this;
    }

    public DepositPage clickDepositButton() {
        depositButton.click();
        return this;
    }
}
