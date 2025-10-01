package _2_sequential_workflow;

import _1_basic_agent.CvGenerator;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.model.chat.ChatModel;
import domain.Cv;
import util.AgenticScopePrinter;
import util.ChatModelProvider;
import util.StringLoader;
import util.log.CustomLogging;
import util.log.LogLevels;

import java.io.IOException;
import java.util.Map;

public class _2c_Declarative_Example {

    static {
        CustomLogging.setLevel(LogLevels.PRETTY, 150);  // control how much you see from the model calls
    }

    // 1. Define the model that will power the agents
    private static final ChatModel CHAT_MODEL = ChatModelProvider.createChatModel();

    public static void main(String[] args) throws IOException {

        // 4. Load the arguments from text files in resources/documents/
        String lifeStory = StringLoader.loadFromResource("/documents/user_life_story.txt");
        String instructions = "Adapt the CV to the job description below." + StringLoader.loadFromResource("/documents/job_description_backend.txt");

        // 5. Build the typed sequence with custom output handling
        DeclarativeSequenceAgent sequenceCvGenerator = AgenticServices
                .createAgenticSystem(DeclarativeSequenceAgent.class, CHAT_MODEL);

        // 6. Call the typed composed agent
        Cv finalCv = sequenceCvGenerator.generateTailoredCv(lifeStory, instructions);

        System.out.println("=== FINAL CV ===");
        System.out.println(finalCv);
    }
}