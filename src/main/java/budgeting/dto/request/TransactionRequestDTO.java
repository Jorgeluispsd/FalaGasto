package budgeting.dto.request;

import budgeting.enums.Category;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRequestDTO {
	private String description;
	private BigDecimal amount;
	private Category category;
}
