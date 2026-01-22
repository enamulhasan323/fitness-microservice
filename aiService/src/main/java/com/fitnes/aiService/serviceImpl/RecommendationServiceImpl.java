package com.fitnes.aiService.serviceImpl;

import com.fitnes.aiService.model.Recommendation;
import com.fitnes.aiService.repository.RecommendationRepository;
import com.fitnes.aiService.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;


    @Override
    public List<Recommendation> getUserRecommendations(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    @Override
    public Recommendation getActivityRecommendations(String activityId) {
        return recommendationRepository.findByActivityId(activityId).orElseThrow(
                () -> new RuntimeException("Recommendation not found for activityId: " + activityId)
        );
    }
}
