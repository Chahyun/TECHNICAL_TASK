package com.example.reservation.service;

import com.example.reservation.domain.dto.ReviewRequest;
import com.example.reservation.domain.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    List<ReviewResponse> getReviews(Long storeId);

    void registerReview(String userName, Long reservationId, ReviewRequest dto);

}
