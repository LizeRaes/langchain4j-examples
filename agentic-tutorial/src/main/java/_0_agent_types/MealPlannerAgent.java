package _0_agent_types;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface MealPlannerAgent {

    @Agent("""
        Plans a meal based on user preferences and health goals.
        """)
    @SystemMessage("""
        Based on user preferences and health goals, propose one balanced meal with max. 4 ingredients.
        You have a tool available to look up nutritional information about ingredients (ask in one call about ALL the user's favorite ingredients).
        """)
    @UserMessage("""
        Create a personalized meal based on this user profile:
        User Profile: {{userProfile}}
        """)
    Meal createMeal(@V("userProfile") UserProfile userProfile);
}
