package nbugs.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nbugs.generators.GeneratingRule;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeCustomerProfileRequest extends BaseModel {
    @GeneratingRule(regex = "^[a-zA-Z]{6}+ [a-zA-Z]{6}$")
    private String name;
}
