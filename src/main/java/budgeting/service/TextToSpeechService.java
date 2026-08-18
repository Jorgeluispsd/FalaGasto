package budgeting.service;


import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.stereotype.Service;

@Service
public class TextToSpeechService {
	
	private final TextToSpeechModel textToSpeechModel;
	
	public TextToSpeechService(TextToSpeechModel textToSpeechModel) {
		this.textToSpeechModel = textToSpeechModel;
	}
	
	public byte[] synthesizeConfirmation(String message) {
		String text = "Gasto de " + message + " salvo com sucesso";
		return textToSpeechModel.call(text);
	}
	
	public byte[] synthesizeMessage(String message){
		return  textToSpeechModel.call(message);
	}
}
