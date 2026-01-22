package com.fitnes.activityService.serviceImpl;


import com.fitnes.activityService.dto.ActivityRequest;
import com.fitnes.activityService.dto.ActivityResponse;
import com.fitnes.activityService.model.Activity;
import com.fitnes.activityService.repository.ActivityRepository;
import com.fitnes.activityService.service.ActivityService;
import com.fitnes.activityService.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.template.routing-key}")
    private String routingKey;

    @Override
    public ActivityResponse trackActivity(ActivityRequest activityRequest) {

        Boolean isValidUser=userValidationService.validateUser(activityRequest.getUserId());
        if(!isValidUser){
            throw new IllegalArgumentException("Invalid user ID: "+activityRequest.getUserId());
        }
        Activity activity= Activity.builder()
                .userId(activityRequest.getUserId())
                .activityType(activityRequest.getActivityType())
                .duration(activityRequest.getDuration())
                .caloriesBurned(activityRequest.getCaloriesBurned())
                .startTime(activityRequest.getStartTime())
                .additionalMetrics(activityRequest.getAdditionalMetrics())
                .build();
        Activity savedActivity=activityRepository.save(activity);

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);

        } catch (Exception e) {
            log.error("Failed to publish activity in RabbitMq ",e);
        }

        return mapToResponse(savedActivity);
    }

    private ActivityResponse mapToResponse(Activity activity) {
        ActivityResponse response=new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setActivityType(activity.getActivityType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());
        return response;
    }

    @Override
    public List<ActivityResponse> getUserActivities(String userId) {
        List<Activity> activities=activityRepository.findByUserId(userId);
        return activities.stream().map(this::mapToResponse).toList();
    }

    @Override
    public ActivityResponse getUserActivityById(String activityId) {
        Activity activity=activityRepository.findById(activityId).orElse(null);
        if(activity==null){
            return null;
        }
        return mapToResponse(activity);
    }
}
