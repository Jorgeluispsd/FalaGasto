package budgeting.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class TranscriptionController {

	private static final Logger logger = LoggerFactory.getLogger(TranscriptionController.class);
	private final TranscriptionModel transcriptionModel;
	
	public TranscriptionController(TranscriptionModel transcriptionModel) {
		
		this.transcriptionModel = transcriptionModel;
	}
	
	@PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	String transcribe(@RequestParam("file") MultipartFile file) {
		logger.info("Iniciando transcrição de áudio");
		logger.debug("Nome do arquivo: {}", file.getOriginalFilename());
		logger.debug("Tamanho do arquivo: {} bytes", file.getSize());
		logger.debug("Content-Type: {}", file.getContentType());
		if (file.isEmpty()) {
			logger.error("Arquivo vazio recebido");
			throw new IllegalArgumentException("Arquivo vazio");
		}
		
		try{
			var resource = file.getResource();
			logger.debug("Resource criado: {}", resource);
			
			logger.info("Enviando arquivo para transcrição via OpenAI Whisper");
			var transcription = transcriptionModel.transcribe(resource);
			
			logger.info("Transcrição concluída com sucesso");
			logger.debug("Resultado da transcrição: {}", transcription);
			
			return transcription;
		} catch (Exception e) {
			logger.error("Erro ao processar transcrição: {}", e.getMessage(), e);
			throw new RuntimeException("Erro ao processar transcrição: " + e.getMessage(), e);
		}
	}
}
