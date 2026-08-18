package budgeting.model;

import budgeting.enums.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false)
	private String description;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal amount;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Category category;
}
