package nbugs.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class TransferPage extends BasePage<TransferPage> {

    public static final String MAKE_TRANSFER_TEXT = "\uD83D\uDD04 Make a Transfer";

    private SelenideElement transferMoneyText = $(Selectors.byText("\uD83D\uDD04 Make a Transfer"));
    private SelenideElement transferMoneyButton = $(Selectors.byText("\uD83D\uDE80 Send Transfer"));
    private SelenideElement accountSelector = $(Selectors.byClassName("account-selector"));
    private SelenideElement namePlaceholder = $(Selectors.byAttribute("placeholder", "Enter recipient name"));
    private SelenideElement recipientAccountPlaceholder = $(Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private SelenideElement amountPlaceholder = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private SelenideElement confirm = $(Selectors.byId("confirmCheck"));


    @Override
    public String url() {
        return "/transfer";
    }

    public TransferPage selectAccount(String accountId) {
        accountSelector.selectOptionByValue(accountId);
        return this;
    }

    public TransferPage fillRecipientName(String name) {
        namePlaceholder.sendKeys(name);
        return this;
    }

    public TransferPage fillRecipientAccountNumber(String accountNumber) {
        recipientAccountPlaceholder.sendKeys(accountNumber);
        return this;
    }

    public TransferPage fillAmount(String amount) {
        amountPlaceholder.sendKeys(amount);
        return this;
    }

    public TransferPage confirmDetails() {
        confirm.click();
        return this;
    }

    public TransferPage clickTransferButton() {
        transferMoneyButton.click();
        return this;
    }
}
