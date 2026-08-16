package budgeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@TestPropertySource(properties = "spring.ai.openai.api-key=${OPENAI_API_KEY}")
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiTranscriptionModelIT {
	
	@Autowired
	OpenAiAudioTranscriptionModel transcriptionModel;
	
	@CsvSource({
			"Carrefour_Audio.m4a, 10 reais",
			"CompraOnline_Audio.m4a, 300 reais",
			"Lanchonete_Audio.m4a, 9 reais",
			"Onibus_Audio.m4a, 10 reais",
			"Pizzaria_Audio.m4a, 60 reais",
	})
	
	@ParameterizedTest
	void should_containsExpectedKeywords_when_audioFilesAreProcessed(String fileName, String expectedKeyword) {
		var recording = new FileSystemResource("audio-files/" + fileName);
		
		var response = transcriptionModel.transcribe(recording);
		
		var value = expectedKeyword.split(" ")[0];
		
		var variations = new String[]{
				value + " reais",
				"R$" + value + ".00",
				"R$" + value,
				value + ",00 reais",
				"R$ " + value,
				value + "R$"
		};
		
		assertThat(response).containsAnyOf(variations);
		System.out.println(response);
	}
}
