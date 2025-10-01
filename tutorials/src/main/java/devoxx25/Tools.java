package devoxx25;

import dev.langchain4j.agent.tool.ReturnBehavior;
import dev.langchain4j.agent.tool.Tool;

public class Tools {

    @Tool(returnBehavior = ReturnBehavior.IMMEDIATE)
    public int add(int a, int b) {
        System.out.println("--- Tool called: Adding " + a + " and " + b + " ---");
        return a + b;
    }

}
