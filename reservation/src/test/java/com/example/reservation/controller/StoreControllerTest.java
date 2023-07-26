package com.example.reservation.controller;

import com.example.reservation.domain.dto.StoreRegisterRequest;
import com.example.reservation.exception.AppException;
import com.example.reservation.exception.ErrorCode;
import com.example.reservation.service.StoreService;
import com.example.reservation.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest
class StoreControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    StoreService storeService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("가게 등록 성공")
    @WithMockUser
    void register() throws  Exception{
        String userName = "dasdas";
        String businessName = "dcxzcz";
        String address = "dasdsaddd";
        String description = "adadaaaaa";
        String tel = "01011111110";
        Double lat = 127.1234D;
        Double lnt = 32.158D;


        mockMvc.perform(post("/api/v1/store/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userName))
                        .content(objectMapper.writeValueAsBytes(new StoreRegisterRequest(
                                businessName, address, description,tel, lat, lnt))))
                .andDo(print())
                .andExpect(status().isConflict());
    }


    @Test
    @DisplayName("가게 등록 실패 - 파트너 회원 아님")
    @WithMockUser
    void registerFailNotPartner() throws  Exception{
        String userName = "dasdas";
        String businessName = "dcxzcz";
        String address = "dasdsaddd";
        String description = "adadaaaaa";
        String tel = "01011111110";
        Double lat = 127.1234D;
        Double lnt = 32.158D;

        when(storeService.registerStore(any(), any()))
                .thenThrow(new AppException(ErrorCode.NOT_PARTNER, ""));

        mockMvc.perform(post("/api/v1/store/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userName))
                        .content(objectMapper.writeValueAsBytes(new StoreRegisterRequest(
                                businessName, address, description,tel, lat, lnt))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}