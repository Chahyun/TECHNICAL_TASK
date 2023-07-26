package com.example.reservation.service;

import com.example.reservation.domain.dto.UserJoinRequest;


public interface UserService {

    /**
     * 회원가입
     */
    String join(UserJoinRequest dto);

    String login(String userId, String password);
}
