package nbugs.requests.steps;

import nbugs.models.*;
import nbugs.requests.skelethon.Endpoint;
import nbugs.requests.skelethon.requesters.CrudRequester;
import nbugs.requests.skelethon.requesters.ValidatedCrudRequester;
import nbugs.specs.RequestSpecs;
import nbugs.specs.ResponseSpecs;

import java.util.List;

public class CustomerSteps {

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
}
