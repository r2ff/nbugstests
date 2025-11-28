package nbugs.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeCustomerProfileResponse extends BaseModel {
    private String message;
    private CreateUserResponse customer;
}
