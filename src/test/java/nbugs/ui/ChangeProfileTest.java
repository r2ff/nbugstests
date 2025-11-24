package nbugs.ui;

import nbugs.annotations.UserSession;
import nbugs.generators.RandomModelGenerator;
import nbugs.models.ChangeCustomerProfileRequest;
import nbugs.pages.BankAlert;
import nbugs.pages.EditUserPage;
import nbugs.pages.UserDashboard;
import nbugs.requests.steps.AdminSteps;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeProfileTest extends BaseUiTest {

    @Test
    @UserSession
    void userCanOpenEditProfilePage() {
        var editUserPage = new UserDashboard().open().openEditUserPage();

        assertThat(editUserPage.getEditUserText().getText()).isEqualTo("✏\uFE0F Edit Profile");
    }

    @Test
    @UserSession
    void userCanChangeProfile() {
        var changeProfileRequest =
                RandomModelGenerator.generate(ChangeCustomerProfileRequest.class);
        new EditUserPage()
                .open().changeUserName(changeProfileRequest.getName())
                .checkAlertMessageAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMessage());

        var user = findUserCountByName(changeProfileRequest.getName());
        Assertions.assertThat(user).isNotZero().isEqualTo(1);
    }

    @Test
    @UserSession
    void userCanNotChangeProfile() {
        var name = "a";
        new EditUserPage()
                .open().changeUserName(name)
                .checkAlertMessageAndAccept(BankAlert.NAME_MUST_CONTAIN_TWO_WORDS_WITH_LETTERS_ONLY.getMessage());

        var user = findUserCountByName(name);
        Assertions.assertThat(user).isZero();
    }


    @Test
    @UserSession
    void userCanNotChangeProfileWithEmptyName() {
        new EditUserPage()
                .open().clickSaveChanges()
                .checkAlertMessageAndAccept(BankAlert.PLEASE_ENTER_VALID_NAME.getMessage());
    }

    private Long findUserCountByName(String name) {
        return AdminSteps.getUsers().stream()
                .filter(it -> it.getName() != null && it.getName().equals(name))
                .count();
    }
}
