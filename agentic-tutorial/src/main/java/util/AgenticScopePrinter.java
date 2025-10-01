package util;

import dev.langchain4j.agentic.scope.AgenticScope;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgenticScopePrinter {

    public static String printPretty(AgenticScope agenticScope, int maxChars) {
        if (agenticScope == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"memoryId\": \"").append(agenticScope.memoryId()).append("\",\n");
        sb.append("  \"state\": {\n");

        Map<String, Object> state = agenticScope.state();
        if (state == null || state.isEmpty()) {
            sb.append("    // empty\n");
        } else {
            int count = 0;
            for (Map.Entry<String, Object> entry : state.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (count > 0) {
                    sb.append(",\n");
                }

                sb.append("    \"").append(key).append("\": ");

                if (value == null) {
                    sb.append("null");
                } else {
                    String valueStr = value.toString();
                    if (valueStr.length() <= maxChars) {
                        String escaped = valueStr.replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                                .replace("\n", "\\n")
                                .replace("\r", "\\r")
                                .replace("\t", "\\t");
                        sb.append("\"").append(escaped).append("\"");
                    } else {
                        String truncated = valueStr.substring(0, maxChars);
                        String escaped = truncated.replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                                .replace("\n", "\\n")
                                .replace("\r", "\\r")
                                .replace("\t", "\\t");
                        sb.append("\"").append(escaped).append(" [truncated...]\"");
                    }
                }
                count++;
            }
            sb.append("\n");
        }
        sb.append("  }\n");
        sb.append("}");

        return sb.toString();
    }

    public static String printConversation(String conversation, int maxChars) {
        if (conversation == null || conversation.isEmpty()) {
            return "(empty conversation)";
        }

        String[] parts = conversation.split("(?m)(?=^User:|^\\w+\\s+agent:)"); // <-- fixed
        StringBuilder sb = new StringBuilder();

        Pattern agentPattern = Pattern.compile("^(\\w+)\\s+agent:(.*)$", Pattern.DOTALL);

        for (String part : parts) {
            if (part.trim().isEmpty()) continue;

            Matcher agentMatcher = agentPattern.matcher(part.trim());
            if (agentMatcher.matches()) {
                String agentType = agentMatcher.group(1);
                String content = agentMatcher.group(2).trim();

                sb.append(agentType).append(" agent:");
                if (!content.isEmpty()) {
                    if (content.length() > maxChars) {
                        sb.append(" ").append(content, 0, maxChars).append(" [truncated...]");
                    } else {
                        sb.append(" ").append(content);
                    }
                }
            } else if (part.startsWith("User:")) {
                String content = part.substring(5).trim();
                sb.append("User:");
                if (!content.isEmpty()) {
                    if (content.length() > maxChars) {
                        sb.append(" ").append(content, 0, maxChars).append(" [truncated...]");
                    } else {
                        sb.append(" ").append(content);
                    }
                }
            } else {
                if (part.length() > maxChars) {
                    sb.append(part, 0, maxChars).append(" [truncated...]");
                } else {
                    sb.append(part);
                }
            }
            sb.append("\n\n");
        }

        return sb.toString().trim();
    }

    public static String printAgentInvocations(Object agentInvocations, int maxChars) {
        if (agentInvocations == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        String invocationStr = agentInvocations.toString();
        
        // Handle the case where agentInvocations is a list or array
        if (invocationStr.startsWith("[") && invocationStr.endsWith("]")) {
            // Remove outer brackets and split by AgentInvocation entries
            String content = invocationStr.substring(1, invocationStr.length() - 1);
            
            // Split by AgentInvocation{ pattern, but keep the pattern
            String[] parts = content.split("(?=AgentInvocation\\{)");
            
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i].trim();
                if (part.isEmpty()) continue;
                
                if (i > 0) {
                    sb.append("\n\n");
                }
                
                sb.append("AgentInvocation {\n");
                
                // Extract agentName
                Pattern agentNamePattern = Pattern.compile("agentName=([^,}]+)");
                Matcher agentNameMatcher = agentNamePattern.matcher(part);
                if (agentNameMatcher.find()) {
                    String agentName = agentNameMatcher.group(1);
                    sb.append("  agentName: ").append(agentName).append("\n");
                }
                
                // Extract input
                Pattern inputPattern = Pattern.compile("input=\\{([^}]+(?:\\{[^}]*\\}[^}]*)*)\\}");
                Matcher inputMatcher = inputPattern.matcher(part);
                if (inputMatcher.find()) {
                    String input = inputMatcher.group(1);
                    sb.append("  input: {\n");
                    sb.append(formatMapContent(input, maxChars, "    "));
                    sb.append("\n  }\n");
                }
                
                // Extract output - everything after "output=" until the end of the part
                int outputIndex = part.indexOf("output=");
                if (outputIndex >= 0) {
                    String output = part.substring(outputIndex + 7).trim(); // +7 for "output="
                    sb.append("  output: ");
                    if (output.length() > maxChars) {
                        sb.append(output, 0, maxChars).append(" [truncated...]");
                    } else {
                        sb.append(output);
                    }
                    sb.append("\n");
                }
                
                sb.append("}");
            }
        } else {
            // Handle single invocation or other formats
            if (invocationStr.length() > maxChars) {
                sb.append(invocationStr, 0, maxChars).append(" [truncated...]");
            } else {
                sb.append(invocationStr);
            }
        }
        
        return sb.toString();
    }
    
    private static String formatMapContent(String mapContent, int maxChars, String indent) {
        StringBuilder sb = new StringBuilder();
        
        // Simple approach: find the first = and treat everything before as key, after as value
        int firstEquals = mapContent.indexOf('=');
        if (firstEquals > 0) {
            String key = mapContent.substring(0, firstEquals).trim();
            String value = mapContent.substring(firstEquals + 1).trim();
            
            sb.append(indent).append(key).append(": ");
            
            // For long values, truncate the entire value as a whole
            if (value.length() > maxChars) {
                sb.append(value, 0, maxChars).append(" [truncated...]");
            } else {
                sb.append(value);
            }
        } else {
            // No equals found, treat as single value
            if (mapContent.length() > maxChars) {
                sb.append(indent).append(mapContent, 0, maxChars).append(" [truncated...]");
            } else {
                sb.append(indent).append(mapContent);
            }
        }
        
        return sb.toString();
    }

}