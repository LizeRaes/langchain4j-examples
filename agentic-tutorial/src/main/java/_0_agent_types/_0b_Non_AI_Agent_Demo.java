package _0_agent_types;

import java.util.Map;

public class _0b_Non_AI_Agent_Demo {
    
    public static void main(String[] args) {
        System.out.println("=== Non-AI Agent Demo ===");
        
        ShoppingBasketCalculator basketCalculator = new ShoppingBasketCalculator();
        
        Map<String, Double> ingredients = Map.of(
                "tomatoes", 0.8,
                "chicken", 2.5,
                "rice", 1.0
        );
        
        double totalPrice = basketCalculator.calculateTotalPrice(ingredients);
        
        System.out.println("Total price: " + String.format("%.2f", totalPrice) + " €");
    }
}
