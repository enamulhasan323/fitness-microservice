package com.fitnes.activityService.controller;


import com.fitnes.activityService.dto.ActivityRequest;
import com.fitnes.activityService.dto.ActivityResponse;
import com.fitnes.activityService.service.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {

    private ActivityService activityService;

    @PostMapping("/track-activity")
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest activityRequest) {
        return ResponseEntity.ok(activityService.trackActivity(activityRequest));
    }

    @GetMapping("/get-user-activities")
    public ResponseEntity<List<ActivityResponse>> getUserActivities(@RequestHeader ("X-User-ID") String userId){
        return ResponseEntity.ok(activityService.getUserActivities(userId));
    }

    @GetMapping("/get-user-activityById/{activityId}")
    public ResponseEntity<ActivityResponse> getUserActivityById(@PathVariable String activityId){
        return ResponseEntity.ok(activityService.getUserActivityById(activityId));
    }
}
