package _0_agent_types;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.workflow.HumanInTheLoop;
import util.log.CustomLogging;
import util.log.LogLevels;

import java.util.List;
import java.util.Scanner;

public class _0c_Human_In_The_Loop_Agent_Demo {
    
    static {
        CustomLogging.setLevel(LogLevels.PRETTY, 150);
    }
    
    public static void main(String[] args) {
        System.out.println("=== Human-in-the-Loop Agent Demo ===");
        
        HumanInTheLoop humanValidator = AgenticServices.humanInTheLoopBuilder()
                .description("gathers user's product substitution wishes")
                .inputName("missingIngredients")
                .outputName("userResponse")
                .requestWriter(request -> {
                    System.out.println("Product(s) not available: " + request);
                    System.out.println("Do you want to order something else instead?");
                    System.out.print("> ");
                })
                .responseReader(() -> new Scanner(System.in).nextLine())
                .build();
        
        List<Ingredient> missingIngredients = List.of(
            new Ingredient("Worcestershire Sauce", Category.DRESSING),
            new Ingredient("Speculaaspasta", Category.SPREAD)
        );
         
        String userResponse = (String) humanValidator.askUser(missingIngredients);
        
        System.out.println("\nUser answered: " + userResponse);
    }
}
