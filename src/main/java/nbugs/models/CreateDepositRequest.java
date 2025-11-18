package nbugs.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomUtils;

import java.util.Random;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateDepositRequest extends BaseModel{
    private Long id;
    @Builder.Default
    private Double balance = RandomUtils.nextDouble(1000.0, 5000.0);
}
