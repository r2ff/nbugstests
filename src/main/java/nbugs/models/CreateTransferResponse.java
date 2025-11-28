package nbugs.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransferResponse extends BaseModel {
    private String message;
    private Double amount;
    private Long receiverAccountId;
    private Long senderAccountId;
}
