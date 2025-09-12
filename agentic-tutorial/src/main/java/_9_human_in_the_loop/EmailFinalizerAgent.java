package _9_human_in_the_loop;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;

public interface EmailFinalizerAgent {
    
    @Agent("Finalizes and sends approved emails")
    @UserMessage("""
        The email has been approved. Finalize it by:
        1. Removing the DRAFT marking
        2. Adding final formatting
        3. Preparing it for sending
        4. Confirming the recipient and subject
        
        Return a summary of the finalized email.
        """)
    String finalizeEmail();
}
