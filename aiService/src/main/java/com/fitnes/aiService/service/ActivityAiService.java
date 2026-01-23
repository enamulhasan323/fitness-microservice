package com.fitnes.aiService.service;

import com.fitnes.aiService.model.Activity;
import com.fitnes.aiService.model.Recommendation;

public interface ActivityAiService {

    Recommendation generateRecommendation(Activity activity);

}
