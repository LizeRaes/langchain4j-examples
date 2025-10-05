package devoxx25;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class DynamicToolsDemo {

    public static void main(String[] args) {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_O_MINI)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .toolProvider(BookingToolProvider.create()) // Only exposes tools when "booking" is mentioned
                .build();

        String question1 = "What's the weather like today?";
        System.out.println("1. Question: " + question1);
        Result<String> result1 = assistant.chat(question1);
        System.out.println("AI Answer: " + result1.content());
        System.out.println("Tools executed: " + result1.toolExecutions().size() + "\n");

        System.out.println("2. Booking question:");
        Result<String> result2 = assistant.chat("Can you show me booking 12345?");
        System.out.println("AI Answer: " + result2.content());
        result2.toolExecutions().forEach(exec ->
                System.out.println("Executed tool: " + exec.request().name() + " → " + exec.result())
        );
        System.out.println();

        System.out.println("3. Cancel booking:");
        Result<String> result3 = assistant.chat("Cancel booking 12345");
        System.out.println("AI Answer: " + result3.content());
        result3.toolExecutions().forEach(exec ->
                System.out.println("Executed tool: " + exec.request().name() + " → " + exec.result())
        );
    }

}

