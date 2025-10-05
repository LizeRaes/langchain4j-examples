package devoxx25;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderResult;

import java.util.Map;

/**
 * Simple tool provider that conditionally exposes booking tools.
 * 
 * Key concept: Tools are only available when user message contains "booking".
 */
public class BookingToolProvider {

    public static ToolProvider create() {
        return (toolProviderRequest) -> {
            // Only expose booking tools when user mentions "booking"
            if (toolProviderRequest.userMessage().singleText().toLowerCase().contains("booking")) {
                
                // Create simple specifications
                ToolSpecification getBookingDetailsTool = ToolSpecification.builder()
                        .name("getBookingDetails")
                        .description("Returns booking details for a given booking number")
                        .parameters(JsonObjectSchema.builder()
                                .addStringProperty("bookingNumber")
                                .build())
                        .build();

                ToolSpecification cancelBookingTool = ToolSpecification.builder()
                        .name("cancelBooking")
                        .description("Cancels a booking by booking number")
                        .parameters(JsonObjectSchema.builder()
                                .addStringProperty("bookingNumber")
                                .build())
                        .build();

                // Create simple executors that extract parameters from the request
                BookingTools bookingTools = new BookingTools();
                ToolExecutor getBookingDetailsExecutor = (request, memoryId) -> {
                    // Extract bookingNumber from the tool execution request
                    String bookingNumber = extractBookingNumber(request.arguments());
                    return bookingTools.getBookingDetails(bookingNumber);
                };
                ToolExecutor cancelBookingExecutor = (request, memoryId) -> {
                    // Extract bookingNumber from the tool execution request
                    String bookingNumber = extractBookingNumber(request.arguments());
                    return bookingTools.cancelBooking(bookingNumber);
                };

                return ToolProviderResult.builder()
                        .add(getBookingDetailsTool, getBookingDetailsExecutor)
                        .add(cancelBookingTool, cancelBookingExecutor)
                        .build();
            }
            return null; // No tools for non-booking messages
        };
    }

    private static String extractBookingNumber(String json) {
        try {
            Map<String, Object> map = new ObjectMapper().readValue(json, new TypeReference<>() {});
            return String.valueOf(map.get("bookingNumber"));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse booking number from: " + json, e);
        }
    }
}
