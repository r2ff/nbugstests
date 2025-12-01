package nbugs.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDao {
    private Long id;
    private String type;
    private Long accountId;
    private Long relatedAccountId;
    private Double amount;
}