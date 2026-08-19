package budgeting.controller;

import budgeting.dto.request.TransactionRequestDTO;
import budgeting.dto.response.TransactionResponseDTO;
import budgeting.enums.Category;

import budgeting.service.AudioTranscriptionService;
import budgeting.service.TextToSpeechService;
import budgeting.service.TransactionExtractionService;
import budgeting.service.TransactionService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	private final TransactionService transactionService;
	private final AudioTranscriptionService transcriptionService;
	private final TransactionExtractionService extractionService;
	private final TextToSpeechService textToSpeechService;
	
	public TransactionController(TransactionService transactionService,
								 AudioTranscriptionService transcriptionService,
								 TransactionExtractionService extractionService,
								 TextToSpeechService textToSpeechService) {
		this.transactionService = transactionService;
		this.transcriptionService = transcriptionService;
		this.extractionService = extractionService;
		this.textToSpeechService = textToSpeechService;
	}
	
	@PostMapping
	public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody TransactionRequestDTO request) {
		TransactionResponseDTO response = transactionService.createTransaction(request);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping(value= "/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<byte[]> createFromAudio(@RequestParam("file") MultipartFile file) {
		
		String transcribedText = transcriptionService.transcribe(file);
		
		TransactionRequestDTO dto = extractionService.extractTransaction(transcribedText);

		if (dto == null) {
			String noTransactionMessage = "Não foi identificado nenhum gasto no áudio. " +
					"O áudio continha: " + transcribedText;
			byte[] audio = textToSpeechService.synthesizeMessage(noTransactionMessage);
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType("audio/mpeg"))
					.body(audio);
		}
		
		TransactionResponseDTO saved = transactionService.createTransaction(dto);
		
		String confirmationMessage = String.format("Gasto de R$ %.2f em %s salvo com sucesso",
				saved.getAmount(), saved.getDescription());
		byte[] audioConfirmation = textToSpeechService.synthesizeConfirmation(confirmationMessage);
		
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("audio/mpeg"))
				.body(audioConfirmation);
	}
	
	@GetMapping
	public ResponseEntity<List<TransactionResponseDTO>> findAll() {
		return ResponseEntity.ok(transactionService.findAll());
	}
	
	@GetMapping(value = "/audio", produces = "audio/mpeg")
	public ResponseEntity<byte[]> findAllWithAudio() {
		List<TransactionResponseDTO> transactions = transactionService.findAll();
		String message = buildSpokenMessage(transactions);
		byte[] audio = textToSpeechService.synthesizeMessage(message);
		return ResponseEntity.ok()
				.body(audio);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<TransactionResponseDTO> findById(@PathVariable UUID id) {
		return ResponseEntity.ok(transactionService.findById(id));
	}
	
	@GetMapping(value = "/{id}/audio", produces = "audio/mpeg")
	public ResponseEntity<byte[]> findByIdWithAudio(@PathVariable UUID id) {
		TransactionResponseDTO transaction = transactionService.findById(id);
		String message = String.format("Gasto de R$ %.2f em %s, categoria %s",
				transaction.getAmount(), transaction.getDescription(), transaction.getCategory());
		byte[] audio = textToSpeechService.synthesizeMessage(message);
		return ResponseEntity.ok().body(audio);
	}
	
	@GetMapping("/category/{category}")
	public ResponseEntity<List<TransactionResponseDTO>> findByCategory(@PathVariable Category category) {
		return ResponseEntity.ok(transactionService.findAllByCategory(category));
	}
	
	@GetMapping(value = "/category/{category}/audio", produces = "audio/mpeg")
	public ResponseEntity<byte[]> findByCategoryWithAudio(@PathVariable Category category) {
		List<TransactionResponseDTO> transactions = transactionService.findAllByCategory(category);
		String message = buildSpokenMessage(transactions);
		byte[] audio = textToSpeechService.synthesizeMessage(message);
		return ResponseEntity.ok().body(audio);
	}
	
	private String buildSpokenMessage(List<TransactionResponseDTO> transactions) {
		if (transactions.isEmpty()) {
			return "Você não tem transações registradas.";
		}
		
		StringBuilder sb = new StringBuilder("Você tem ");
		sb.append(transactions.size()).append("transações. ");
		
		for (TransactionResponseDTO t: transactions) {
			sb.append("Gasto de R$ ").append(t.getAmount())
					.append(" em ").append(t.getDescription())
					.append(", categoria ").append(t.getCategory()).append(". ");
		}
		return sb.toString();
	}
}
