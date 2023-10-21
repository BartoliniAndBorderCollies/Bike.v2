package com.klodnicki.Bike.v2.rest.controller;

import com.klodnicki.Bike.v2.model.entity.Feedback;
import com.klodnicki.Bike.v2.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFeedback(@RequestBody Feedback feedback) {
        return feedbackService.addFeedback(feedback);
    }
}