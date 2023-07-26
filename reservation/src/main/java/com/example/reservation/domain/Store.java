package com.example.reservation.domain;

import com.example.reservation.domain.dto.StoreRegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String businessName;
    private String address;
    private String description;
    private String tel;
    private Double lat;
    private Double lng;
    private Double rated;
    private LocalDateTime registerDate;


    /**
     *
     * 입력 받은 dto를 Entity로 바꿔줌
     */
    public static Store dtoToEntity(String userName, StoreRegisterRequest dto) {
        return Store.builder()
                .userName(userName)
                .businessName(dto.getBusinessName())
                .address(dto.getAddress())
                .description(dto.getDescription())
                .tel(dto.getTel())
                .lat(dto.getLat())
                .lng(dto.getLng())
                .rated(0.0D)
                .registerDate(LocalDateTime.now())
                .build();
    }

    /**
     * 수정사항이 있을때 업데이트
     */
    public void updateFromDto(StoreRegisterRequest dto) {
        if (dto.getBusinessName() != null) {
            this.businessName = dto.getBusinessName();
        }
        if (dto.getAddress() != null) {
            this.address = dto.getAddress();
        }
        if (dto.getDescription() != null) {
            this.description = dto.getDescription();
        }
        if (dto.getTel() != null) {
            this.tel = dto.getTel();
        }
        if (dto.getLat() != null) {
            this.lat = dto.getLat();
        }
        if (dto.getLng() != null) {
            this.lng = dto.getLng();
        }
    }

}
