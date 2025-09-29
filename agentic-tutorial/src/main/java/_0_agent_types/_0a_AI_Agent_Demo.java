package _0_agent_types;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.model.chat.ChatModel;
import util.ChatModelProvider;
import util.log.CustomLogging;
import util.log.LogLevels;

import java.util.List;

public class _0a_AI_Agent_Demo {
    static {
        CustomLogging.setLevel(LogLevels.INFO, 300);
    }

    private static final ChatModel CHAT_MODEL = ChatModelProvider.createChatModel();

    public static void main(String[] args) {
        System.out.println("=== AI Agent Demo: Meal Planning with Tools ===");

        MealPlannerAgent mealPlanner = AgenticServices
                .agentBuilder(MealPlannerAgent.class)
                .chatModel(CHAT_MODEL)
                .tools(new NutritionContentRetriever())
                .outputName("meal")
                .build();

        UserProfile userProfile = new UserProfile();
        userProfile.favoriteIngredients = List.of("rice", "broccoli", "chicken", "salmon", "eggs", "spinach", "carrots", "potato", "milk");
        userProfile.allergies = List.of("peanuts", "shellfish");
        userProfile.healthGoal = "high protein";

        Meal meal = mealPlanner.createMeal(userProfile);

        System.out.println("\nGenerated Meal:");
        System.out.println(meal);
    }
}
