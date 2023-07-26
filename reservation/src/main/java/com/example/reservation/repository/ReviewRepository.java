package com.example.reservation.repository;

import com.example.reservation.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByStoreId(Long storeId); // 해당 가게 모든 리뷰 불러오기

    Long countByStoreId(Long storeId); // 해당 가게 모든 리뷰 갯수 가져오기
}
