package com.example.reservation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StoreRegisterRequest {

    private String businessName;
    private String address;
    private String description;
    private String tel;
    private Double lat;
    private Double lng;

}
