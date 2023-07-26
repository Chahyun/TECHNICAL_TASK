package com.example.reservation.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ReviewResponse {

   private String userName;
   private String description;
   private Double point;
   private LocalDateTime registerDate;

}
