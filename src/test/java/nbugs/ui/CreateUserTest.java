package nbugs.ui;


import nbugs.annotations.AdminSession;
import nbugs.elements.UserBage;
import nbugs.generators.RandomModelGenerator;
import nbugs.models.CreateUserRequest;
import nbugs.models.CreateUserResponse;
import nbugs.models.comparison.ModelAssertions;
import nbugs.pages.AdminPanel;
import nbugs.pages.BankAlert;
import nbugs.requests.steps.AdminSteps;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateUserTest extends BaseUiTest {

    @Test
    @AdminSession
    public void adminCanCreateUserTest() {
        CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);

        UserBage newUserBage = new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
                .checkAlertMessageAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY.getMessage())
                .findUserByUsername(newUser.getUsername());

        assertThat(newUserBage)
                .as("UserBage should exist on Dashboard after user creation").isNotNull();
        CreateUserResponse createdUser = AdminSteps.getUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername()))
                .findFirst().get();

        ModelAssertions.assertThatModels(newUser, createdUser).match();
    }

    @Test
    @AdminSession
    public void adminCannotCreateUserWithInvalidDataTest() {
        CreateUserRequest newUser = RandomModelGenerator.generate(CreateUserRequest.class);
        newUser.setUsername("a");

       assertTrue(new AdminPanel().open().createUser(newUser.getUsername(), newUser.getPassword())
               .checkAlertMessageAndAccept(BankAlert.USERNAME_MUST_BE_BETWEEN_3_AND_15_CHARACTERS.getMessage())
               .getAllUsers().stream().noneMatch(userBage -> userBage.getUsername().equals(newUser.getUsername())));

        long usersWithSameUsernameAsNewUser = AdminSteps.getUsers().stream()
                .filter(user -> user.getUsername().equals(newUser.getUsername())).count();

        assertThat(usersWithSameUsernameAsNewUser).isZero();
    }
}
