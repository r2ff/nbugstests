package nbugs.extensions;


import nbugs.annotations.AdminSession;
import nbugs.models.CreateUserRequest;
import nbugs.pages.BasePage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class AdminSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        // Шаг 1: проверка, есть ли у теста аннотация AdminSession
        AdminSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(AdminSession.class);
        if (annotation != null) { // Шаг 2: если есть, добавляем в local storage токен админа
            BasePage.authAsUser(CreateUserRequest.getAdmin());
        }
    }
}
