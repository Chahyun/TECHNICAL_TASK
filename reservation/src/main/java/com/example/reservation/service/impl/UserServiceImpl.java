package com.example.reservation.service.impl;

import com.example.reservation.domain.dto.UserJoinRequest;
import com.example.reservation.domain.User;
import com.example.reservation.exception.AppException;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.repository.UserRepository;
import com.example.reservation.service.UserService;
import com.example.reservation.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.secret}")
    private String key;

    private Long expireTimeMs = 1000 * 60 * 60L;

    /**
     * 회원가입
     */
    public String join(UserJoinRequest users){
        userRepository.findByUserName(users.getUserName())
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, users.getUserName() + "는 이미 있습니다.");
                });

        userRepository.save(User.builder()
                .userName(users.getUserName())
                .name(users.getName())
                .partnerYn(users.getPartnerYn().equals("1") ? true : false)
                .password(encoder.encode(users.getPassword()))
                .phone(users.getPhone())
                .build());
        return "SUCCESS";
    }

    /**
     * 로그인
     */
    public String login(String userName, String password) {
        // userId 없음
        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, userName + "이 없습니다"));

        // password 틀림
        if(!encoder.matches(password, selectedUser.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "패스워드를 잘못 입력 하셨습니다.");
        }
        // 에러 안나면 토큰 발행
        return JwtTokenUtil.createToken(selectedUser.getUserName(), key, expireTimeMs);
    }
}
