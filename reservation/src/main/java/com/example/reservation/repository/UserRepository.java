package com.example.reservation.repository;


import com.example.reservation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //해당 유저의 아이디로 가져오기
    Optional<User> findByUserName(String userName);
}
