package budgeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@TestPropertySource(properties = "spring.ai.openai.api-key=${OPENAI_API_KEY}")
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class OpenAiChatClientIT {
	
	@Autowired
	OpenAiChatModel chatModel;
	
	@Test
	void should_executeSum_when_prompted(){
		var chatClient = ChatClient.builder(chatModel)
				.defaultSystem("Você é um matemático").build();
		
		var response = chatClient.prompt("Some 30 mais 20. Depois subtraia 30 do resultado anterior. E por fim exiba apenas o resultado final sem explicações")
				.call().content();
		
		assertThat(response).contains("20");
		System.out.println(response);
	}
}
