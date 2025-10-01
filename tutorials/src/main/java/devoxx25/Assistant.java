package devoxx25;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;

public interface Assistant {
    @Tool("answers questions regarding bookings")
    @SystemMessage("You are a helpful assistant for a travel agency. You can answer questions regarding bookings.")
    Result<String> chat(String question);
}
