package devoxx25;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderResult;

import java.util.Map;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class DynamicToolsDemo {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName("gpt-4o-mini") // or GPT_4_O_MINI if imported
                .build();

        // Executor for fetching booking details
        ToolExecutor getBookingDetailsExecutor = (toolExecutionRequest, memoryId) -> {
            String json = toolExecutionRequest.arguments();
            Map<String, Object> map = null;
            try {
                map = MAPPER.readValue(json, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            String bookingNumber = String.valueOf(map.get("bookingNumber"));
            Booking booking = getBooking(bookingNumber);
            return booking.toString();
        };

        ToolExecutor cancelBookingExecutor = (toolExecutionRequest, memoryId) -> {
            String json = toolExecutionRequest.arguments();
            Map<String, Object> map;
            try {
                map = MAPPER.readValue(json, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            String bookingNumber = String.valueOf(map.get("bookingNumber"));
            return cancelBooking(bookingNumber);
        };


        // Dynamic tool provider
        ToolProvider toolProvider = (toolProviderRequest) -> {
            if (toolProviderRequest.userMessage().singleText().toLowerCase().contains("booking")) {
                ToolSpecification getBookingDetailsTool = ToolSpecification.builder()
                        .name("get_booking_details")
                        .description("Returns booking details")
                        .parameters(JsonObjectSchema.builder()
                                .addStringProperty("bookingNumber")
                                .build())
                        .build();

                ToolSpecification cancelBookingTool = ToolSpecification.builder()
                        .name("cancel_booking")
                        .description("Cancels a booking by booking number")
                        .parameters(JsonObjectSchema.builder()
                                .addStringProperty("bookingNumber")
                                .build())
                        .build();

                return ToolProviderResult.builder()
                        .add(getBookingDetailsTool, getBookingDetailsExecutor)
                        .add(cancelBookingTool, cancelBookingExecutor)
                        .build();
            } else {
                return null;
            }
        };

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .toolProvider(toolProvider)
                .build();

        // Example usage: ask for details
        Result<String> result1 = assistant.chat("Can you show me booking 12345?");
        System.out.println("AI Answer: " + result1.content());
        result1.toolExecutions().forEach(exec ->
                System.out.println("Executed tool: " + exec.request().name() + " → " + exec.result())
        );

        // Example usage: cancel booking
        Result<String> result2 = assistant.chat("Cancel booking 12345");
        System.out.println("AI Answer: " + result2.content());
        result2.toolExecutions().forEach(exec ->
                System.out.println("Executed tool: " + exec.request().name() + " → " + exec.result())
        );
    }

    // Mock booking fetcher
    private static Booking getBooking(String bookingNumber) {
        System.out.println("---Tool called: getBooking with " + bookingNumber);
        return new Booking(bookingNumber, "John Doe", "2025-10-15");
    }

    // Mock cancel booking
    private static String cancelBooking(String bookingNumber) {
        System.out.println("---Tool called: cancelBooking with " + bookingNumber);
        return "Booking " + bookingNumber + " has been successfully canceled.";
    }

    // Simple POJO for example
    static class Booking {
        private final String number;
        private final String customer;
        private final String date;

        Booking(String number, String customer, String date) {
            this.number = number;
            this.customer = customer;
            this.date = date;
        }

        @Override
        public String toString() {
            return "Booking " + number + " for " + customer + " on " + date;
        }
    }
}

