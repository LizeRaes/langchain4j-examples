package _9_human_in_the_loop;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.io.Console;

public interface HumanInputAgent {
    
    @Agent("Asks human for input via console interaction")
    @SystemMessage("""
            You are a human input agent that asks for user confirmation.
            When you receive a proposal, you should:
            1. Display the proposal clearly
            2. Ask the user for a yes/no response
            3. Return the user's response
            
            Be friendly and professional in your interaction.
            """)
    @UserMessage("""
            Please ask the user to confirm or reject this proposal: {{proposal}}
            
            Ask for a yes/no response and return their answer.
            """)
    String askForConfirmation(@V("proposal") String proposal);
    
    /**
     * Helper method to get human input from console
     */
    default String getHumanInput(String prompt) {
        System.out.println("\n" + prompt);
        System.out.print("Your response (yes/no): ");
        
        try {
            Console console = System.console();
            if (console != null) {
                return console.readLine();
            } else {
                // Fallback for IDEs that don't support console input
                System.out.println("(Console input not available, using 'no' to continue demo)");
                return "no";
            }
        } catch (Exception e) {
            System.out.println("(Error reading console input, using 'no' to continue demo)");
            return "no";
        }
    }
}
