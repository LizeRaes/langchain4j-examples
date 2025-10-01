package devoxx25;

import dev.langchain4j.service.Result;

public interface ToolCallingAiService {
    Result<String> callAiService(String input);
}
