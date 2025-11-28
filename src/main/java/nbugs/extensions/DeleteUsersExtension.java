package nbugs.extensions;

import nbugs.requests.steps.AdminSteps;

import java.util.Arrays;

public class DeleteUsersExtension implements AroundTestExtension {
    @Override
    public void onEnd() {
        Arrays.stream(AdminSteps.getUsers()).map(it -> it.getId())
                .forEach(AdminSteps::deleteUser);

        AroundTestExtension.super.onEnd();
    }
}
