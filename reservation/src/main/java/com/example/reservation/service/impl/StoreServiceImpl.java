package com.example.reservation.service.impl;

import com.example.reservation.domain.Store;
import com.example.reservation.domain.dto.StoreRegisterRequest;
import com.example.reservation.exception.AppException;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.repository.StoreRepository;
import com.example.reservation.repository.UserRepository;
import com.example.reservation.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final UserRepository userRepository;

    private final StoreRepository storeRepository;

    /**
     * 가게 등록
     */
    @Override
    public String registerStore(String userName, StoreRegisterRequest dto) {
        if(!userRepository.findByUserName(userName).get().isPartnerYn()){
            throw new AppException(ErrorCode.NOT_PARTNER, userName + "님은 파트너 회원이 아닙니다.");
        }
        storeRepository.save(Store.dtoToEntity(userName,dto));

        return "가게등록 완료";
    }

    /**
     * 별점순
     */
    @Override
    public List<Store> getAllStoresOrderByPoint() {
        List<Store> stores = storeRepository.findAllByOrderByRatedDesc();
        return stores != null ? stores : Collections.emptyList();
    }

    /**
     * 거리순
     */
    @Override
    public List<Store> getAllStoresOrderByDistance(Double lat, Double lnt) {
        List<Store> stores = storeRepository.findNearestStores(lat, lnt);
        return stores != null ? stores : Collections.emptyList();
    }

    /**
     * 가나다순
     */
    @Override
    public List<Store> getAllStoresOrderByName() {
        List<Store> stores = storeRepository.findAllByOrderByBusinessName();
        return stores != null ? stores : Collections.emptyList();
    }

    /**
     * 특정 가게 조회
     */
    @Override
    public Store getStoreById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NOT_FOUND, "가게 정보가 존재하지 않습니다."));
    }

    /**
     * 가게 정보 수정
     */
    @Override
    public String updateStore(String userName, Long id, StoreRegisterRequest dto) {
        Store store = storeRepository.findByIdAndUserName(id, userName)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NON_MATCHERS, "가게 정보가 존재하지 않거나 등록하지 않은 사용자입니다."));
        // Store 객체 업데이트
        store.updateFromDto(dto);
        storeRepository.save(store);

        return "가게 수정 완료";
    }

    /**
     * 가게 삭제
     */
    @Override
    public String deleteStore(String userName,  Long id) {
        storeRepository.findByIdAndUserName(id, userName)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_NON_MATCHERS, "가게 정보가 존재하지 않거나 등록하지 않은 사용자입니다."));
        storeRepository.deleteById(id);
        return "가게 삭제 완료";
    }

    /**
     * 내가 등록한 가게리스트
     */
    @Override
    public List<Store> getStoresByUserName(String userName) {
        List<Store> myStores = storeRepository.findByUserName(userName);
        if (myStores.isEmpty()) {
            throw new AppException(ErrorCode.STORE_NOT_FOUND, "내 아이디로 등록된 가게가 없습니다.");
        }
        return myStores;
    }
}
