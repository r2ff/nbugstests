package nbugs.extensions;


import nbugs.annotations.UserSession;
import nbugs.models.CreateUserRequest;
import nbugs.pages.BasePage;
import nbugs.requests.steps.AdminSteps;
import nbugs.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.LinkedList;
import java.util.List;

public class UserSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        // Шаг 1: проверка, что у теста есть аннотация UserSession
        UserSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation != null) {
            int userCount = annotation.value();

            SessionStorage.clear();

            List<CreateUserRequest> users = new LinkedList<>();

            for (int i = 0; i < userCount; i++) {
                CreateUserRequest user = AdminSteps.createUser();
                users.add(user);
            }

            SessionStorage.addUsers(users);

            int authAsUser = annotation.auth();

            BasePage.authAsUser(SessionStorage.getUser(authAsUser));

            int accountUser = annotation.account();
            if (accountUser > 0) {
                SessionStorage.getSteps(accountUser)
                        .createAccount(SessionStorage.getUser(authAsUser).getUsername(),
                                SessionStorage.getUser(authAsUser).getPassword());
            }

        }
    }
}
