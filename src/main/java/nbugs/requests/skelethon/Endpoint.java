package nbugs.requests.skelethon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nbugs.models.*;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USER(
            "/admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),

    DELETE_ADMIN_USER(
            "/admin/users/%s",
            BaseModel.class,
            BaseModel.class
    ),

    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),

    ACCOUNTS(
            "/accounts",
            BaseModel.class,
            CreateAccountResponse.class
    ),
    CUSTOMER_ACCOUNTS(
            "/customer/accounts",
            BaseModel.class,
            GetCustomerAccountsResponse.class
    ),
    GET_CUSTOMER_PROFILE(
            "/customer/profile",
            BaseModel.class,
            CreateUserResponse.class
    ),
    PUT_CUSTOMER_PROFILE(
            "/customer/profile",
            BaseModel.class,
            ChangeCustomerProfileResponse.class
    ),
    ACCOUNTS_DEPOSIT(
            "/accounts/deposit",
            BaseModel.class,
            CreateAccountResponse.class
    ),
    ACCOUNTS_TRANSFER(
            "/accounts/transfer",
            BaseModel.class,
            CreateTransferResponse.class
    ),
    ;


    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;
}
