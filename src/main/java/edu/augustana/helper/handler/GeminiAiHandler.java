package edu.augustana.helper.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import edu.augustana.dataModel.AIResponse;
import edu.augustana.dataModel.Session;
import io.github.cdimascio.dotenv.Dotenv;
import swiss.ameri.gemini.api.*;
import swiss.ameri.gemini.gson.GsonJsonParser;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Handles interactions with Gemini AI to generate content based on scenarios.
 */
public class GeminiAiHandler {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String API_KEY = dotenv.get("API_KEY");

    private final GenAi genAi;
    private final ConcurrentHashMap<String, Session> sessions;

    public GeminiAiHandler() {
        swiss.ameri.gemini.spi.JsonParser parser = new GsonJsonParser();
        this.genAi = new GenAi(getGeminiApiKey(), parser);
        this.sessions = new ConcurrentHashMap<>();
    }

    public void createSession(String sessionId, String name, String objective) {
        Session session = new Session(sessionId, name, objective);
        sessions.put(sessionId, session);
    }

    public Session getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public AIResponse generateAIResponse(String sessionId, String userInput) {
        Session session = getSession(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session with ID " + sessionId + " does not exist.");
        }

        session.addMessage("User: " + userInput);

        String prompt = createSessionPrompt(session);

        int maxRetries = 2;
        int attempt = 0;

        while (attempt < maxRetries) {
            attempt++;
            try {
                // Generate response from AI
                String jsonResponse = genAi.generateContent(createGenerativeModel(prompt))
                        .thenApply(this::extractResponseText)
                        .get(20, TimeUnit.SECONDS);

                session.addMessage("You: " + jsonResponse);

                // Parse and validate the JSON response
                return parseAIResponse(jsonResponse);

            } catch (JsonSyntaxException e) {
                System.err.println("Attempt " + attempt + " failed with invalid JSON: " + e.getMessage());
                // Retry on invalid JSON
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
                return new AIResponse("Error generating response: " + e.getMessage(), false);
            }
        }

        // If all retries fail, return an error
        System.err.println("All attempts failed. Likely a prompt issue.");
        return new AIResponse("Error: Invalid JSON after " + maxRetries + " attempts. Check your prompt.", false);
    }

    private AIResponse parseAIResponse(String response) throws JsonSyntaxException {
        // Validate required fields
        if (!response.contains("%SEPERATOR%")) {
            throw new JsonSyntaxException("Missing required data: 'message' or 'isObjectiveComplete'");
        }

        String message = response.split("%SEPERATOR")[0].trim();
        boolean isObjectiveComplete = Boolean.parseBoolean(response.split("%SEPERATOR")[1].trim());

        // Return the AIResponse object
        return new AIResponse(message, isObjectiveComplete);
    }

    private String createSessionPrompt(Session session) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Answer in this form: `Write CW-friendly text. Use only comma, dot, and question mark. Be very brief and use word shortcuts. Use common CW wordings if possible, dont send \"User\" or \"You\" I can tell. %SEPERATOR% false if the objective is not 100% complete, true only if the objective is 100% complete.`");
        prompt.append("You are a bot part of a HAM Radio Scenario, I will provide the user's objective while talking to you in this scenario and your name or your position. Do not send messages that will make the user wait, the user must always be expected to respond to your response or leave. Your response must be CW friendly, do not use other special character other than comma, period, and question mark. Before marking the objective as complete, try to engage the user.\n");
        prompt.append("Your objective: ").append(session.getBotObjective());
        prompt.append("Your name: ").append(session.getBotName());
        for (String message : session.getHistory()) {
            prompt.append(message).append("\n");
        }
        return prompt.toString();
    }

    private String extractResponseText(GenAi.GeneratedContent response) {
        return response.text();
    }

    private GenerativeModel createGenerativeModel(String prompt) {
        return GenerativeModel.builder()
                .modelName(ModelVariant.GEMINI_1_5_FLASH)
                .addContent(Content.textContent(Content.Role.USER, prompt))
                .addSafetySetting(SafetySetting.of(
                        SafetySetting.HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT,
                        SafetySetting.HarmBlockThreshold.BLOCK_ONLY_HIGH
                ))
                .generationConfig(GenerationConfig.builder().responseMimeType("text/plain").build())
                .build();
    }

    private static String getGeminiApiKey() {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException("API_KEY is not set. Please provide a valid API key.");
        }
        return API_KEY;
    }

    public void close() {
        genAi.close();
    }
}
