package nbugs.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import nbugs.utils.RetryUtils;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class EditUserPage extends BasePage<EditUserPage> {
    private SelenideElement editUserText = $(Selectors.byText("✏\uFE0F Edit Profile"));
    private SelenideElement enterUserName = $(Selectors.byClassName("form-control"));
    private SelenideElement saveChanges = $(Selectors.byText("\uD83D\uDCBE Save Changes"));

    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditUserPage changeUserName(String userName) {
        enterUserName.shouldBe(Condition.visible);
        RetryUtils.retry(() -> {
                    enterUserName.sendKeys(userName);
                    return enterUserName.has(Condition.value(userName));
                },
                result -> result,
                2,
                500
        );
        saveChanges.click();
        return this;
    }

    public EditUserPage clickSaveChanges() {
        enterUserName.shouldBe(Condition.visible);
        saveChanges.click();
        return this;
    }
}
