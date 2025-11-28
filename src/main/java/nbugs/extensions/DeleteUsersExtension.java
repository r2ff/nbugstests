package nbugs.extensions;

import nbugs.annotations.AroundTestExtension;
import nbugs.requests.steps.AdminSteps;

public class DeleteUsersExtension implements AroundTestExtension {
    @Override
    public void onEnd() {
        AdminSteps.getUsers().stream().map(it -> it.getId())
                .forEach(AdminSteps::deleteUser);

        AroundTestExtension.super.onEnd();
    }
}
