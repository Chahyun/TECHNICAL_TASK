package com.example.reservation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    USERNAME_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 사용자 이름입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "유효하지 않은 비밀번호입니다."),
    NOT_PARTNER(HttpStatus.NON_AUTHORITATIVE_INFORMATION, "파트너가 아닙니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다."),
    STORE_NON_MATCHERS(HttpStatus.FORBIDDEN, "가게와 사용자가 일치하지 않습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."),
    TOO_EARLY(HttpStatus.UNPROCESSABLE_ENTITY, "너무 일찍 도착했습니다."),
    TOO_LATE(HttpStatus.UNPROCESSABLE_ENTITY, "너무 늦게 도착했습니다."),
    ALREADY_VISITED(HttpStatus.CONFLICT, "이미 방문한 곳입니다."),
    NOT_VISITED(HttpStatus.CONFLICT, "방문하지 않은 가게입니다."),
    ALREADY_REVIEW(HttpStatus.CONFLICT, "이미 리뷰를 등록했습니다."),
    NOT_APPROVED(HttpStatus.FORBIDDEN, "승인되지 않은 행동입니다.");

    private HttpStatus httpStatus;
    private String message;

    // 생성자, 게터 등의 코드 생략...
}
