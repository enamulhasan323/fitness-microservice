package com.fitnes.aiService.service;

import com.fitnes.aiService.model.Recommendation;

import java.util.List;

public interface RecommendationService {
    List<Recommendation> getUserRecommendations(String userId);

    Recommendation getActivityRecommendations(String activityId);
}
