package com.example.reservation.service.impl;

import com.example.reservation.domain.Reservation;
import com.example.reservation.domain.Store;
import com.example.reservation.domain.User;
import com.example.reservation.domain.dto.ReservationRequest;
import com.example.reservation.exception.AppException;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.repository.ReservationRepository;
import com.example.reservation.repository.StoreRepository;
import com.example.reservation.repository.UserRepository;
import com.example.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository ;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    /**
     * 예약 하기
     */
    @Override
    public void reservationStore(String userName, Long storeId, ReservationRequest dto) {
        User user = findUser(userName);
        storeRepository.findById(storeId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "가게 정보를 찾을 수 없습니다."));

        Reservation reservation = Reservation.dtoToEntity(user.getId(), storeId, dto);
        reservationRepository.save(reservation);
    }


    /**
     * 예약 확인(고객)
     */
    @Override
    public List<Map<String, Object>> reservationCheck(String username) {
        User user = findUser(username);

        List<Map<String, Object>> maps = new ArrayList<>();
        List<Reservation> reservations = reservationRepository.findAllByUserId(user.getId());
        for(Reservation reservation : reservations){
            //원하는 날짜 형식으로 바꿔 주기
            String convertedDate = reservation.getReservationDate().format(
                    DateTimeFormatter.ofPattern("yyyy년MM월dd일 HH시mm분")
            );

            Optional<Store> optionalStore = storeRepository.findById(reservation.getStoreId());
            if(optionalStore.isEmpty()){
                continue;
            }
            Map<String, Object> reservationMap = new HashMap<>();
            reservationMap.put("businessName", optionalStore.get().getBusinessName());
            reservationMap.put("businessPhone", optionalStore.get().getTel());
            reservationMap.put("address", optionalStore.get().getAddress());
            reservationMap.put("username", username);
            reservationMap.put("userPhone",reservation.getPhone());
            reservationMap.put("reservationDate",convertedDate);
            reservationMap.put("status", reservation.getStatus());
            reservationMap.put("visit", reservation.isVisit());
            maps.add(reservationMap);
        }
        return maps;
    }

    /**
     * 예약 확인(파트너)
     */
    @Override
    public List<Map<String, Object>> reservationPartnerCheck(String userName, Long storeId, int year, int month, int day) {
        List<Map<String, Object>> maps = new ArrayList<>();
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "가게를 찾을 수 없습니다."));

        List<Reservation> reservations = reservationRepository.findReservationsByStoreIdAndDate(storeId, year, month, day);
        for(Reservation reservation : reservations){
            String convertedDate = reservation.getReservationDate().format(
                    DateTimeFormatter.ofPattern("yyyy년MM월dd일 HH시mm분")
            );

            Optional<User> optionalUser = userRepository.findById(reservation.getUserId());
            if (optionalUser.isPresent()) {
                Map<String, Object> reservationMap = new HashMap<>();
                reservationMap.put("businessName", store.getBusinessName());
                reservationMap.put("businessPhone", store.getTel());
                reservationMap.put("address", store.getAddress());
                reservationMap.put("username", optionalUser.get().getUserName());
                reservationMap.put("userPhone", reservation.getPhone());
                reservationMap.put("reservationDate", convertedDate);
                reservationMap.put("status", reservation.getStatus());
                reservationMap.put("visit", reservation.isVisit());
                maps.add(reservationMap);
            }
        }
        return maps;
    }

    /**
     * 예약 승인(파트너)
     */
    @Override
    public void reservationRefusal(String username, Long id) {
        Reservation reservation = partnerStoreMatchers(username, id);
        reservation.setStatus("예약 거부");
        reservationRepository.save(reservation);
    }

    /**
     * 예약 거부(파트너)
     */
    @Override
    public void reservationApproved(String username, Long id) {
        Reservation reservation = partnerStoreMatchers(username, id);
        reservation.setStatus("예약 승인");
        reservationRepository.save(reservation);
    }

    @Override
    public void checkIn(String partnerName, String businessName, String username) {
        Store store = storeRepository.findByBusinessNameAndUserName(businessName, partnerName)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND, "가게가 없습니다."));

        User user = findUser(username);
        Reservation reservation = reservationRepository.findByStoreIdAndUserId(store.getId(), user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND, "예약이 없습니다"));
        String status = reservation.getStatus();

        // 가게 주인이 승인을 하지 않았을때,
        if(!status.equals("예약 승인")){
            throw new AppException(ErrorCode.NOT_APPROVED, "승인되지 않은 예약입니다.");
        }
        // 이미 방문처리 되었을 때
        if(reservation.isVisit()){
            throw new AppException(ErrorCode.ALREADY_VISITED, "이미 방문 처리 되었습니다.");
        }
        Duration duration = Duration.between(LocalDateTime.now(), reservation.getReservationDate());

        if(Math.abs(duration.toMinutes()) > 10){
            if(duration.toMinutes() < 0) { // 예약 시간보다 10분이상 늦었을 때
                throw new AppException(ErrorCode.TOO_LATE,"너무 늦게 오셨습니다.");
            }
            // 예약 시간보다 10분이상 일찍 왔을 때
            throw new AppException(ErrorCode.TOO_EARLY, "너무 일찍 오셨습니다.");
        }
        reservation.setVisit(true);
        reservationRepository.save(reservation);
    }


    /**
     * 예약과 예약된 가게 찾기 메서드
     */
    Reservation partnerStoreMatchers(String userName, Long id){
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND, "예약이 없습니다."));

        Store store = storeRepository.findById(reservation.getStoreId())
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND, "가게가 없습니다."));

        // 가게에 저장된 유저와 요청하는 유저가 일치하지 않을 때
        if(!store.getUserName().equals(userName)){
            throw new AppException(ErrorCode.STORE_NON_MATCHERS, "사용자의 가게가 아닙니다.");
        }
        return reservation;
    }

    /**
     * 유저 찾기 메서드
     */
    User findUser(String userName){
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        return user;
    }
}
