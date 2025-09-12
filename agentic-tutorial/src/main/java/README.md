PLAN:
8) Non-AI agents (first-class “operators” in flows)

Package: _8_non_ai_agents
Why now: You just used them from Supervisor. Make it explicit: an agent can be a plain Java operator.

Files

ScoreAggregator (non-AI, computes CvReview average & feedback merge)

_8_Non_Ai_Agents_Example (main)

Sketch

public class ScoreAggregator {
@Agent(value = "Aggregates HR/Manager/Team reviews into a combined review",
outputName = "combinedCvReview")
public CvReview aggregate(@V("hrReview") CvReview hr,
@V("managerReview") CvReview mgr,
@V("teamMemberReview") CvReview tm) {
double avg = (hr.score + mgr.score + tm.score) / 3.0;
String fb = "HR: " + hr.feedback + "\nManager: " + mgr.feedback + "\nTeam: " + tm.feedback;
return new CvReview(avg, fb);
}
}


Use it inside supervisor and also inside a composed workflow to show interchangeability.

9) Human-in-the-loop agent

Package: _9_human_in_the_loop
Why now: You can show the supervisor noticing missing data and asking a human.

Files

HumanInTheLoopConsole (or HumanInTheLoopUI if you prefer)

_9_HITL_Example (main)

Sketch

HumanInTheLoop hitl = AgenticServices.humanInTheLoopBuilder()
.description("Ask candidate for missing info (e.g., preferred interview timeslot)")
.outputName("timeslot")
.requestWriter(q -> { System.out.println(q); System.out.print("> "); })
.responseReader(() -> System.console().readLine())
.build();

// Add hitl to the supervisor’s subAgents


Demo: Supervisor tries to schedule → lacks timeslot → invokes HITL.

10) Goal-oriented “chatbot” composed agent

Package: _10_goal_oriented_chatbot
Why now: Builds on supervisor & HITL to create a conversational agent that works through goals.

Files

OnboardingChatbot (typed)

_10_Goal_Oriented_Chatbot_Example

Behavior

Goal list: CollectCandidateInfo → ValidateAgainstJD → ScheduleInterview → SendConfirmation.

Use a sequence where each step is itself:

a small supervisor for flexible sub-steps, or

a conditional (if missing(field) ⇒ HITL), or

a loop (refine CV until score ≥ 0.8).

Sketch

public interface OnboardingChatbot {
@Agent String progress(@MemoryId String threadId,
@V("userMessage") String userMessage);
}


Keep memory on to maintain dialogue.

Stop when goalState == DONE (stored in AgenticScope).

11) Persisted agent state (AgenticScope store) + memory

Package: _11_persisted_state
Why now: Your chatbot needs continuity; show registry + custom persistence.

Files

InMemoryScopeStore (trivial), JdbcScopeStore (skeleton)

_11_Persisted_State_Example

Show

AgenticScopePersister.setStore(new JdbcScopeStore(...))

agent.evictAgenticScope(memoryId) when closing a thread.

Memory with @MemoryId on the root agent and summarizedContext for subagents.

12) Error handling & interruptions

Package: _12_error_handling_and_interruptions
Why now: Reliability once things get stateful & interactive.

Files

_12_Error_Handling_Example (missing arg → recover & retry)

_12_Interruptions_Example (simulate tool failure; return fallback result)

Show

errorHandler(ctx -> { ... return ErrorRecoveryResult.retry(); })

Branching on MissingArgumentException vs generic.

Interruption pattern: cancel parallel executor, surface partials, or return a “paused” state written to AgenticScope.

13) Observability: scope & call chain tracing

Package: _13_observability
Why now: After error handling, show how to see what happened.

Files

_13_Observability_Example

Show

agenticScope.contextAsConversation() before passing to an agent.

Logging with CustomLogging.setLevel(LogLevels.VERBOSE, …) and a small pretty printer of the agent call chain (read from scope).

Optional: add a tiny ScopeEventListener (if available) to dump each invocation and outputName mutations.

14) Streaming responses (UX polish)

Package: _14_streaming
Why now: Improves the dev experience for long outputs; self-contained.

Files

StreamingCvGenerator

_14_Streaming_Example

Show

Model parameter enabling response streaming in LangChain4j (use your ChatModelProvider stream variant).

Stream a draft CV while the loop continues to refine in background steps (note: keep within synchronous demo—streaming is about tokens delivery, not background jobs).

15) Guardrails + structured outputs

Package: _15_guardrails_and_structured_outputs
Why now: Safety + deterministic handoffs—perfect after streaming.

Files

SafeEmailAssistant (with content filters / allowlist of actions)

StructuredInterviewPlan (POJO; @Json mapping) produced by an agent.

_15_Guardrails_Example

Show

Guardrail prompt &/or policy wrapper; simple “tool invocation allowlist”.

Structured output (e.g., { date, slot, participants, agenda[] }) to show robust downstream use.

16) A2A integration

Package: _16_a2a_integration
Why now: Capstone—compose local + remote agents.

Files

A2ACreativeWriter (typed interface)

_16_A2A_Example

Show

AgenticServices.a2aBuilder(serverUrl, A2ACreativeWriter.class).outputName("story")

Mix with local StyleEditor in a sequence, then put that sequence under a supervisor to decide when to call remote vs local.


// TODO quick find your topic map here:



- Outline of the agentic-tutorial
- Typed return types -> link to where this is explained
- Typing superagents -> link to where this is explained
- Passing parameters and AgenticScope (dealing with context) -> link to where this is explained
- Aggregting outputs from multiple agents -> link to where this is explained
- Adding tools -> link to where this is explained
- Handling memory in agentic systems -> link to where this is explained
- Chat loops and exit conditions -> link to where this is explained
- Logging -> link to where this is explained
- Interrupting and error handling -> link to where this is explained
- Streaming mode -> link to where this is explained
- JSON mode (or structured output mode?) -> link to where this is explained
- Testing invocation order and single agents -> link to where this is explained
- TODO go through code to see if other FAQ-like points of interest
// TODO remove imports
// TODO an image of the whole system(s) being built with subagents, superagents, inputs, outputs