package com.example.reservation.service;

import com.example.reservation.domain.Store;
import com.example.reservation.domain.dto.StoreRegisterRequest;

import java.util.List;

public interface StoreService {

    String registerStore(String userName, StoreRegisterRequest dto);

    List<Store> getAllStoresOrderByPoint();

    List<Store> getAllStoresOrderByDistance(Double lat, Double lnt);

    List<Store> getAllStoresOrderByName();

    Store getStoreById(Long id);

    String updateStore(String userName, Long id, StoreRegisterRequest dto);

    String deleteStore(String userName, Long id);

    List<Store> getStoresByUserName(String username);
}
