package devoxx25;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class ReturnImmediateDemo {

    public static void main(String[] args) {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_4_O_MINI)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .toolProvider(BookingToolProvider.create()) // Add booking tools that are conditionally exposed
                .build();

        ToolCallingAiService aiService = AiServices.builder(ToolCallingAiService.class)
                .tools(new Tools(), assistant)
                .chatModel(model)
                .maxSequentialToolsInvocations(3)
                .build();

        Result<String> aiServiceMathResult = aiService.callAiService("Add 284 and 42");

        int mathToolResult = (int) aiServiceMathResult.toolExecutions().get(0).resultObject();
        System.out.println("Math Tool result: " + mathToolResult);

//        Result<String> aiServiceBookingResult = aiService.callAiService("Cancel booking 12345");
//        String answer = (String) aiServiceBookingResult.content();
//        System.out.println("Booking Service answer: " + answer);
//        // should be dummy: "Booking " + bookingNumber + " has been successfully canceled."

    }
}
