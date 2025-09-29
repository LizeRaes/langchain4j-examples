package _0_agent_types;

public class Ingredient {
    public String name;
    public Category category;
    
    public Ingredient(String name, Category category) {
        this.name = name;
        this.category = category;
    }
    
    @Override
    public String toString() {
        return name + " (" + category + ")";
    }
}
