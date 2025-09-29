package _0_agent_types;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.util.List;
import java.util.Map;

public class NutritionContentRetriever {

    // Simple in-memory nutrition database
    private static final Map<String, String> NUTRITION_FACTS = Map.ofEntries(
        Map.entry("chicken", "protein:31g | carbs:0g  | fat:3g  | per 100g"),
        Map.entry("salmon", "protein:20g | carbs:0g  | fat:13g | per 100g"),
        Map.entry("egg", "protein:13g | carbs:1g  | fat:11g | per 100g"),
        Map.entry("broccoli", "protein:3g  | carbs:7g  | fat:0g  | per 100g"),
        Map.entry("spinach", "protein:3g  | carbs:4g  | fat:0g  | per 100g"),
        Map.entry("carrots", "protein:1g  | carbs:10g | fat:0g  | per 100g"),
        Map.entry("potato", "protein:2g  | carbs:17g | fat:0g  | per 100g"),
        Map.entry("brown rice", "protein:7g  | carbs:77g | fat:2g  | per 100g"),
        Map.entry("white rice", "protein:2.7g| carbs:80g | fat:0g  | per 100g"),
        Map.entry("rice", "brown rice: protein:7g  | carbs:77g | fat:2g  | per 100g\nwhite rice: protein:2.7g| carbs:80g | fat:0g  | per 100g"),
        Map.entry("peanut", "protein:25g | carbs:16g | fat:49g | per 100g"),
        Map.entry("peanuts", "protein:25g | carbs:16g | fat:49g | per 100g"),
        Map.entry("nuts", "peanuts: protein:25g | carbs:16g | fat:49g | per 100g")
    );

    @Tool("Retrieves nutrition facts for base ingredients")
    public String getNutritionFacts(@P("list of any number of ingredients you want to know nutritional facts about (expressed simply, eg. 'egg', 'cheese' and NOT 'scrambled eggs', 'old gouda") List<String> ingredients) {
        System.out.println("*** NutritionContentRetriever was called with ingredients: " + ingredients + " ***");
        
        StringBuilder result = new StringBuilder();
        for (String ingredient : ingredients) {
            String nutrition = NUTRITION_FACTS.getOrDefault(ingredient.toLowerCase(), 
                "No nutrition data available");
            result.append(ingredient).append(": ").append(nutrition).append("\n");
        }
        
        String facts = result.toString().trim();
        System.out.println("Retrieved nutrition facts:\n" + facts);
        return facts;
    }
}
