package com.fitnes.aiService.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitnes.aiService.model.Activity;
import com.fitnes.aiService.model.Recommendation;
import com.fitnes.aiService.service.ActivityAiService;
import com.fitnes.aiService.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiServiceImpl implements ActivityAiService {

    private final GeminiService geminiService;

    @Override
    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPrompt(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("AI Response from AI : {}", aiResponse);
        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {
        StringBuilder fullAnalysis;
        List<String> improvements;
        List<String> suggestions;
        List<String> safties;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(aiResponse);
            JsonNode textNode = jsonNode.path("candidates").
                    get(0).
                    path("content").path("parts")
                    .get(0).path("text");
            String jsonContent = textNode.asText()
                    .replace("\\n'''", "")
                    .trim();
            log.info("Parsed  Response from AI : {}", jsonContent);

            JsonNode analysisJson = objectMapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");

            fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "HeartRate:");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "CaloriesBurned:");

            improvements = extractImprovements(analysisJson.path("improvements"));
            suggestions = extractSuggestions(analysisJson.path("suggestions"));
            safties = extractSsafetyGuidelines(analysisJson.path("safetyTips"));
        } catch (Exception e) {
            log.error("Error processing AI response for activity ID {}: {}", activity.getId(), e.getMessage());
            throw new RuntimeException("Failed to process AI response", e);
        }
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getActivityType())
                .recommendation(fullAnalysis.toString().trim())
                .improvements(improvements)
                .suggestions(suggestions)
                .safety(safties)
                .build();
    }

    private List<String> extractSsafetyGuidelines(JsonNode safetyTips) {
        List<String> safetyGuidelines = new java.util.ArrayList<>();
        if (safetyTips.isArray()) {
            safetyTips.forEach(tip -> safetyGuidelines.add(tip.asText()));
            return safetyGuidelines.isEmpty() ? Collections.singletonList("Follow general SafetyGuidelines.") : safetyGuidelines;
        } else {
            return Collections.singletonList("Follow general SafetyGuidelines.");
        }
    }

    private List<String> extractSuggestions(JsonNode suggestionNode) {
        List<String> suggestions = new java.util.ArrayList<>();
        if (suggestionNode.isArray()) {
            suggestionNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String nutrition = suggestion.path("nutrition").asText();
                suggestions.add("Workout: " + workout + " - Nutrition: " + nutrition);
            });
            return suggestions.isEmpty() ? Collections.singletonList("No suggestions provided.") : suggestions;
        } else {
            return Collections.singletonList("No suggestions provided.");
        }
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new java.util.ArrayList<>();
        if ( improvementsNode.isArray() ) {
             improvementsNode.forEach(improvement -> {
                    String area = improvement.path("area").asText();
                    String recommendation = improvement.path("recommendation").asText();
                    improvements.add("Area: " + area + " - Recommendation: " + recommendation);
             });
        }
        return improvements.isEmpty() ? Collections.singletonList("No improvements suggested.") : improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if (!analysisNode.path(key).isMissingNode()) {
            fullAnalysis.append(prefix)
                    .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPrompt(Activity activity) {

        return String.format("""
                Analyze the fitness activity and provide detailed recommendation in the following EXACT JSON format:
                {
                  "analysis": {
                   "overall: "Provide an overall assessment of the activity.",
                   "pace": "Evaluate the pace of the activity and suggest improvements if necessary.",
                   "heartRate": "Analyze heart rate data and provide insights on cardiovascular performance.",
                   "caloriesBurned": "Comment on the calories burned and suggest dietary recommendations if applicable."
                   },
                    "improvements": [{
                    "area": "Specify the area of improvement (e.g., pace, endurance, technique).",}]
                     "recommendation": "Provide a specific recommendation to improve in this area."
                    }],
                    "suggestions":[
                    "workout":"Workout suggestions based on the activity data.",
                    "nutrition":"Nutritional advice to complement the activity."
                    ],
                    "safetyTips": [
                    "List safety tips relevant to the activity performed."]
                  }
                  
                  Analyze the following activity data:
                  Activity Type: %s
                  Duration (minutes): %d
                  Calories Burned: %d
                  Additional Metrics: %s
                  
                  provide the response in JSON format only without any additional text.
                """,
                activity.getActivityType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics());
    }
}
