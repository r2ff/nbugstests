package nbugs.ui;


import com.codeborne.selenide.Condition;
import nbugs.annotations.Browsers;
import nbugs.models.CreateUserRequest;
import nbugs.pages.AdminPanel;
import nbugs.pages.LoginPage;
import nbugs.pages.UserDashboard;
import nbugs.requests.steps.AdminSteps;
import org.junit.jupiter.api.Test;

public class LoginUserTest extends BaseUiTest {
    @Test
    @Browsers({"chrome"})
    public void adminCanLoginWithCorrectDataTest() {
        CreateUserRequest admin = CreateUserRequest.getAdmin();

        new LoginPage().open().login(admin.getUsername(), admin.getPassword())
                        .getPage(AdminPanel.class).getAdminPanelText().shouldBe(Condition.visible);
    }

    @Test
    public void userCanLoginWithCorrectDataTest() {
        CreateUserRequest user = AdminSteps.createUser();

        new LoginPage().open().login(user.getUsername(), user.getPassword())
                .getPage(UserDashboard.class).getWelcomeText()
                .shouldBe(Condition.visible).shouldHave(Condition.text("Welcome, noname!"));
    }
}
