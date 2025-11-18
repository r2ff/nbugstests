package nbugs.requests.steps;

import nbugs.models.*;
import nbugs.requests.skelethon.Endpoint;
import nbugs.requests.skelethon.requesters.CrudRequester;
import nbugs.requests.skelethon.requesters.ValidatedCrudRequester;
import nbugs.specs.RequestSpecs;
import nbugs.specs.ResponseSpecs;

public class AccountSteps {

    public static CreateAccountResponse createAccount(CreateUserRequest userRequest) {
        return new ValidatedCrudRequester<CreateAccountResponse>(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);
    }

    public static CreateAccountResponse deposit(CreateUserRequest userRequest, CreateDepositRequest depositRequest) {
        return new ValidatedCrudRequester<CreateAccountResponse>(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);
    }

    public static CreateTransferResponse transfer(CreateUserRequest userRequest, CreateTransferRequest depositRequest) {
        return new ValidatedCrudRequester<CreateTransferResponse>(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);
    }
}
