package nbugs.requests.steps;


import nbugs.generators.RandomModelGenerator;
import nbugs.models.CreateUserRequest;
import nbugs.models.CreateUserResponse;
import nbugs.requests.skelethon.Endpoint;
import nbugs.requests.skelethon.requesters.CrudRequester;
import nbugs.requests.skelethon.requesters.ValidatedCrudRequester;
import nbugs.specs.RequestSpecs;
import nbugs.specs.ResponseSpecs;

import java.util.List;

public class AdminSteps {
    public static CreateUserRequest createUser() {
        CreateUserRequest userRequest =
                RandomModelGenerator.generate(CreateUserRequest.class);

        new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated())
                .post(userRequest);

        return userRequest;
    }

    public static void deleteUser(Long userId) {
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.DELETE_ADMIN_USER,
                ResponseSpecs.requestReturnsOK())
                .delete(userId);
    }

    public static List<CreateUserResponse> getUsers() {

        return new ValidatedCrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsOK())
                .getAll(CreateUserResponse[].class);
    }
}
