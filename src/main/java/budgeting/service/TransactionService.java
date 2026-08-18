package budgeting.service;

import budgeting.dto.request.TransactionRequestDTO;
import budgeting.dto.response.TransactionResponseDTO;
import budgeting.enums.Category;
import budgeting.model.Transaction;
import budgeting.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionService {
	
	private final TransactionRepository repository;
	
	public TransactionResponseDTO createTransaction(TransactionRequestDTO dto) {
		Transaction transaction = new Transaction();
		transaction.setDescription(dto.getDescription());
		transaction.setAmount(dto.getAmount());
		transaction.setCategory(dto.getCategory());
		
		Transaction saved =  repository.save(transaction);
		return toResponseDTO(saved);
	}
	
	public List<TransactionResponseDTO> findAllByCategory(Category category) {
		return repository.findByCategory(category).stream()
			.map(this::toResponseDTO)
			.collect(Collectors.toList());
	}
	
	public List<TransactionResponseDTO> findAll() {
		return repository.findAll().stream()
			.map(this::toResponseDTO)
			.collect(Collectors.toList());
	}
	
	public TransactionResponseDTO findById(UUID id) {
		Transaction transaction = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Transaction not found"));
		return toResponseDTO(transaction);
	}
	
	private TransactionResponseDTO toResponseDTO(Transaction transaction) {
		TransactionResponseDTO dto = new TransactionResponseDTO();
		dto.setId(transaction.getId());
		dto.setDescription(transaction.getDescription());
		dto.setAmount(transaction.getAmount());
		dto.setCategory(transaction.getCategory());
		return dto;
	}
}
