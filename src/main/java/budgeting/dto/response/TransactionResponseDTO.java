package budgeting.dto.response;

import budgeting.enums.Category;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class TransactionResponseDTO {
	private UUID id;
	private String description;
	private BigDecimal amount;
	private Category category;
}
