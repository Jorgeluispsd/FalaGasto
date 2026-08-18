package budgeting.service;

import budgeting.dto.request.TransactionRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionExtractionService {
	
	private final ChatClient chatClient;
	private final ObjectMapper objectMapper;
	
	public TransactionExtractionService(ChatClient chatClient) {
		this.chatClient = chatClient;
		this.objectMapper = new ObjectMapper();
	}
	
	public TransactionRequestDTO extractTransaction(String transcribedText) {
		String propmt = String.format("""
				            Analise o seguinte texto e determine se contém informações sobre um gasto financeiro.
				            Texto: "%s"
				
				            Se o texto NÃO contiver informações de gasto (valor, descrição), retorne APENAS:
				            {"hasTransaction": false}
				
				            Se o texto contiver informações de gasto, retorne APENAS um JSON válido:
				            {
				                "hasTransaction": true,
				                "description": "descrição do gasto",
				                "amount": 70.85,
				                "category": "FOOD"
				            }
				
				            Categorias disponíveis: GROCERIES, PHARMA, TRANSPORT, ENTERTAINMENT, FOOD, SHOPPING, OTHER
				            O valor deve ser numérico com ponto decimal (ex: 70.85).
				            Não inclua nenhum texto além do JSON.
				""", transcribedText);
		
		String response = chatClient.prompt()
				.user(propmt)
				.call().content();
		
		return parseJsonTODTO(response);
	}
	
	private TransactionRequestDTO parseJsonTODTO(String json) {
		try {
			String cleanedJson = json.trim();
			if (cleanedJson.startsWith("```json")) {
				cleanedJson = cleanedJson.substring(7);
			}
			if (cleanedJson.endsWith("```json")) {
				cleanedJson = cleanedJson.substring(0, cleanedJson.length() - 3);
			}
			cleanedJson = cleanedJson.trim();
			
			var jsonNode = objectMapper.readTree(cleanedJson);
			
			boolean hasTransaction = jsonNode.get("hasTransaction").asBoolean();
			
			if (!hasTransaction){
				return null;
			}
			
			TransactionRequestDTO dto = new TransactionRequestDTO();
			dto.setDescription(jsonNode.get("description").asText());
			dto.setAmount(BigDecimal.valueOf(jsonNode.get("amount").asDouble()));
			dto.setCategory(budgeting.enums.Category.valueOf(jsonNode.get("category").asText()));
			
			return dto;
		} catch (Exception e) {
			throw new RuntimeException("Erro ao parsear Json da resposta da IA" + e.getMessage(), e);
		}
	}
}
