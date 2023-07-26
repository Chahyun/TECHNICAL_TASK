package com.example.reservation.repository;

import com.example.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    List<Reservation> findAllByUserId(Long userId); // 해당 유저의 모든 예약 정보 가져오기

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.storeId = :storeId " +
            "AND YEAR(r.reservationDate) = :year " +
            "AND MONTH(r.reservationDate) = :month " +
            "AND DAY(r.reservationDate) = :day")
    List<Reservation> findReservationsByStoreIdAndDate(Long storeId, int year, int month, int day); //원하는 날짜의 예약정보 리스트 불러오기

    Optional<Reservation> findByStoreIdAndUserId(Long storeId, Long userId); // 해당 유저의 예약이 가게와 일치한것을 가져오기
}
