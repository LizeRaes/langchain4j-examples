package _0_agent_types;


import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.V;

import java.util.Map;
import java.util.Random;

public class PriceLookupTool {

    private static final Random RANDOM = new Random();

    @Tool("Looks up the total price of a set of ingredients by fetching their unit prices from the database")
    public double calculateTotalPrice(@V("ingredients") Map<Ingredient, Double> ingredients) {
        System.out.println("*** PriceLookupTool was called with input: " + ingredients + " ***");

        double total = 0.0;
        for (Map.Entry<Ingredient, Double> entry : ingredients.entrySet()) {
            Ingredient ingredient = entry.getKey();
            Double quantity = entry.getValue();
            double unitPrice = grabUnitPriceFromDb(ingredient);
            double itemTotal = quantity * unitPrice;
            total += itemTotal;
        }
        return total;
    }

    private double grabUnitPriceFromDb(Ingredient ingredient) {
        // dummy random price generator for demo (1–5 €/unit)
        return 1.0 + RANDOM.nextDouble() * 4.0;
    }
}
