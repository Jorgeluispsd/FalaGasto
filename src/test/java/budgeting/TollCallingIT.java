package budgeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@TestPropertySource(properties = "spring.ai.openai.api-key=${OPENAI_API_KEY}")
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
public class TollCallingIT {
	
	@Autowired
	OpenAiChatModel chatModel;
	
	static class MathTolls{
		@Tool(description = "soma de dois números inteiros, a e b")
		public int sum(int a, int b) {
			return a + b;
		}
		
		@Tool(description = "subtrai de dois números inteiros, a e b")
		public int sub(int a, int b) {
			return a - b;
		}
	}
	
	@Test
	void should_executeSum_when_prompted(){
		var chatClient = ChatClient.builder(chatModel)
				.defaultSystem("Você é um matemático")
				.defaultTools(new MathTolls())
				.build();
		
		var response = chatClient.prompt("Some 30 mais 20. Depois subtraia 30 do resultado anterior. E por fim exiba apenas o resultado final sem explicações")
				.call().content();
		
		assertThat(response).contains("20");
		System.out.println(response);
	}
}
