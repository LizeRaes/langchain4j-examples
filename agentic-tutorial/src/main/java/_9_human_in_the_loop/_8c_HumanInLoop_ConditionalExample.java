package _9_human_in_the_loop;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.model.chat.ChatModel;
import util.ChatModelProvider;
import util.log.CustomLogging;
import util.log.LogLevels;

import java.util.Map;

public class _8c_HumanInLoop_ConditionalExample {

    static {
        CustomLogging.setLevel(LogLevels.PRETTY, 300);  // control how much you see from the model calls
    }

    /**
     * This example demonstrates a conditional approval flow with human-in-the-loop interaction.
     * The AI drafts an email, asks the human for approval, and then either finalizes it
     * or handles the rejection based on the human's response.
     * 
     * This showcases the common pattern of AI preparation → Human approval → AI continuation.
     */

    private static final ChatModel CHAT_MODEL = ChatModelProvider.createChatModel();

    public static void main(String[] args) {
        
        System.out.println("=== CONDITIONAL APPROVAL FLOW DEMO ===");
        System.out.println("The AI will draft an email, ask for your approval, then proceed accordingly.\n");

        // 1. AI Agent that drafts emails
        EmailDraftAgent emailDraftAgent = AgenticServices
                .agentBuilder(EmailDraftAgent.class)
                .chatModel(CHAT_MODEL)
                .outputName("draftEmail")
                .build();

        // 2. Human-in-the-loop agent for approval
        HumanInputAgent humanApproval = AgenticServices
                .agentBuilder(HumanInputAgent.class)
                .chatModel(CHAT_MODEL)
                .outputName("approval")
                .build();

        // 3. Agent that finalizes approved emails
        EmailFinalizerAgent emailFinalizer = AgenticServices
                .agentBuilder(EmailFinalizerAgent.class)
                .chatModel(CHAT_MODEL)
                .outputName("finalizedEmail")
                .build();

        // 4. Agent that handles rejected emails
        EmailRejectionAgent emailRejection = AgenticServices
                .agentBuilder(EmailRejectionAgent.class)
                .chatModel(CHAT_MODEL)
                .outputName("rejectionResponse")
                .build();

        // 5. Create the complete workflow: Draft → Human Approval → Conditional Action
        UntypedAgent emailWorkflow = AgenticServices
                .sequenceBuilder()
                .subAgents(emailDraftAgent, humanApproval)
                .build();

        // 6. Create the conditional approval workflow
        UntypedAgent approvalWorkflow = AgenticServices
                .conditionalBuilder()
                .subAgents(scope -> {
                    // Check if user approved
                    String approval = (String) scope.readState("approval");
                    return approval != null && approval.toLowerCase().contains("yes");
                }, emailFinalizer) // run this if approved
                .subAgents(scope -> true, emailRejection) // fallback to rejection handler
                .build();

        // 7. Create the complete workflow
        UntypedAgent completeWorkflow = AgenticServices
                .sequenceBuilder()
                .subAgents(emailWorkflow, approvalWorkflow)
                .build();

        // 8. Run the workflow
        Map<String, Object> input = Map.of(
                "emailType", "project status update to stakeholders",
                "tone", "professional and informative"
        );
        
        System.out.println("Email Request: " + input.get("emailType"));
        System.out.println("Tone: " + input.get("tone"));
        System.out.println("\nStarting email workflow...\n");
        
        var result = completeWorkflow.invoke(input);
        
        System.out.println("\n=== EMAIL WORKFLOW COMPLETED ===");
        System.out.println("Final result: " + result);
        System.out.println("The email workflow has been completed based on your approval decision!");
    }
}
