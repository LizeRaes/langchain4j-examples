package _9_human_in_the_loop;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface EmailDraftAgent {
    
    @Agent("Drafts professional emails")
    @UserMessage("""
        Draft a professional email for {{emailType}}.
        The email should be {{tone}} and include all necessary details.
        Make it ready to send but mark it as DRAFT for human review.
        """)
    String draftEmail(@V("emailType") String emailType, @V("tone") String tone);
}
