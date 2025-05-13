package com.aja.ott.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aja.ott.entity.TrialUser;
import com.aja.ott.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    boolean existsByPhone(String phone);
    List<User> findByIsDeletedFalse();
    List<User> findByRoleAndIsDeletedFalse(String role);
}
