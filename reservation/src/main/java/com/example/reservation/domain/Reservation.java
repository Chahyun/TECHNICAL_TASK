package com.example.reservation.domain;

import com.example.reservation.domain.dto.ReservationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long storeId;
    private String phone;
    private LocalDateTime reservationDate;
    private boolean visit;
    private String status;
    private boolean review;


    public static Reservation dtoToEntity(Long userId, Long storeId, ReservationRequest dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년MM월dd일 HH시mm분");
        return Reservation.builder()
                .userId(userId)
                .storeId(storeId)
                .phone(dto.getPhone())
                .reservationDate(LocalDateTime.parse(dto.getReservationDate(), formatter))
                .status("예약 승인 전")
                .visit(false)
                .build();
    }
}
