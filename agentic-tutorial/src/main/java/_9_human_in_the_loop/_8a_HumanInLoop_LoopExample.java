package _9_human_in_the_loop;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.agentic.workflow.HumanInTheLoop;
import util.ChatModelProvider;
import util.log.CustomLogging;
import util.log.LogLevels;

import java.util.Map;

public class _8a_HumanInLoop_LoopExample {

    // TODO Mario in theory the human in the loop can be async in this loop, but how does the other agent know it has to wait for an answer then?
    // TODO try the code out

    static {
        CustomLogging.setLevel(LogLevels.PRETTY, 300);  // control how much you see from the model calls
    }

    /**
     * This example demonstrates a back-and-forth loop with human-in-the-loop interaction.
     * The AI agent proposes meeting times that fit his calendar,
     * and the human confirms or rejects them.
     * The loop continues until the human confirms availability (exit condition met).
     */

    private static final ChatModel CHAT_MODEL = ChatModelProvider.createChatModel();

    public static void main(String[] args) {

        // 1. Define sub-agents
        MeetingProposer proposer = AgenticServices
                .agentBuilder(MeetingProposer.class)
                .chatModel(CHAT_MODEL)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(15)) // so the agent remembers what he proposed already
                .outputName("proposal")
                .build();

        // 2. Define Human-in-the-loop agent
        HumanInTheLoop humanInTheLoop = AgenticServices
                .humanInTheLoopBuilder()
                .description("agent that asks input from the user")
                .outputName("reply")
                .inputName("proposal") // the default would be 'request'
                .requestWriter(request -> {
                    System.out.println(request);
                    System.out.print("> ");
                })
                .responseReader(() -> System.console().readLine())
                .build();

        // 3. construct the loop
        UntypedAgent schedulingLoop = AgenticServices
                .loopBuilder()
                .subAgents(proposer, humanInTheLoop)
                .outputName("proposal")
                .exitCondition(scope -> {
                    String response = (String) scope.readState("reply");
                    return response != null && response.toLowerCase().contains("yes"); // this is a very naive and risky implementation for demo purposes
                // TODO think of sth better here
                })
                .maxIterations(5)
                .build();
        // TODO Mario another place where a fail should trigger sth different but it's hard to differentiate?

        // 4. Run the scheduling loop
        Map<String, Object> input = Map.of("meetingTopic", "on-site visit",
                "memoryId", "user-1234"); // if we don't put a memoryId, the proposer agent will not remember what he proposed already

        var result = schedulingLoop.invoke(input);
        
        System.out.println("\n=== RETAINED MEETING SLOT ===");
        System.out.println(result);
    }
}
