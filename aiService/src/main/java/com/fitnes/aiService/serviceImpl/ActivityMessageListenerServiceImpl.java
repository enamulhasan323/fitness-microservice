package com.fitnes.aiService.serviceImpl;

import com.fitnes.aiService.model.Activity;
import com.fitnes.aiService.service.ActivityMessageListenerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityMessageListenerServiceImpl implements ActivityMessageListenerService {


    @RabbitListener(queues = "activity.queue")
    @Override
    public void processActivityMessage(Activity activity) {
        log.info("Received activity message: {}", activity);
        // Add your message processing logic here
    }
}
