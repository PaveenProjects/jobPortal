package com.jobportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	
//	@Modifying
//    @Query("DELETE FROM RecruiterProfile r WHERE r.userId = :userId")
//    void deleteRecruiterProfileByUserId(@Param("userId") Long userId);
//
//    @Modifying
//    @Query("DELETE FROM JobSeekerProfile j WHERE j.userId = :userId")
//    void deleteJobSeekerProfileByUserId(@Param("userId") Long userId);
//
//    @Modifying
//    @Query("DELETE FROM Users u WHERE u.userId = :userId")
//    void deleteUserByUserId(@Param("userId") Long userId);
//    
    @Modifying
    @Query(value = "DELETE FROM job_seeker_profile WHERE user_account_id = :userId", nativeQuery = true)
    void deleteJobSeekerProfileByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query(value = "DELETE FROM recruiter_profile WHERE user_account_id = :userId", nativeQuery = true)
    void deleteRecruiterProfileByUserId(@Param("userId") Long userId);
    
    @Modifying
    @Query(value = "delete from job_post_activity where posted_by_id=:userId",nativeQuery = true)
    void deleteJobPostActivity(@Param("userId") Long userId);
    
    @Modifying
    @Query(value = "DELETE FROM users WHERE user_id = :userId", nativeQuery = true)
    void deleteUserByUserId(@Param("userId") Long userId);
}
