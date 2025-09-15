package _9_human_in_the_loop;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface MeetingProposer {
    
    @Agent("Proposes a meeting time")
    @SystemMessage("""
        You assist CompanyA in trying to schedule a new meeting time for a 3h meeting slot on topic {{meetingTopic}}.
        Write your proposal as a single sentence, like:
        "Would you be available on Monday at 10am?"
        Answer user questions if any.
        The people at CompanyA are available at the following times: tomorrow all morning, the day after all afternoon, 
        next week same days but then available all day.
        Today is Monday 12 September.
        """)
    // TODO this could be RAG by checking a calendar
    @UserMessage("""
        Previous candidate answer was: {{candidateAnswer}}
        """)
    // TODO put date instead of hardcoded
    String propose(@MemoryId String memoryId, @V("meetingTopic") String meetingTopic, @V("candidateAnswer") String candidateAnswer);
}
