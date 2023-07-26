package com.example.reservation.repository;

import com.example.reservation.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {


    // 거리순으로 가져오기
    @Query(value = "SELECT *, " +
            "6371 * acos(cos(radians(:lat)) * cos(radians(s.lat)) * cos(radians(s.lng) - radians(:lng)) + " +
            "sin(radians(:lat)) * sin(radians(s.lat))) AS distance " +
            "FROM Store s " +
            "ORDER BY distance ASC", nativeQuery = true)
    List<Store> findNearestStores(@Param("lat") Double lat, @Param("lng") Double lng);


    // 평점으로 가져오기
    List<Store> findAllByOrderByRatedDesc();

    // 이름순 으로 가져오기
    List<Store> findAllByOrderByBusinessName();

    // 해당 유저의 가게 리스트 불러오기
    List<Store> findByUserName(String userName);

    // 해당 유저의 원하는 가게 정보 하나 가져오기
    Optional<Store> findByIdAndUserName(Long id, String userName);

    // 해당 유저의 원하는 가게 정보 하나 가져오기
    Optional<Store> findByBusinessNameAndUserName(String businessName, String partnerName);

}
