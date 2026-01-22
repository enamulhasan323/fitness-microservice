package com.fitnes.aiService.service;

import com.fitnes.aiService.model.Activity;

public interface ActivityMessageListenerService {

    void processActivityMessage(Activity activity);
}
