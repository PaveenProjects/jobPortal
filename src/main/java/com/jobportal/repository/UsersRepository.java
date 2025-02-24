package com.jobportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jobportal.entity.Users;

import jakarta.transaction.Transactional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);

	Optional<Users> findByUserId(Long id);

	void deleteByUserId(Long id);

	@Modifying
    @Transactional
    @Query(value = "UPDATE users SET password = ?1 WHERE user_id = ?2", nativeQuery = true)
    void changePassword(String encodedPassword, int id);
}
