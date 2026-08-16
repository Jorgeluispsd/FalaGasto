package budgeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@TestPropertySource(properties = "spring.ai.openai.api-key=${OPENAI_API_KEY}")
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiChatModelIT {
	
	@Autowired
	ChatModel chatModel;
	
	@Test
	void should_receveidResponse_when_chatModelIsCalled(){
		var response = chatModel.call("Gere um registro de budgeting, com descrição de gasto, valor em reais e local");
		
		assertThat(response).isNotNull();
		System.out.println(response);
	}
}
