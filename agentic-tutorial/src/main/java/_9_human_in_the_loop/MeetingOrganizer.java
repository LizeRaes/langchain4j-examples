package _9_human_in_the_loop;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;

public interface MeetingOrganizer {
    
    @Agent("Organizes and confirms the meeting")
    @UserMessage("""
        Confirm the meeting based on the agreed time and prepare a summary.
        Include the meeting topic, confirmed time, and next steps.
        """)
    String organize();
}
