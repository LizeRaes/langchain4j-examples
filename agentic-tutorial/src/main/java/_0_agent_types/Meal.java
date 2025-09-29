package _0_agent_types;

import java.util.List;

public class Meal {
    public String name;
    public List<String> ingredients;

    @Override
    public String toString() {
        return "Meal{name='" + name + "', ingredients=" + ingredients + "}";
    }
}
