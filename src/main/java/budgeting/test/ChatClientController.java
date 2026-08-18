package budgeting.test;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class ChatClientController {

	private final ChatClient chatClient;
	
	public ChatClientController(ChatClient chatClient) {
		
		this.chatClient = chatClient;
	}
	
	@GetMapping("/chat")
	String chat(String prompt){
		return chatClient.prompt().user(prompt).call().content();
	}
	
}
