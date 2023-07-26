package com.example.reservation.controller;



import com.example.reservation.domain.Store;
import com.example.reservation.domain.dto.ReviewResponse;
import com.example.reservation.domain.dto.StoreRegisterRequest;
import com.example.reservation.exception.AppException;
import com.example.reservation.service.ReviewService;
import com.example.reservation.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
@Slf4j
public class StoreController {

    private final StoreService storeService;
    private final ReviewService reviewService;

    /**
     * 가게 등록
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerStore(Authentication authentication,
                                                @RequestBody StoreRegisterRequest dto){
        storeService.registerStore(authentication.getName(), dto);
        return ResponseEntity.ok().body(String.format("%s님의 가게 등록이 완료 되었습니다.", authentication.getName()));
    }

    /**
     * 가게 조회
     * 별점순
     * 거리순
     * 가나다순
     */
    @GetMapping()
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Store>> getAllStores(
            @RequestParam(value = "orderBy", required = false, defaultValue = "name") String orderBy,
            @RequestParam(value = "lat", required = false) Double lat,
            @RequestParam(value = "lng", required = false) Double lng
    ) {
        List<Store> storeList;
        // 별점순
        if (orderBy.equalsIgnoreCase("point")) {
            storeList = storeService.getAllStoresOrderByPoint();
        }
        // 거리순
        else if (orderBy.equalsIgnoreCase("distance") && lat != null && lng != null) {
            storeList = storeService.getAllStoresOrderByDistance(lat, lng);
        }
        //가나다순
        else {
            storeList = storeService.getAllStoresOrderByName();
        }

        return new ResponseEntity<>(storeList, HttpStatus.OK);
    }


    /**
     * 특정 id가게 상세정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<Store> getStoreById(@PathVariable Long id) {
        try {
            Store store = storeService.getStoreById(id);
            return new ResponseEntity<>(store, HttpStatus.OK);
        } catch (AppException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    /**
     * 내 아이디로 등록된 가게들 조회
     */
    @GetMapping("/my-stores")
    public ResponseEntity<List<Store>> getMyStores(Authentication authentication) {
        try {
            List<Store> myStores = storeService.getStoresByUserName(authentication.getName());
            return new ResponseEntity<>(myStores, HttpStatus.OK);
        } catch (AppException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 특정 id가게 상세정보
     */
    @GetMapping("/my-stores/{id}")
    public ResponseEntity<Store> myStoreById(@PathVariable Long id) {
        try {
            Store store = storeService.getStoreById(id);
            return new ResponseEntity<>(store, HttpStatus.OK);
        } catch (AppException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 가게 정보 수정
     */
    @PutMapping("/my-stores/{id}")
    public ResponseEntity<String> updateStore(
            Authentication authentication
            ,@PathVariable Long id,
            @RequestBody StoreRegisterRequest dto) {
        storeService.updateStore(authentication.getName(), id, dto);
        return ResponseEntity.ok().body("가게 정보가 수정되었습니다.");
    }

    /**
     * 가게 삭제
     */
    @DeleteMapping("/my-stores/{id}")
    public ResponseEntity<String> deleteStore(
            Authentication authentication,
            @PathVariable Long id) {
        storeService.deleteStore(authentication.getName(), id);
        return ResponseEntity.ok().body("가게 정보가 삭제되었습니다.");
    }


    /**
     * 특정 id가게 리뷰보기
     */
    @GetMapping("/{id}/review")
    public ResponseEntity<List<ReviewResponse>> getStoreByIdReview(@PathVariable Long id) {
        return new ResponseEntity<>(reviewService.getReviews(id),HttpStatus.OK);
    }

}
