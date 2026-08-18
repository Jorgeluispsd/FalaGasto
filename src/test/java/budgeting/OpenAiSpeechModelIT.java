package budgeting;

import com.openai.models.audio.speech.SpeechModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@TestPropertySource(properties = "spring.ai.openai.api-key=${OPENAI_API_KEY}")
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiSpeechModelIT {
	
	@Autowired
	TextToSpeechModel textToSpeechModel;
	
	@Test
	public void should_produceAudio_when_textIsProvided() throws IOException {
		var response = textToSpeechModel.call("O valor total do serviço ficou 80 reais. Posso confirmar o pagamento?");
		
		assertThat(response).hasSizeGreaterThan(1024);
		var tempFile = Files.createTempFile("AUDIO_", ".mp3");
		Files.write(tempFile, response);
		System.out.println(tempFile.toAbsolutePath());
		
	}
}
