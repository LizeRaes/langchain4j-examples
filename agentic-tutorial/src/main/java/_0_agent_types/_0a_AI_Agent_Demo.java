package _0_agent_types;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.model.chat.ChatModel;
import util.ChatModelProvider;
import util.log.CustomLogging;
import util.log.LogLevels;

public class _0a_AI_Agent_Demo {
    static {
        CustomLogging.setLevel(LogLevels.PRETTY, 300);  // control how much you see from the model calls
    }

    private static final ChatModel CHAT_MODEL = ChatModelProvider.createChatModel();

    public static void main(String[] args) {
        System.out.println("=== AI Agent Demo: Meal Planning with Tools ===");

        UserProfile userProfile = new UserProfile(); // prefilled for demo

        MealPlannerAgent mealPlanner = AgenticServices
                .agentBuilder(MealPlannerAgent.class)
                .chatModel(CHAT_MODEL)
                .tools(new NutritionContentRetriever())
                .outputName("meal")
                .build();

        Meal meal = mealPlanner.createMeal(userProfile);

        System.out.println("\nGenerated Meal:");
        System.out.println(meal);
    }
}
