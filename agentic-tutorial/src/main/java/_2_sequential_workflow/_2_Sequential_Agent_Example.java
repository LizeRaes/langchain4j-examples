package _2_sequential_workflow;

import _1_basic_agent.CvGenerator;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.model.chat.ChatModel;
import util.ChatModelProvider;
import util.StringLoader;
import util.log.CustomLogging;
import util.log.LogLevels;

import java.io.IOException;
import java.util.Map;

public class _2_Sequential_Agent_Example {

    static {
        CustomLogging.setLevel(LogLevels.PRETTY, 300);  // control how much you see from the model calls
    }

    /**
     * This example demonstrates how to implement two agents:
     * - CvGenerator (takes in a life story and generates a complete master CV)
     * - CvTailor (takes in the master CV and tailors it to specific instructions (job description, feedback, ...)
     * Then we will call them one after in a fixed workflow
     * using the sequenceBuilder, and demonstrate how to pass a parameter between them.
     * When combining multiple agents, all intermediary parameters and the call chain are
     * stored in the AgenticScope, which is accessible for advanced use cases.
     */

    // 1. Define the model that will power the agents
    private static final ChatModel CHAT_MODEL = ChatModelProvider.createChatModel();

    public static void main(String[] args) throws IOException {

        // 2. Define the two sub-agents in this package:
        //      - CvGenerator.java
        //      - CvTailor.java

        // 3. Create both agents using AgenticServices
        CvGenerator cvGenerator = AgenticServices
                .agentBuilder(CvGenerator.class)
                .chatModel(CHAT_MODEL)
                .outputName("masterCv") // if you want to pass this variable from agent 1 to agent 2,
                // then make sure the output name here matches the input variable name
                // specified in the second agent interface agent_interfaces/CvTailor.java
                .build();
        CvTailor cvTailor = AgenticServices
                .agentBuilder(CvTailor.class)
                .chatModel(CHAT_MODEL) // note that it is also possible to use a different model for a different agent
                .outputName("tailoredCv") // we need to define the name of the output object
                // if we would put "masterCv" here, the original master CV would be overwritten
                // by the second agent. In this case we don't want this, but it's a useful feature.
                .build();
        // TODO consider cases where no variable is passed but tools execute other things

        ////////////////// UNTYPED EXAMPLE //////////////////////

        // 4. Build the sequence
        UntypedAgent tailoredCvGenerator = AgenticServices // use UntypedAgent unless you define the resulting composed agent, see below
                .sequenceBuilder()
                .subAgents(cvGenerator, cvTailor) // this can be as many as you want, order matters
                .outputName("tailoredCv") // this is the final output of the composed agent
                // note that you can use as output any field that is part of the AgenticScope
                // for example you could output 'masterCv' instead of tailoredCv (even if in this case that makes no sense)
                .build();

        // 4. Load the arguments from text files in resources/documents/
        // - user_life_story.txt
        // - job_description_backend.txt
        String lifeStory = StringLoader.loadFromResource("/documents/user_life_story.txt");
        String instructions = "Adapt the CV to the job description below." + StringLoader.loadFromResource("/documents/job_description_backend.txt");

        // 5. Because we use an untyped agent, we need to pass a map of arguments
        Map<String, Object> arguments = Map.of(
                "lifeStory", lifeStory, // matches the variable name in agent_interfaces/CvGenerator.java
                "instructions", instructions // matches the variable name in agent_interfaces/CvTailor.java
        );

        // 5. Call the composed agent to generate the tailored CV
        String tailoredCv = (String) tailoredCvGenerator.invoke(arguments);

        // 6. and print the generated CV
        System.out.println("=== TAILORED CV UNTYPED ===");
        System.out.println((String) tailoredCv); // you can observe that the CV looks very different
        // when you'd use job_description_fullstack.txt as input

        ////////////////// TYPED EXAMPLE WITH MULTIPLE OUTPUTS AND INVOCATION TRACE //////////////////////

        // Note that the untyped composed agent uses the generic 'invoke' method and
        // both input and output include Objects to support generic use cases.
        // If you know the input and output types, you can define a typed composed agent interface.
        // Here is the same example, but now we use typed agent agent_interfaces/SequenceCvGenerator.java

        // Another thing we'll illustrate here is how to output multiple variables from the AgenticScope
        // including input variables, intermediary variables and output variables.
        // We also show how to retrieve the AgenticScope after invocation, typically for testing purposes

        SequenceCvGenerator sequenceCvGenerator = AgenticServices
                .sequenceBuilder(SequenceCvGenerator.class) // here we specify the typed interface
                .subAgents(cvGenerator, cvTailor)
                .outputName("bothCvsAndLifeStory")
                .output(agenticScope -> {
                    Map<String, String> bothCvsAndLifeStory = Map.of(
                            "lifeStory", agenticScope.readState("lifeStory", ""),
                            "masterCv", agenticScope.readState("masterCv", ""),
                            "tailoredCv", agenticScope.readState("tailoredCv", "")
                    );
                    return bothCvsAndLifeStory;
                    })
                .build();

        // Call the typed composed agent
        ResultWithAgenticScope<Map<String,String>> bothCvsAndScope = sequenceCvGenerator.generateTailoredCv(lifeStory, instructions);

        // Extract result and agenticScope
        AgenticScope agenticScope = bothCvsAndScope.agenticScope();
        Map<String,String> bothCvsAndLifeStory = bothCvsAndScope.result();

        System.out.println("=== USER INFO (input) ===");
        System.out.println(bothCvsAndLifeStory.get("lifeStory"));
        System.out.println("=== MASTER CV TYPED (intermediary variable) ===");
        System.out.println(bothCvsAndLifeStory.get("masterCv"));
        System.out.println("=== TAILORED CV TYPED (output) ===");
        System.out.println(bothCvsAndLifeStory.get("tailoredCv"));

        System.out.println("=== INVOCATION TRACE (all messages in the conversation) ===");
        System.out.println(agenticScope);
        // this will return this object filled out:
        // AgenticScope {
        //     memoryId = "e705028d-e90e-47df-9709-95953e84878c",
        //             state = {
        //                     bothCvsAndLifeStory = { // output
        //                             masterCv = "...",
        //                            lifeStory = "...",
        //                            tailoredCv = "..."
        //                     },
        //                     instructions = "...", // inputs and intermediary variables
        //                     tailoredCv = "...",
        //                     masterCv = "...",
        //                     lifeStory = "..."
        //             }
        // }
        System.out.println("=== INVOCATION TRACE (all messages in the conversation) ===");
        System.out.println(agenticScope.contextAsConversation("tailorCv"));
        // TODO Mario agenticScope.contextAsConversation() seems empty
        // TODO Mario do we have a way to get the call chain?
        //  would be important to be able to trace:
        //  - what agent had which inputs (or what was the variables state)
        //  - which one called which (trace with unique IDs allowing to link one to another)
        // TODO Mario how would it interplay with returntype.IMMEDIATE?

        // TODO Mario
        // We can test the AgenticScope, but also call chain? (hack that through the logs?) Ask cursor for writing a test of invocation order on one of the agents
        //      Invocation Order - Are agents called in the correct sequence? NOT POSSIBLE NOW?
        //      Input/Output Parameters - Do parameters flow correctly between agents (e.g., masterCv output becomes cv input)? POSSIBLE by testing each agent separately
        //      Exit Conditions - Do loops exit when score ≥ 0.8 and respect max iterations? POSSIBLE by testing separate agent
        //      Branching Logic - Does conditional routing work (high score → interview, low score → rejection)? POSSIBLE by building a small system (although call chain would help to prove certain agent was called)
        //      Content Quality - Does output contain expected keywords and avoid irrelevant ones? POSSIBLE, normal testing

        // Both untyped and typed agents give the same result (differences are due to the non-deterministic nature of LLMs)
        // but the typed agent is easier to use and safer because of compile-time type checking

    }
}