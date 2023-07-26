package com.example.reservation.service.impl;

import com.example.reservation.domain.Reservation;
import com.example.reservation.domain.Review;
import com.example.reservation.domain.Store;
import com.example.reservation.domain.User;
import com.example.reservation.domain.dto.ReviewRequest;
import com.example.reservation.domain.dto.ReviewResponse;
import com.example.reservation.exception.AppException;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.ReviewRepository;
import com.example.reservation.repository.StoreRepository;
import com.example.reservation.repository.UserRepository;
import com.example.reservation.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    /**
     * 가게 리뷰 정보 보기
     */
    @Override
    public List<ReviewResponse> getReviews(Long storeId) {
        List<ReviewResponse> responseReviews = new ArrayList<>();
        List<Review> reviews = reviewRepository.findAllByStoreId(storeId);
        for(Review review : reviews){
            User user =  userRepository.findById(review.getUserId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "해당 유저가 없습니다."));

            ReviewResponse response = ReviewResponse.builder()
                    .userName(user.getUserName())
                    .description(review.getDescription())
                    .point(review.getRated())
                    .registerDate(review.getRegisterDate())
                    .build();

            responseReviews.add(response);
        }

        return responseReviews;
    }

    /**
     * 예약 방문 후 리뷰 등록
     */
    @Override
    public void registerReview(String userName, Long reservationId, ReviewRequest dto) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND, "예약 정보가 없습니다."));

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "해당 유저가 없습니다."));

        if(reservation.getUserId() != user.getId()){
            throw new AppException(ErrorCode.NOT_APPROVED, "허용되지 않는 유저입니다.");
        }
        if(reservation.isReview()){
            throw new AppException(ErrorCode.ALREADY_REVIEW, "이미 리뷰를 등록한 예약정보 입니다.");
        }
        if(!reservation.isVisit()){
            throw new AppException(ErrorCode.NOT_VISITED, "방문하지 않은 가게입니다.");
        }

        Review review = Review.builder()
                .userId(user.getId())
                .storeId(reservation.getStoreId())
                .reservationId(reservationId)
                .description(dto.getDescription())
                .rated(dto.getRated())
                .registerDate(LocalDateTime.now())
                .build();

        reservation.setReview(true);

        Store store = storeRepository.findById(reservation.getStoreId())
                        .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND, "해당 가게를 찾을 수 없습니다."));

        Long cnt = reviewRepository.countByStoreId(store.getId());
        Double updateRated = ((store.getRated()*cnt)+ dto.getRated())/(cnt + 1);
        store.setRated(updateRated);

        reviewRepository.save(review);
        reservationRepository.save(reservation);
        storeRepository.save(store);
    }
}
