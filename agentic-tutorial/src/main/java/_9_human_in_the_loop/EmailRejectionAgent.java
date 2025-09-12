package _9_human_in_the_loop;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;

public interface EmailRejectionAgent {
    
    @Agent("Handles rejected emails and provides feedback")
    @UserMessage("""
        The email has been rejected. Please:
        1. Acknowledge the rejection politely
        2. Ask for specific feedback on what needs to be changed
        3. Suggest next steps for improvement
        4. Offer to redraft with different approach
        
        Be helpful and professional in your response.
        """)
    String handleRejection();
}
