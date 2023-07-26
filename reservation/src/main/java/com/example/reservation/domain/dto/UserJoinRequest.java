package com.example.reservation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@AllArgsConstructor
@Data
@Builder
public class UserJoinRequest {
    private String userName;
    private String name;
    private String password;
    private String phone;
    private String partnerYn;
}
