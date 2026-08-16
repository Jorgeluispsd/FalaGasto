package budgeting.controller;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatModelController {

	private final OpenAiChatModel chatModel;
	
	public ChatModelController(OpenAiChatModel chatModel) {
		this.chatModel = chatModel;
	}
	
	@GetMapping("/chat-model")
	String chat(String prompt){
		return chatModel.call(prompt);
	}
	
}
