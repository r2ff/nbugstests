package nbugs.requests.steps;

import nbugs.models.*;
import nbugs.requests.skelethon.Endpoint;
import nbugs.requests.skelethon.requesters.CrudRequester;
import nbugs.requests.skelethon.requesters.ValidatedCrudRequester;
import nbugs.specs.RequestSpecs;
import nbugs.specs.ResponseSpecs;

import java.util.List;

public class CustomerSteps {

    private String username;
    private String password;

    public CustomerSteps(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public CustomerSteps(CreateUserRequest createUserRequest) {
        this.username = createUserRequest.getUsername();
        this.password = createUserRequest.getPassword();
    }

    public static GetCustomerAccountsResponse[] getCustomerAccounts(CreateUserRequest userRequest) {
        return new CrudRequester(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK()
        ).get(null).extract().body().as(GetCustomerAccountsResponse[].class);
    }

    public static CreateUserResponse getCustomerProfile(CreateUserRequest userRequest) {
        return new ValidatedCrudRequester<CreateUserResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.GET_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK()
        ).get(null);
    }

    public static ChangeCustomerProfileResponse changeCustomerProfile(CreateUserRequest userRequest, ChangeCustomerProfileRequest profileRequest) {
        return new ValidatedCrudRequester<ChangeCustomerProfileResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.PUT_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsOK()
        ).update(null, profileRequest);
    }

    public List<CreateAccountResponse> getAllAccounts() {
        return new ValidatedCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(username, password),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOK()).getAll(CreateAccountResponse[].class);
    }

    public CreateAccountResponse createAccount(String username, String password) {
        return new ValidatedCrudRequester<CreateAccountResponse>(RequestSpecs.authAsUser(username, password),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityWasCreated())
                .post(null);
    }

    public CreateAccountResponse createAccount(CreateUserRequest createUserRequest) {
        return createAccount(createUserRequest.getUsername(), createUserRequest.getPassword());
    }

    public CreateAccountResponse deposit(CreateUserRequest userRequest, CreateDepositRequest depositRequest) {
        return new ValidatedCrudRequester<CreateAccountResponse>(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_DEPOSIT,
                ResponseSpecs.requestReturnsOK())
                .post(depositRequest);
    }

    public CreateTransferResponse transfer(CreateUserRequest userRequest, CreateTransferRequest transferRequest) {
        return new ValidatedCrudRequester<CreateTransferResponse>(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS_TRANSFER,
                ResponseSpecs.requestReturnsOK())
                .post(transferRequest);
    }
}
