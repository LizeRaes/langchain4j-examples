package _9_human_in_the_loop;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.supervisor.SupervisorAgent;
import dev.langchain4j.agentic.supervisor.SupervisorContextStrategy;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.model.chat.ChatModel;
import util.ChatModelProvider;
import util.log.CustomLogging;
import util.log.LogLevels;

public class _8b_HumanInLoop_SupervisorExample {

    // TODO got here
    // TODO note supervisor will enforce/encapsulate with blocking for his planning purposes
    static {
        CustomLogging.setLevel(LogLevels.PRETTY, 300);  // control how much you see from the model calls
    }

    /**
     * This example demonstrates how human-in-the-loop agents can be integrated
     * into supervisor orchestration. The supervisor dynamically decides when
     * to ask the human for input, when to propose times, and when to confirm scheduling.
     * 
     * This showcases the pattern of dynamic reasoning where the supervisor
     * can ask for more information multiple times if needed.
     */

    private static final ChatModel CHAT_MODEL = ChatModelProvider.createChatModel();

    public static void main(String[] args) {
        
        System.out.println("=== SUPERVISOR-DRIVEN MEETING PLANNER DEMO ===");
        System.out.println("The supervisor will orchestrate the meeting planning process,\n" +
                          "deciding when to ask you for input and when to proceed automatically.\n");

        // 1. Create the meeting proposer agent
        MeetingProposer proposer = AgenticServices
                .agentBuilder(MeetingProposer.class)
                .chatModel(CHAT_MODEL)
                .outputName("proposal")
                .build();

        // 2. Create human-in-the-loop agent for user confirmation
        HumanInputAgent humanInLoop = AgenticServices
                .agentBuilder(HumanInputAgent.class)
                .chatModel(CHAT_MODEL)
                .outputName("response")
                .build();

        // 3. Create the meeting organizer agent
        MeetingOrganizer organizer = AgenticServices
                .agentBuilder(MeetingOrganizer.class)
                .chatModel(CHAT_MODEL)
                .outputName("confirmation")
                .build();

        // 4. Create the supervisor that orchestrates all agents
        SupervisorAgent supervisor = AgenticServices
                .supervisorBuilder()
                .chatModel(CHAT_MODEL)
                .subAgents(proposer, humanInLoop, organizer)
                .contextGenerationStrategy(SupervisorContextStrategy.CHAT_MEMORY_AND_SUMMARIZATION)
                .responseStrategy(SupervisorResponseStrategy.SUMMARY)
                .supervisorContext("""
                    You are a meeting planning supervisor. Your goal is to schedule a meeting by:
                    1. Proposing appropriate meeting times using the proposer agent
                    2. Asking the user for confirmation using the human-in-the-loop agent
                    3. If the user rejects, propose alternative times
                    4. Once confirmed, use the organizer agent to finalize the meeting
                    
                    Always be polite and professional. If the user rejects a time, propose alternatives.
                    Only finalize the meeting after getting explicit confirmation from the user.
                    """)
                .build();

        // 5. Run the supervisor with a meeting request
        String meetingRequest = "Please schedule a kickoff meeting for our new project. " +
                               "The meeting should be about 1 hour long and include the core team.";
        
        System.out.println("Meeting Request: " + meetingRequest);
        System.out.println("\nStarting supervisor orchestration...\n");
        
        var result = supervisor.invoke(meetingRequest);
        
        System.out.println("\n=== SUPERVISOR ORCHESTRATION COMPLETED ===");
        System.out.println("Final result: " + result);
        System.out.println("The supervisor has completed the meeting planning process!");
    }
}
