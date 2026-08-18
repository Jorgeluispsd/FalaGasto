package budgeting.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AudioTranscriptionService {
	private static final Logger logger = LoggerFactory.getLogger(AudioTranscriptionService.class);
	private final TranscriptionModel transcriptionModel;
	
	public AudioTranscriptionService(TranscriptionModel transcriptionModel) {
		this.transcriptionModel = transcriptionModel;
	}
	
	public String transcribe(MultipartFile file) {
		logger.info("Iniciando transcrição de áudio");
		
		if(file.isEmpty()){
			throw new IllegalArgumentException("Arquivo de áudio vazio");
		}
		
		try{
			var resource = file.getResource();
			logger.info("Enviando arquivo para transcrição via OpenAI Whisper");
			var transcription = transcriptionModel.transcribe(resource);
			logger.info("Transcrição concluida: {}", transcription);
			return transcription;
		} catch (Exception e) {
			logger.error("Erro ao processar transcrição: {}", e.getMessage(), e);
			throw new RuntimeException("Erro ao processar transcrição", e);
		}
	}
}
