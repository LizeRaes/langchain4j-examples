package _0_agent_types;

import java.util.List;

public class UserProfile {
    public List<String> favoriteIngredients;
    public List<String> allergies;
    public String healthGoal;

    public UserProfile(){}

    public UserProfile(List<String> favoriteIngredients, List<String> allergies, String healthGoal) {
        this.favoriteIngredients = favoriteIngredients;
        this.allergies = allergies;
        this.healthGoal = healthGoal;
    }

    @Override
    public String toString() {
        return "User Profile\n" +
                "Favorite Ingredients: " + String.join(", ", favoriteIngredients) + "\n" +
                "Allergies: " + String.join(", ", allergies) + "\n" +
                "Health Goal: " + healthGoal;
    }
}
