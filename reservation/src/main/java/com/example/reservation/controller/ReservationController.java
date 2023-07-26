package com.example.reservation.controller;

import com.example.reservation.domain.dto.ReservationRequest;
import com.example.reservation.domain.dto.ReservationUserRequest;
import com.example.reservation.domain.dto.ReviewRequest;
import com.example.reservation.exception.AppException;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.service.ReservationService;
import com.example.reservation.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;
    private final ReviewService reviewService;

    /**
     * 예약 하기
     */
    @PostMapping("/{storeId}")
    public ResponseEntity<String> reservationStore(Authentication authentication,
                                                   @RequestBody ReservationRequest dto,
                                                   @PathVariable Long storeId) {

        reservationService.reservationStore(authentication.getName(), storeId, dto);
        return ResponseEntity.ok().body(String.format("%s님 예약 완료가 되었습니다.", authentication.getName()));
    }

    /**
     * 예약 확인(고객)
     */
    @GetMapping("/check")
    public ResponseEntity<List<Map<String, Object>>> reservationCheck(Authentication authentication) {
        try {
            List<Map<String, Object>> reservationMaps = reservationService.reservationCheck(authentication.getName());
            return new ResponseEntity<>(reservationMaps, HttpStatus.OK);
        } catch (AppException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 예약 확인(파트너)
     * 지정한 날짜 기준으로 데이터를 가져옴
     */
    @GetMapping("/partnerCheck/{storeId} {year}-{month}-{day}")
    public ResponseEntity<List<Map<String, Object>>> reservationPartnerCheck(Authentication authentication,
                                                                             @PathVariable Long storeId,
                                                                             @PathVariable int year,
                                                                             @PathVariable int month,
                                                                             @PathVariable int day) {

        List<Map<String, Object>> reservationMaps = reservationService.reservationPartnerCheck(authentication.getName(), storeId, year, month, day);
        if(reservationMaps.isEmpty()){
            throw  new AppException(ErrorCode.RESERVATION_NOT_FOUND, "해당하는 날짜의 예약이 없습니다.");
        }
        return new ResponseEntity<>(reservationMaps ,HttpStatus.OK);

    }

    /**
     * 예약 승인 및 거부(파트너)
     */
    @PutMapping("/partnerCheck/approved/{id} {status}")
    public ResponseEntity<String> reservationApproved(Authentication authentication,
                                                      @PathVariable Long id,
                                                      @PathVariable Long status) {
        if(status == 2){
            reservationService.reservationRefusal(authentication.getName(), id);
            return ResponseEntity.ok().body("예약이 거부 되었습니다.");
        }
            reservationService.reservationApproved(authentication.getName(),id);
        return ResponseEntity.ok().body("예약이 승인 되었습니다.");
    }

    /**
     * 키오스크 - 가게 도착
     */
    @PutMapping("/checkIn/{businessName}")
    public ResponseEntity<String> reservationApproved(Authentication authentication,
                                                      @PathVariable String businessName,
                                                      @RequestBody ReservationUserRequest dto) {
        System.out.println(dto.getUserName());
        reservationService.checkIn(authentication.getName(), businessName, dto.getUserName());
        return ResponseEntity.ok().body("예약한 곳에 도착 하였습니다.");
    }

    /**
     * 리뷰 등록(고객)
     */
    @PostMapping("/check/review/{reservationId}")
    public ResponseEntity<String> registerReview(Authentication authentication,
                                                 @PathVariable Long reservationId,
                                                 @RequestBody ReviewRequest dto) {

        reviewService.registerReview(authentication.getName(), reservationId, dto);
        return ResponseEntity.ok().body("리뷰가 등록 되었습니다.");
    }
}
