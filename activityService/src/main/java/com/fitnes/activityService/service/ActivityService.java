package com.fitnes.activityService.service;

import com.fitnes.activityService.dto.ActivityRequest;
import com.fitnes.activityService.dto.ActivityResponse;

import java.util.List;

public interface ActivityService {

    ActivityResponse trackActivity(ActivityRequest activityRequest);
    List<ActivityResponse> getUserActivities(String userId);
    ActivityResponse getUserActivityById(String activityId);
}
