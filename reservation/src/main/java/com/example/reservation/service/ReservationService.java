package com.example.reservation.service;

import com.example.reservation.domain.dto.ReservationRequest;

import java.util.List;
import java.util.Map;

public interface ReservationService {
    void reservationStore(String userName, Long storeId, ReservationRequest dto);

    List<Map<String, Object>> reservationCheck(String userName);

    List<Map<String, Object>> reservationPartnerCheck(String userName, Long storeId, int year, int month, int day);

    void reservationRefusal(String userName, Long id);

    void reservationApproved(String userName, Long id);


    void checkIn(String partnerName, String businessName, String userName);
}
