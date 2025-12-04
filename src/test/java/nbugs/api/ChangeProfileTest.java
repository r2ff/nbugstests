package nbugs.api;

import nbugs.dao.comparison.DaoAndModelAssertions;
import nbugs.generators.RandomModelGenerator;
import nbugs.models.ChangeCustomerProfileRequest;
import nbugs.requests.skelethon.Endpoint;
import nbugs.requests.skelethon.requesters.CrudRequester;
import nbugs.requests.steps.AdminSteps;
import nbugs.requests.steps.CustomerSteps;
import nbugs.requests.steps.DataBaseSteps;
import nbugs.specs.RequestSpecs;
import nbugs.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ChangeProfileTest {

    private static final String errorText = "Name must contain two words with letters only";

    @Test
    void changeProfileWithValidData() {
        var userRequest = AdminSteps.createUser();
        var getCustomerProfileResponse = CustomerSteps.getCustomerProfile(userRequest);
        assertThat(getCustomerProfileResponse.getName()).isNull();

        var changeProfileRequest =
                RandomModelGenerator.generate(ChangeCustomerProfileRequest.class);

        var changeProfileResponse = CustomerSteps.changeCustomerProfile(userRequest, changeProfileRequest);
        assertThat(changeProfileResponse.getCustomer().getName()).isEqualTo(changeProfileRequest.getName());
        var getCustomerProfileResponseAfter = CustomerSteps.getCustomerProfile(userRequest);

        assertThat(getCustomerProfileResponseAfter.getName()).isEqualTo(changeProfileResponse.getCustomer().getName());

//        var userDao = DataBaseSteps.getUserByUsername(userRequest.getUsername());
//        DaoAndModelAssertions.assertThat(getCustomerProfileResponseAfter, userDao).match();
    }

    @ParameterizedTest
    @MethodSource("customerInvalidData")
    void changeProfileWithInvalidData(String name, String errorText) {
        var userRequest = AdminSteps.createUser();
        var getCustomerProfileResponse = CustomerSteps.getCustomerProfile(userRequest);
        assertThat(getCustomerProfileResponse.getName()).isNull();

        var changeProfileRequest = ChangeCustomerProfileRequest.builder()
                .name(name).build();

        new CrudRequester(RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.PUT_CUSTOMER_PROFILE,
                ResponseSpecs.requestReturnsBadRequest(errorText))
                .update(null, changeProfileRequest);

//        var userDao = DataBaseSteps.getUserByUsername(userRequest.getUsername());
//        DaoAndModelAssertions.assertThat(getCustomerProfileResponse, userDao).match();
    }

    public static Stream<Arguments> customerInvalidData() {
        return Stream.of(
                Arguments.of("Text 123", errorText),
                Arguments.of("Texttext", errorText),
                Arguments.of(" Texttext", errorText),
                Arguments.of("  Texttext", errorText),
                Arguments.of("Test Test Test", errorText)
        );

    }
}
