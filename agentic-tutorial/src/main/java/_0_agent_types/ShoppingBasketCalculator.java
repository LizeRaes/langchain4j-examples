package _0_agent_types;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.V;

import java.util.Map;
import java.util.Random;

public class ShoppingBasketCalculator {
    
    private static final Random RANDOM = new Random();
    
    @Agent(description = "calculates total price for ingredients by fetching unit price from the database", outputName = "totalPrice")
    public double calculateTotalPrice(@V("ingredients") Map<String, Double> ingredients) {
        System.out.println("*** ShoppingBasketCalculator was called with input: " + ingredients + " ***");
        
        double total = 0.0;
        for (Map.Entry<String, Double> entry : ingredients.entrySet()) {
            String ingredient = entry.getKey();
            Double quantity = entry.getValue();
            double unitPrice = grabUnitPriceFromDb(ingredient);
            double itemTotal = quantity * unitPrice;
            total += itemTotal;
        }
        return total;
    }
    
    private double grabUnitPriceFromDb(String ingredient) {
        // dummy random price generator for demo
        return 1.0 + RANDOM.nextDouble() * 4.0; // Random price between 1-5 euros
    }
}
