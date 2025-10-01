package _2_sequential_workflow;

import _1_basic_agent.CvGenerator;
import dev.langchain4j.agentic.declarative.SequenceAgent;
import dev.langchain4j.agentic.declarative.SubAgent;
import dev.langchain4j.service.V;
import domain.Cv;

public interface DeclarativeSequenceAgent {
    @SequenceAgent(outputName = "tailoredCv", description = "Generates tailored CV",
            subAgents = {
                    @SubAgent(type = CvGenerator.class, outputName = "masterCv"),
                    @SubAgent(type = CvTailor.class, outputName = "tailoredCv")
            })
    Cv generateTailoredCv(@V("lifeStory") String lifeStory, @V("instructions") String instructions);
}
